#include "defs.h"
#include "thread.h"
#include "game.h"
#include "connection.h"
#include "logging.h"

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>

#include <arpa/inet.h>

/*
    hlavní modul aplikace, která slouží jako TCP server pro hru Piškvorky pro více hráčů
*/

/**
    Vypíše nápovědu do příkazové řádky a ukončí program.
*/
void print_help_and_exit() {
    puts("TCP server hry \"Piškvorky pro více hráčů\" (Verze 1.0)");
	puts("Seminární práce z předmětu \"Úvod do počítačových sítí\" (KIV/UPS)");
	puts("Autor: Petr Kozler (A13B0359P), 2016\n");
	puts("Použití:   server [[<host>:]<port>]");
    puts("Příklad:   server 10001");
    puts("Specifikace parametrů:");
    puts("   <host> ... konkrétní IP adresa pro naslouchání");
    puts("   <port> ... celé číslo v rozsahu 1 - 65535");

    exit(EXIT_SUCCESS);
}

/**
    Provede validaci portu.
*/
bool validate_port(int32_t *port, char *str) {
    *port = strtol(str, NULL, 10);

    if (*port < 1 || *port > 65535) {
        return false;
    }

    return true;
}

/**
    Provede validaci IP adresy.
*/
bool validate_host(int32_t *host, char *str) {
    bool valid = inet_pton(AF_INET, str, host);
    *host = htonl(*host);
    return valid;
}

/**
    Získá IP adresu a port server-socketu od uživatele prostřednictvím standardního vstupu.
*/
inet_socket_address_t *enter_host_and_port(bool invalid_args) {
    bool error_message = invalid_args;
    inet_socket_address_t *address = (inet_socket_address_t *) malloc(sizeof(inet_socket_address_t));
    bool valid;

    while (true) {
        if (error_message) {
            printf("Neplatný vstup.\n");
        }

        printf("Zadejte IP adresu pro naslouchání (např. %s) "
            "nebo prázdný řádek pro naslouchání všem adresám:\n", SOCK_HOST);

        char buf[MAX_STR_LEN];

        if (fgets(buf, MAX_STR_LEN, stdin) == NULL) {
            err("fgets()");
        }

        size_t len = strlen(buf);
        buf[len - 1] = buf[len];

        if (buf[0] != '\0') {
            strcpy(address->host_str, buf);
            valid = validate_host(&(address->host), buf);

            if (!valid) {
                error_message = true;
                continue;
            }
        }

        printf("Zadejte port pro naslouchání: (např. %d)\n", SOCK_PORT);

        if (fgets(buf, MAX_STR_LEN, stdin) == NULL) {
            err("fgets()");
        }

        valid = validate_port(&(address->port), buf);

        if (!valid) {
            error_message = true;
            continue;
        }

        return address;
    }
}

/**
    Získá IP adresu a port socketu z argumentu příkazové řádky.
*/
inet_socket_address_t *parse_address(char *arg) {
    inet_socket_address_t *address = (inet_socket_address_t *) malloc(sizeof(inet_socket_address_t));
    bool valid;

    if (strstr(arg, ":") == NULL) {
        valid = validate_port(&(address->port), arg);

        if (!valid) {
            free(address);
            return NULL;
        }
    }
    else {
        char *token;

        token = strtok(arg, ":");
        strcpy(address->host_str, token);
        valid = validate_host(&(address->host), token);

        if (!valid) {
            free(address);
            return NULL;
        }

        token = strtok(NULL, ":");
        valid = validate_port(&(address->port), token);

        if (!valid) {
            free(address);
            return NULL;
        }
    }

    return address;
}

/**
    Zkontroluje argumenty příkazové řádky a pokud je argumentem
	platná adresa a port, předá je jako návratovou hodnotu.
	Pokud není, vyžádá je od uživatele.
*/
inet_socket_address_t *get_address_from_args(int argc, char **argv) {
    if (argc > 1) {
        // výpis nápovědy
        if (!strcmp("-h", argv[1]) || !strcmp("--help", argv[1])) {
            print_help_and_exit();
        }

        // pokus o parsování IP a portu z parametru příkazového řádku
        inet_socket_address_t *address = parse_address(argv[1]);

        // načtení z klávesnice při neplatném vstupu
        if (address == NULL) {
            return enter_host_and_port(true);
        }
        else {
            return address;
        }
    }
    else {
        return enter_host_and_port(false);
    }
}

/**
    Vstupní bod programu.
*/
int main(int argc, char **argv)
{
    inet_socket_address_t *address = get_address_from_args(argc, argv);
    thread_counter = 0;
    game_counter = 0;
    pthread_mutex_init(&thread_list_lock, NULL);
    pthread_mutex_init(&game_list_lock, NULL);
    log_file = fopen("log.txt", "w");

    if (log_file == NULL) {
        printf("Chyba (%i, %s): chyba při otevárání výstupního souboru.",
            errno, strerror(errno));
        exit(EXIT_FAILURE);
    }

    print("Server spuštěn pro poslouchání %s na portu: %d",
        address->host ? address->host_str : "všech IP adres", address->port);

    run_server(address->host, address->port);

    fclose(log_file);
    pthread_mutex_destroy(&game_list_lock);
    pthread_mutex_destroy(&thread_list_lock);
    free(address);

    return 0;
}
