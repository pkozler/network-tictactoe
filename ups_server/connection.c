#include "connection.h"

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdint.h>
#include <signal.h>
#include <unistd.h>
#include <pthread.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#include "logging.h"
#include "thread.h"

/*
    modul s funkcemi pro navazování spojení s klienty
    a vytváření obslužných vláken pro komunikaci s klienty
*/

/**
    Vytvoří server-socket.
*/
int create_srv_sock(int32_t sock_host, int32_t sock_port) {
    int srv_sock;
    struct sockaddr_in srv_addr;

    // vytvoření TCP socketu
    srv_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (srv_sock < 0) {
        err("socket()");
    }

    struct timeval timeout;
    timeout.tv_sec = SOCKET_TIMEOUT_SEC;
    timeout.tv_usec = 0;

    if (setsockopt (srv_sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        err("setsockopt()");
    }

    if (setsockopt (srv_sock, SOL_SOCKET, SO_SNDTIMEO, (char *)&timeout,
                sizeof(timeout)) < 0) {
        err("setsockopt()");
    }

    // nastavení znovupoužití adresy
    int optval = 1;
    setsockopt(srv_sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval));

    // naplnění struktury adresy
    memset(&srv_addr, 0, sizeof(srv_addr));
    srv_addr.sin_family = AF_INET;
    srv_addr.sin_port = htons(sock_port);
    srv_addr.sin_addr.s_addr = htonl(sock_host);

    // provázání socketu s adresou
    if (bind(srv_sock, (struct sockaddr*) &srv_addr, sizeof(srv_addr)) < 0) {
        err("bind()");
    }

    // nastavení režimu čekání na příchozí spojení
    if (listen(srv_sock, QUEUE_LEN) < 0) {
        err("listen()");
    }

    return srv_sock;
}

/**
    Čeká na příchozí spojení s klientem.
*/
int accept_cli_sock(int srv_sock) {
    int sock;
    struct sockaddr_in addr;
    unsigned int addr_len;

    // přijetí příchozího spojení
    addr_len = sizeof(addr);

    do {
        sock = accept(srv_sock, (struct sockaddr *) &addr, &addr_len);

        /*if (sock < 0) {
            printf("Čekám na spojení s klientem...\n");
        }*/
    }
    while (sock < 0);

    print("Příchozí spojení %s:%i", inet_ntoa(addr.sin_addr), ntohs(addr.sin_port));

    return sock;
}

/**
    Vytvoří strukturu pro uchování stavu hráče,
    která slouží jako argument pro obslužná vlákna klienta
    a spustí obslužná vlákna.
*/
void create_thread(int sock, int id) {
    thread_t *thread = (thread_t *) calloc(1, sizeof(thread_t));

    if (thread == NULL) {
        err("malloc()");
    }

    thread->client_id = id;
    thread->sock = sock;
    pthread_mutex_init(&thread->thread_lock, NULL);

    print("Vytvořeno vlákno s ID: %d", thread->client_id);
    pthread_mutex_lock(&thread_list_lock);

    if (!add_to_list(&thread_list, thread)) {
        print("Nepodařilo se vytvořit vlákno pro klienta %d.", thread->client_id);
        free(thread);
        return;
    }

    thread_counter++;
    pthread_mutex_unlock(&thread_list_lock);
    thread->connected = true;

    if (pthread_create(&thread->recv_thread, NULL, run_recv_thread, (void *) thread) < 0) {
        err("pthread_create()");
    }

    if (pthread_create(&thread->send_thread, NULL, run_send_thread, (void *) thread) < 0) {
        err("pthread_create()");
    }
}

/**
    Spustí navazování spojení s klienty a vytváření
    vláken pro komunikaci s nimi.
*/
void run_server(int32_t sock_host, int32_t sock_port) {
    signal(SIGPIPE, SIG_IGN);
    // vytvoření serversocketu
    int srv_sock = create_srv_sock(sock_host, sock_port);

    // inicializace datových struktur obslužných vláken pro klienty
    for (;;) {
        // spojení s klientem a vytvoření obslužného vlákna
        int sock = accept_cli_sock(srv_sock);
        create_thread(sock, thread_counter + 1);
    }

    close(srv_sock);
}
