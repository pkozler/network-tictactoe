#include "connection.h"
#include "config.h"
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#define HOST_MAX_LEN 15
#define PORT_MAX_LEN 5
#define LOG_MAX_LEN 2000

int32_t parse_host(char *host_arg) {
    while (true) {
        int32_t host = 0;
        
        if (host_arg != NULL) {
            if (strcmp(ANY_IP_STR, host_arg)) {
                printf("Server bude přijímat spojení z libovolné adresy.\n");
                
                return INADDR_ANY;
            }

            if (inet_pton(AF_INET, host_arg, host)) {
                printf("Server bude přijímat spojení z adresy %s.\n", host_arg);
                *host = htonl(*host);
                
                return host;
            }
            
            printf("Neplatný formát IP adresy. IP adresa musí být ve tvaru \"#.#.#.#\", kde \"#\" je celé číslo v rozsahu 0 - 255.\n");
            free(host_arg);
        }
        
        host_arg = (char *) malloc(sizeof(char) * HOST_MAX_LEN + 1);
        printf("Zadejte IP adresu pro naslouchání nebo \"%s\" pro naslouchání na všech adresách a stiskněte ENTER:\n", ANY_IP_STR);
        fgets(host_arg, HOST_MAX_LEN, stdin);
    }
}

int32_t parse_port(char *port_arg) {
    while (true) {
        int32_t port = 0;
        
        if (port_arg != NULL) {
            int32_t port = strtol(port_arg, NULL, 10);
            
            if (port >= MIN_PORT && port <= MAX_PORT) {
                printf("Server bude přijímat spojení na portu %d.\n", port);
                
                return port;
            }
            
            printf("Neplatný formát čísla portu. Port musí být celé číslo v rozsahu %d - %d\n", MIN_PORT, MAX_PORT);
            free(port_arg);
        }
        
        port_arg = (char *) malloc(sizeof(char) * PORT_MAX_LEN + 1);
        printf("Zadejte číslo portu pro naslouchání a stiskněte ENTER:\n");
        fgets(port_arg, PORT_MAX_LEN, stdin);
    }
}

char *parse_log(char *log_arg) {
    while (true) {
        if (log_arg != NULL) {
            char invalid_chars[] = "!@%^*~|";
            bool valid_path = true;
            int i;
            for (i = 0; i < strlen(invalid_chars); ++i) {
                if (strchr(log_arg, invalid_chars[i]) != NULL) {
                    valid_path = false;

                    break;
                }
            }
            
            if (valid_path) {
                printf("Server bude zapisovat logy do souboru %s.\n", log_arg);
                
                return log_arg;
            }
            
            printf("Neplatný formát cesty k souboru.\n");
        }
        
        log_arg = (char *) malloc(sizeof(char) * LOG_MAX_LEN + 1);
        printf("Zadejte název souboru pro logování včetně cesty a stiskněte ENTER:\n");
        fgets(log_arg, LOG_MAX_LEN, stdin);
    }
}

void run_prompt(void *arg) {
    
}

void start_prompt() {
    
}