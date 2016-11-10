/* 
 * Author: Petr Kozler
 */

#ifndef CONNECTION_H
#define CONNECTION_H

void start_server(char *host_arg, char *port_arg, char *log_arg);

void send_restart_warning();

#endif /* CONNECTION_H */