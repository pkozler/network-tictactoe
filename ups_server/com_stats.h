/* 
 * Author: Petr Kozler
 */

#ifndef COM_STATS_H
#define COM_STATS_H

/**
 * Struktura uchovávající statistiky běhu serveru.
 */
typedef struct {
   uint32_t bytes_transferred; // přenesený počet bytů
   uint32_t messages_transferred; // přenesený počet zpráv
   uint32_t connections_established; // počet navázaných spojení
   uint32_t transfers_failed; // počet přenosů zrušených pro chybu
   struct timeval start_time; // čas spuštění serveru
} stats_t;

void add_stats_bytes_transferred(int32_t bytes_transferred);
void add_stats_connections_established(int32_t connections_established);
void add_stats_messages_transferred(int32_t messages_transferred);
void add_stats_transfers_failed(int32_t transfers_failed);
void clear_stats();

#endif /* COM_STATS_H */

