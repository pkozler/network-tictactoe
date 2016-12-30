/* 
 * Author: Petr Kozler
 */

#ifndef TCP_SERVER_INFO_H
#define TCP_SERVER_INFO_H

#include "cmd_arg.h"

#include <stdbool.h>
#include <stdint.h>

bool is_server_running();
void start_server();
void stop_server();
void print_args();
void print_stats();
void add_stats_bytes_transferred(int32_t bytes_transferred);
void add_stats_connections_established(int32_t connections_established);
void add_stats_messages_transferred(int32_t messages_transferred);
void add_stats_transfers_failed(int32_t transfers_failed);
void clear_stats();

#endif /* TCP_SERVER_INFO_H */

