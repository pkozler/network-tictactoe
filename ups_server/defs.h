#ifndef DEFS_H_INCLUDED
#define DEFS_H_INCLUDED

#include <pthread.h>
#include <stdint.h>

#include "linkedlist.h"

/*
    header obsahující definice konstant představujících
    základní konfiguraci serveru a globální proměnné
    používané v různých částech programu (např. seznamy)
*/

#define SOCK_HOST "127.0.0.1"
#define SOCK_PORT 10001
#define SOCKET_TIMEOUT_SEC 3
#define TIMER_DELAY_MICROS 300000
#define QUEUE_LEN 8
#define MAX_STR_LEN 16
#define MIN_PLAYERS_SIZE 2
#define MAX_PLAYERS_SIZE 8
#define MIN_MATRIX_SIZE 3
#define MAX_MATRIX_SIZE 16
#define MSG_CODE 1
#define ACK_CODE -1

/* spojový seznam vláken */
struct LINKED_LIST *thread_list;
/* spojový seznam běžících her */
struct LINKED_LIST *game_list;
/* čítač připojených klientů pro přidělování ID */
int32_t thread_counter;
/* čítač vytvořených her pro přidělování ID */
int32_t game_counter;
/* zámek pro přístup ke spojovému seznamu */
pthread_mutex_t thread_list_lock;
/* zámek pro přístup ke spojovému seznamu */
pthread_mutex_t game_list_lock;

/**
    Strukura, která slouží pro předání portu socketu
    a případně IP adresy pro naslouchání.
*/
typedef struct {
    char host_str[MAX_STR_LEN]; // textová reprezentace IP adresy
    int32_t host; // IP adresa
    int32_t port; // port
} inet_socket_address_t;

#endif // DEFS_H_INCLUDED
