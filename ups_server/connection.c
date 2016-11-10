/* 
 * Author: Petr Kozler
 */

#include "connection.h"
#include "connection_stats.h"
#include "config.h"
#include "observed_list.h"
#include "player.h"
#include "game.h"
#include "console.h"
#include "logger.h"
#include "printer.h"
#include "broadcast.h"
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

int create_serversocket(int32_t host, int32_t port) {
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
    if (listen(srv_sock, QUEUE_LEN) < 0) {
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

void clear_stats() {
    g_stats.bytes_transferred = 0;
    g_stats.connections_established = 0;
    g_stats.messages_transferred = 0;
    g_stats.transfers_failed = 0;
    g_stats.start_time.tv_sec = 0;
    g_stats.start_time.tv_usec = 0;
}

int setup_connection(int32_t host, int32_t port, char *log_file_name) {
    clear_stats();
    g_running = true;
    
    start_logging(log_file_name);
    start_prompt();
    
    int srv_sock = create_serversocket(host, port);
    
    print_out("Server spuštěn");
    
    return srv_sock;
}

void run_connection(int srv_sock) {
    // uložení času spuštění
    gettimeofday(&(g_stats.start_time), NULL);
    
    while (g_running) {
        int cli_sock = accept_socket(srv_sock);
        
        /*if (cli_sock < 0) {
            continue;
        }*/
        
        create_player(cli_sock);
    }
}

void shutdown_connection(int srv_sock) {
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
    
    while (true) {
        host = parse_host(host_arg);
        port = parse_port(port_arg);
        log_file_name = parse_log(log_arg);
        
        int srv_sock = setup_connection(host, port, log_file_name);
        run_connection(srv_sock);
        shutdown_connection(srv_sock);
    }
}