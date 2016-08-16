#include "connection.h"
#include "config.h"
#include "console.h"
#include "logger.h"

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
#include <errno.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

void create_serversocket(int32_t host, int32_t port) {
    int srv_sock;
    struct sockaddr_in srv_addr;

    // vytvoření TCP socketu
    srv_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (srv_sock < 0) {
        log("Chyba při vytváření TCP socketu serveru: %s", strerror(errno));
    }

    struct timeval timeout;
    timeout.tv_sec = SOCKET_TIMEOUT_SEC;
    timeout.tv_usec = 0;

    if (setsockopt (srv_sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        log("Chyba při nastavování timeoutu pro příjem zpráv: %s", strerror(errno));
    }

    if (setsockopt (srv_sock, SOL_SOCKET, SO_SNDTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        log("Chyba při nastavování timeoutu pro odesílání zpráv: %s", strerror(errno));
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
        log("Chyba při propojování socketu serveru s IP adresou: %s", strerror(errno));
    }

    // nastavení režimu čekání na příchozí spojení
    if (listen(srv_sock, QUEUE_LEN) < 0) {
        log("Chyba při spouštění poslechu příchozích spojení: %s", strerror(errno));
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

int setup_connection(int32_t host, int32_t port, char *log_file_name) {
    running = true;
    start_logging(log_file_name);
    start_prompt();
    int srv_sock = create_serversocket(host, port);
    // TODO vytvorit seznam her a hracu a vynulovat statistiky
    printf("Server spuštěn.\n");
    
    return srv_sock;
}

void run_connection(int srv_sock) {
    while (running) {
        int cli_sock = accept_socket(srv_sock);
        // TODO vytvorit hrace
    }
}

void shutdown_connection(int srv_sock) {
    // TODO uklidit
    printf("Server ukončen.\n");
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