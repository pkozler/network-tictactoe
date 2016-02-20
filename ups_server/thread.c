#include "thread.h"

#include "defs.h"
#include "game.h"
#include "thread.h"
#include "common.h"
#include "receiver.h"
#include "sender.h"
#include "logging.h"
#include "linkedlist.h"

#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/time.h>

/*
    modul obsahující vstupní funkce vláken pro komunikaci s klienty
*/

/**
    Vstupní bod vlákna pro příjem požadavků klienta.
*/
void *run_recv_thread(void *arg) {
    thread_t *thread = (thread_t *) arg;

    while (true) {
        while (thread->game == NULL
            || !(thread->game->running)) {
            receive_game_request(thread);
        }

        receive_message(thread);

        while (thread->game != NULL
            && thread->game->running) {
            receive_message(thread);
        }
    }
}

/**
    Vstupní bod vlákna pro odesílání opovědí klientovi.
*/
void *run_send_thread(void *arg) {
    thread_t *thread = (thread_t *) arg;

    int32_t client_id = htonl(thread->client_id);
    write_bytes(thread, &client_id, sizeof(client_id));
    print("Odesláno klientské ID vlákna: %d", thread->client_id);

    while (true) {
        print("Klient %d: zahájeno odesílání seznamu her.", thread->client_id);

        while (thread->game == NULL) {
            usleep(TIMER_DELAY_MICROS);
            check_connection(thread);
            send_game_list(thread);
        }

        send_game_list(thread);
        print("Klient %d: ukončeno odesílání seznamu her.", thread->client_id);
        print("Klient %d: zahájeno odesílání stavu hry.", thread->client_id);

        while (thread->game != NULL) {
            usleep(TIMER_DELAY_MICROS);
            check_connection(thread);
            send_status(thread);
        }

        print("Klient %d: ukončeno odesílání stavu hry.", thread->client_id);
    }
}
