#ifndef CONNECTION_H_INCLUDED
#define CONNECTION_H_INCLUDED

#include <pthread.h>

#include "thread.h"
#include "game.h"

/*
    header s deklarací funkce pro spuštění komunikace s klienty
*/

/* spuštění komunikace */
void run_server(int, int);

#endif // CONNECTION_H_INCLUDED
