#include "logger.h"
#include <string.h>

#define MAX_LINE_LEN 1024

void log(const char *format, ...) {
    va_list vargs;
    char *line = (char *) malloc(sizeof(char) * MAX_LINE_LEN);
    
    va_start(vargs, format);
    vsnprintf(line, MAX_LINE_LEN, format, vargs);
    va_end(vargs);
    
    // TODO vlozit radek logu do fronty pro zapis do souboru
}

void run_logging(void *arg) {
    
}

void start_logging(char *log_file_name) {
    g_log_file = fopen(log_file_name);
    
}