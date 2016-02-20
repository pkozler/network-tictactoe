#ifndef RECEIVER_H_INCLUDED
#define RECEIVER_H_INCLUDED

#include "thread.h"

/*
    header s deklaracemi funkcí pro příjem požadavků
*/

/* kontrola kódu zprávy */
bool is_message(thread_t *);
/* příjem výběru hry */
void receive_game_request(thread_t *);
/* příjem herního tahu */
void receive_message(thread_t *);

#endif // RECEIVER_H_INCLUDED
