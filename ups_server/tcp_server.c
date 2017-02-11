/* 
 * Modul tcp_server definuje funkce pro inicializaci datových struktur
 * serveru a spuštění navazování spojení s klienty.
 * 
 * Author: Petr Kozler
 */

#include "tcp_server.h"
#include "server_control.h"
#include "config.h"
#include "player.h"
#include "game.h"
#include "prompt.h"
#include "logger.h"
#include "broadcaster.h"
#include "game_list.h"
#include "player_list.h"
#include "global.h"
#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

/**
 * Vytvoří strukturu adresy serveru z předaných parametrů.
 * 
 * @param host IP adresa pro naslouchání
 * @param port číslo portu pro naslouchání
 * @return struktura adresy
 */
struct sockaddr_in parse_addr(char *host, int32_t port) {
    struct sockaddr_in srv_addr;
    
    // naplnění struktury adresy
    memset(&srv_addr, 0, sizeof(srv_addr));
    srv_addr.sin_family = AF_INET;
    srv_addr.sin_port = htons((short) port);
    
    if (inet_aton(host, &srv_addr.sin_addr)) {
        return srv_addr;
    }
    
    printf("Neplatná IP adresa pro naslouchání, bude použita výchozí hodnota\n");
    
    if (inet_aton(DEFAULT_HOST, &srv_addr.sin_addr)) {
        return srv_addr;
    }
    
    printf("Nelze použít výchozí IP adresu %s\n", DEFAULT_HOST);
    
    exit(EXIT_FAILURE);
}

/**
 * Vytvoří řetězeczovou reprezentaci zadané adresy.
 * 
 * @param addr struktura adresy
 * @return řetězcová reprezentace
 */
char* build_string_from_addr(struct sockaddr_in addr) {
    const int max_ip_len = (4 * 3 + 3 * 1) + (1 + 5) + 1;
    char *str = (char *) calloc(max_ip_len , sizeof(char));
    int port_no = (int) htons(addr.sin_port);
    
    if (ntohl(addr.sin_addr.s_addr) == INADDR_ANY) {
        snprintf(str, max_ip_len, "INADDR_ANY:%d", port_no);
    }
    else {
        char *ip_str = inet_ntoa(addr.sin_addr);
        snprintf(str, max_ip_len, "%s:%d", ip_str, port_no);
    }
    
    return str;
}

/**
 * Vytvoří socket serveru.
 * 
 * @param srv_addr struktura adresy serveru
 * @return deskriptor socketu serveru
 */
int create_serversocket(struct sockaddr_in srv_addr) {
    int srv_sock;
    
    // vytvoření TCP socketu
    srv_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (srv_sock < 0) {
        return -1;
    }
    
    struct timeval timeout;      
    timeout.tv_sec = SOCKET_TIMEOUT_SEC;
    timeout.tv_usec = 0;

    // nastavení timeoutu čekání na spojení
    if (setsockopt(srv_sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        return -1;
    }

    // nastavení znovupoužití adresy
    int optval = 1;
    
    if (setsockopt(srv_sock, SOL_SOCKET, SO_REUSEADDR, &optval,
            sizeof(optval)) < 0) {
        return -1;
    }
    
    // provázání socketu s adresou
    if (bind(srv_sock, (struct sockaddr*) &srv_addr, sizeof(srv_addr)) < 0) {
        return -1;
    }

    // nastavení režimu čekání na příchozí spojení
    if (listen(srv_sock, QUEUE_LENGTH) < 0) {
        return -1;
    }
    
    char *addr_str = build_string_from_addr(srv_addr);
    print_out("Spuštěno naslouchání na adrese \"%s\", číslo socketu: %d",
        addr_str, srv_sock);
    free(addr_str);

    return srv_sock;
}

/**
 * Vytvoří socket klienta.
 * 
 * @param srv_sock deskriptor socketu serveru
 * @return deskriptor socketu klienta
 */
int accept_socket(int srv_sock) {
    int sock;
    struct sockaddr_in addr;
    socklen_t addr_len;
    addr_len = sizeof(addr);

    // příjem TCP socketu
    sock = accept(srv_sock, (struct sockaddr *) &addr, &addr_len);

    if (sock < 0) {
        return -1;
    }

    struct timeval timeout;
    timeout.tv_sec = SOCKET_TIMEOUT_SEC;
    timeout.tv_usec = 0;

    // nastavení timeoutu pro příjem zpráv
    if (setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        return -1;
    }

    char *addr_str = build_string_from_addr(addr);
    print_out("Příchozí spojení na adrese \"%s\"", addr_str);
    free(addr_str);

    return sock;
}

/**
 * Inicializuje datové struktury serveru a spustí řídící vlákna.
 * 
 * @param host adresa pro naslouchání
 * @param port port pro naslouchání
 * @return deskriptor socketu serveru
 */
int setup_connection(char *host, int32_t port) {
    struct sockaddr_in srv_addr = parse_addr(host, port);
    int srv_sock = create_serversocket(srv_addr);
    
    if (srv_sock < 0) {
        print_err("Chyba při vytváření socketu serveru");
        
        return -1;
    }
    
    clear_stats();
    start_server();
    g_client_list = create_linked_list();
    create_player_list();
    create_game_list();
    
    print_out("Server spuštěn - číslo socketu: %d", srv_sock);
    start_prompt();
    
    return srv_sock;
}

/**
 * Provádí hlavní cyklus programu pro navazování spojení s klienty.
 * 
 * @param srv_sock deskriptor socketu serveru
 */
void run_connection(int srv_sock) {
    while (is_server_running()) {
        int cli_sock = accept_socket(srv_sock);
        
        if (cli_sock < 0) {
            continue;
        }
        
        print_out("Klient %d: Připojen", cli_sock);
        inc_stats_connections_established();
        player_t *player = create_player(cli_sock);
        add_element(g_client_list, player);
    }
}

/**
 * Uvolní paměť datových struktur serveru a ukončí řídící vlákna.
 * 
 * @param srv_sock deskriptor socketu serveru
 */
void shutdown_connection(int srv_sock) {
    close(srv_sock);
    delete_game_list();
    delete_player_list();
    delete_linked_list(g_client_list, (dispose_func_t) delete_player);
    print_out("Server ukončen - číslo socketu: %d", srv_sock);
}

/**
 * Spustí hlavní cyklus programu se zadanými argumenty.
 * 
 * @param args struktura argumentů příkazové řádky
 * @return výsledek běhu
 */
int initialize(args_t args) {
    g_server_info.args.host = args.host;
    g_server_info.args.port = args.port;
    int srv_sock = setup_connection(g_server_info.args.host, g_server_info.args.port);
    
    if (srv_sock < 0) {
        return -1;
    }
    
    run_connection(srv_sock);
    shutdown_connection(srv_sock);
    
    return 0;
}
