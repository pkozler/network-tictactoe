/* 
 * Author: Petr Kozler
 */

#include "connection.h"
#include "connection_status.h"
#include "config.h"
#include "observed_list.h"
#include "player.h"
#include "game.h"
#include "console.h"
#include "logger.h"
#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
#include <err.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

void create_serversocket(int32_t host, int32_t port) {
    int srv_sock;
    struct sockaddr_in srv_addr;

    // vytvoření TCP socketu
    srv_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (srv_sock < 0) {
        die("Chyba při vytváření TCP socketu serveru");
    }

    struct timeval timeout;
    timeout.tv_sec = SOCKET_TIMEOUT_SEC;
    timeout.tv_usec = 0;

    if (setsockopt (srv_sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        die("Chyba při nastavování timeoutu pro příjem zpráv");
    }

    if (setsockopt (srv_sock, SOL_SOCKET, SO_SNDTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        die("Chyba při nastavování timeoutu pro odesílání zpráv");
    }

    // nastavení znovupoužití adresy
    int optval = 1;
    setsockopt(srv_sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval));

    // naplnění struktury adresy
    memset(&srv_addr, 0, sizeof(srv_addr));
    srv_addr.sin_family = AF_INET;
    srv_addr.sin_port = htons(port);
    srv_addr.sin_addr.s_addr = htonl(host);

    // provázání socketu s adresou
    if (bind(srv_sock, (struct sockaddr*) &srv_addr, sizeof(srv_addr)) < 0) {
        die("Chyba při propojování socketu serveru s IP adresou");
    }

    // nastavení režimu čekání na příchozí spojení
    if (listen(srv_sock, QUEUE_LEN) < 0) {
        die("Chyba při spouštění poslechu příchozích spojení");
    }

    return srv_sock;
}

void accept_socket(int srv_sock) {
    int sock;
    struct sockaddr_in addr;
    unsigned int addr_len;

    // přijetí příchozího spojení
    addr_len = sizeof(addr);

    do {
        sock = accept(srv_sock, (struct sockaddr *) &addr, &addr_len);
    }
    while (sock < 0);

    log("Příchozí spojení %s:%i\n", inet_ntoa(addr.sin_addr), ntohs(addr.sin_port));

    return sock;
}

void clear_stats() {
    g_summary.bytes_transferred = 0;
    g_summary.connections_established = 0;
    g_summary.messages_transferred = 0;
    g_summary.transfers_failed = 0;
    g_summary.start_time.tv_sec = 0;
    g_summary.start_time.tv_usec = 0;
}

void send_restart_warning() {
    message_t **messages = (message_t **) malloc(sizeof(message_t *));
    // messages[0] = create_message() - TODO vytvořit zprávu !!!
    send_to_all_clients(1, messages);
    free(messages[0]);
    free(messages);
}

int setup_connection(int32_t host, int32_t port, char *log_file_name) {
    // TODO refaktorovat
    clear_stats();
    g_running = true;
    
    start_logging(log_file_name);
    start_prompt();
    
    int srv_sock = create_serversocket(host, port);
    
    printf("Server spuštěn.\n");
    
    return srv_sock;
}

void run_connection(int srv_sock) {
    // uložení času spuštění
    gettimeofday(&(g_summary.start_time));
    
    while (g_running) {
        int cli_sock = accept_socket(srv_sock);
        create_player(cli_sock);
    }
}

void shutdown_connection(int srv_sock) {
    // TODO rozeslat zpravu o ukonceni
    
    close(srv_sock);
    shutdown_prompt();
    shutdown_logging();
    
    printf("Server ukončen.\n");
    print_stats();
}

void start_server(char *host_arg, char *port_arg, char *log_arg) {
    int32_t host;
    int32_t port;
    char *log_file_name;
    
    atexit(shutdown_connection);
    
    while (true) {
        host = parse_host(host_arg);
        port = parse_port(port_arg);
        log_file_name = parse_log(log_arg);
        
        int srv_sock = setup_connection(host, port, log_file_name);
        run_connection(srv_sock);
        shutdown_connection(srv_sock);
    }
}