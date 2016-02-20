#ifndef SENDER_H_INCLUDED
#define SENDER_H_INCLUDED

#include "thread.h"

/*
    header s deklaracemi funkcí pro odesílání odpovědí
*/

/* kontrola stavu spojení */
void check_connection(thread_t *);
/* odeslání seznamu her */
void send_game_list(thread_t *);
/* odeslání stavu hry */
void send_status(thread_t *);

#endif // SENDER_H_INCLUDED
