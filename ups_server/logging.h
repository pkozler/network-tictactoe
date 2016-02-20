#ifndef LOGGING_H_INCLUDED
#define LOGGING_H_INCLUDED

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

/*
    header s deklaracemi funkcí pro logování stavu serveru
*/

/* soubor pro zápis logů */
FILE *log_file;

/* výpis */
void print(const char *format, ...);
/* chybový výpis */
void err(const char *);

#endif // LOGGING_H_INCLUDED
