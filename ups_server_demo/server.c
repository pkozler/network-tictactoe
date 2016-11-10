/* 
 * Author: Petr Kozler
 * 
 * Modul server.c definuje funkce hlavního vlákna serveru pro navazování
 * spojení s klienty.
 */

#include "server.h"
#include "defs.h"
#include "client.h"
#include "log_printer.h"
#include "linked_list.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/un.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

/**
 * Vytvoří socket serveru svázaný s předanou IP adresou a portem pro naslouchání
 * spojení s klienty.
 * 
 * @param host IP adresa pro naslouchání
 * @param port číslo portu pro naslouchání
 * 
 * @return deskriptor socketu serveru
 */
int create_serversocket(char *host, int32_t port) {
    int srv_sock;
    struct sockaddr_in srv_addr;

    srv_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (srv_sock < 0) {
        err("Chyba při vytváření socketu serveru");
    }
    else {
        out("Socket serveru byl úspěšně vytvořen");
    }

    int optval = 1;
    
    if (setsockopt(srv_sock, SOL_SOCKET, SO_REUSEADDR, &optval,
            sizeof(optval)) < 0) {
        err("Chyba při nastavování znovupoužití adresy");
    }
    else {
        out("Znovupoužití adresy bylo úspěšně nastaveno");
    }

    memset(&srv_addr, 0, sizeof(struct sockaddr_in));
    srv_addr.sin_family = AF_INET;
    srv_addr.sin_port = htons(port);
    inet_aton(host, &srv_addr.sin_addr);

    if (bind(srv_sock, (struct sockaddr*) &srv_addr, sizeof(srv_addr)) < 0) {
        err("Chyba při propojování socketu serveru s adresou pro naslouchání");
    }
    else {
        out("Socket serveru byl úspěšně propojen s adresou pro naslouchání");
    }

    if (listen(srv_sock, QUEUE_LENGTH) < 0) {
        err("Chyba při spouštění naslouchání příchozím spojením");
    }
    else {
        char *ip_str = inet_ntoa(srv_addr.sin_addr);
        int port_no = (int) htons(srv_addr.sin_port);
        out("Naslouchání příchozím spojením na adrese %s:%d spuštěno",
                ip_str, port_no);
    }

    return srv_sock;
}

/**
 * Čeká na spojení s klientem a v případě příchozího spojení vytvoří socket
 * klienta pro příjem a odesílání zpráv.
 * 
 * @param srv_sock deskriptor socketu serveru
 * 
 * @return deskriptor socketu klienta
 */
int accept_socket(int srv_sock) {
    int sock;
    struct sockaddr_in addr;
    socklen_t addr_len;

    addr_len = sizeof(addr);

    do {
        sock = accept(srv_sock, (struct sockaddr *) &addr, &addr_len);
        
        if (sock < 0) {
            err("Chyba při navazování spojení s klientem");
            
            continue;
        }
        
        char *ip_str = inet_ntoa(addr.sin_addr);
        int port_no = (int) htons(addr.sin_port);
        out("Spojení s klientem na adrese %s:%d navázáno",
            ip_str, port_no);
        
        struct timeval timeout;
        timeout.tv_sec = SOCKET_TIMEOUT_SEC;
        timeout.tv_usec = 0;

        if (setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout,
                    sizeof(timeout)) < 0) {
            err("Chyba při nastavování timeoutu pro příjem zpráv");
            close(sock);
            sock = -1;
            
            continue;
        }
        
        out("Timeout pro příjem zpráv byl úspěšně nastaven na %d sekundy",
                SOCKET_TIMEOUT_SEC);
    }
    while (sock < 0);
    
    return sock;
}

/**
 * Představuje rutinu hlavního vlákna serveru pro vytváření spojení s klienty,
 * alokaci paměti pro struktury uchovávající data jednotlivých klientů
 * a spouštění pracovních vláken pro paralelní obsluhu klientských požadavků.
 * 
 * @param host IP adresa pro naslouchání
 * @param port číslo portu pro naslouchání
 */
void start_server(char *host, int32_t port) {
    int srv_sock = create_serversocket(host, port);
    int counter = 0;
    client_list = create_linked_list();
    
    while (1) {
        counter++;
        int cli_sock = accept_socket(srv_sock);
        client_t *client = create_client(counter, cli_sock);
        
        if (client == NULL) {
            err("Klient %d: Chyba při vytváření strukury pro data",
                    counter);
        }
        else {
            out("Klient %d: Strukura pro data byla úspěšně vytvořena",
                    counter);
        }
        
        add_to_list(client_list, (void *) client);
        
        if (pthread_create(&client->thread, NULL, run_client, (void *) client) < 0) {
            err("Klient %d: Chyba při spouštění pracovního vlákna pro komunikaci",
                    counter);
        }
        else {
            out("Klient %d: Pracovní vláko pro komunikaci bylo úspěšně spuštěno",
                    counter);
        }
    }
    
    clear_list(client_list);
    delete_linked_list(client_list);
}