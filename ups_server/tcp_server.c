/* 
 * Author: Petr Kozler
 */

#include "tcp_server.h"
#include "tcp_server_control.h"
#include "config.h"
#include "player.h"
#include "game.h"
#include "console.h"
#include "logger.h"
#include "printer.h"
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
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

int create_serversocket(int32_t host, int32_t port, int32_t queue_length) {
    int srv_sock;
    struct sockaddr_in srv_addr;

    // vytvoření TCP socketu
    srv_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (srv_sock < 0) {
        print_err("Chyba při vytváření socketu serveru");
    }
    else {
        print_out("Socket serveru byl úspěšně vytvořen");
    }

    // nastavení znovupoužití adresy
    int optval = 1;
    
    if (setsockopt(srv_sock, SOL_SOCKET, SO_REUSEADDR, &optval,
            sizeof(optval)) < 0) {
        print_err("Chyba při nastavování znovupoužití adresy");
    }
    else {
        print_out("Znovupoužití adresy bylo úspěšně nastaveno");
    }

    // naplnění struktury adresy
    memset(&srv_addr, 0, sizeof(srv_addr));
    srv_addr.sin_family = AF_INET;
    srv_addr.sin_port = htons(port);
    srv_addr.sin_addr.s_addr = htonl(host);

    // provázání socketu s adresou
    if (bind(srv_sock, (struct sockaddr*) &srv_addr, sizeof(srv_addr)) < 0) {
        print_err("Chyba při propojování socketu serveru s adresou pro naslouchání");
    }
    else {
        print_out("Socket serveru byl úspěšně propojen s adresou pro naslouchání");
    }

    // nastavení režimu čekání na příchozí spojení
    if (listen(srv_sock, queue_length) < 0) {
        print_err("Chyba při spouštění naslouchání příchozím spojením");
    }
    else {
        char *ip_str = inet_ntoa(srv_addr.sin_addr);
        int port_no = (int) htons(srv_addr.sin_port);
        print_out("Naslouchání příchozím spojením na adrese %s:%d spuštěno",
                ip_str, port_no);
    }

    return srv_sock;
}

int accept_socket(int srv_sock) {
    int sock;
    struct sockaddr_in addr;
    socklen_t addr_len;
    addr_len = sizeof(addr);

    do {
        sock = accept(srv_sock, (struct sockaddr *) &addr, &addr_len);
        
        if (sock < 0) {
            print_err("Chyba při navazování spojení s klientem");
            
            continue;
        }
        
        char *ip_str = inet_ntoa(addr.sin_addr);
        int port_no = (int) htons(addr.sin_port);
        print_out("Spojení s klientem na adrese %s:%d navázáno",
            ip_str, port_no);
        
        struct timeval timeout;
        timeout.tv_sec = SOCKET_TIMEOUT_SEC;
        timeout.tv_usec = 0;

        if (setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout,
                    sizeof(timeout)) < 0) {
            print_err("Chyba při nastavování timeoutu pro příjem zpráv");
            close(sock);
            sock = -1;
            
            continue;
        }
        
        print_out("Timeout pro příjem zpráv byl úspěšně nastaven na %d sekundy",
                SOCKET_TIMEOUT_SEC);
    }
    while (sock < 0);
    
    return sock;
}

int setup_connection(int32_t host, int32_t port, char *log_file, int32_t queue_length) {
    clear_stats();
    start_server();
    
    start_logging(log_file);
    g_client_list = create_linked_list();
    create_player_list();
    create_game_list();
    int srv_sock = create_serversocket(host, port, queue_length);
    start_prompt();
    
    print_out("Server spuštěn");
    return srv_sock;
}

void run_connection(int srv_sock) {
    while (is_server_running()) {
        int cli_sock = accept_socket(srv_sock);
        
        /*if (cli_sock < 0) {
            continue;
        }*/
        
        create_player(cli_sock);
    }
}

void shutdown_connection(int srv_sock) {
    close(srv_sock);
    delete_game_list();
    delete_player_list();
    delete_linked_list(g_client_list, delete_player);
    shutdown_logging();
    
    printf("Server ukončen.\n");
    print_stats();
}

void initialize(args_t args) {
    g_server_info.args.host = args.host;
    g_server_info.args.port = args.port;
    g_server_info.args.log_file = args.log_file;
    g_server_info.args.queue_length = args.queue_length;
    
    while (true) {
        int srv_sock = setup_connection(g_server_info.args.host, g_server_info.args.port,
                g_server_info.args.log_file, g_server_info.args.queue_length);
        run_connection(srv_sock);
        shutdown_connection(srv_sock);
    }
}
