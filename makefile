#
# Makefile pro C server
#

# nastavení překladu - překladač, parametry, názvy souborů
CC = gcc
CFLAGS = -std=c11 -lm -O2 -lpthread
BIN = ../c_bin/server
OBJ = ../c_bin/player.o ../c_bin/broadcaster.o ../c_bin/checker.o ../c_bin/cmd_arg.o ../c_bin/com_stats.o ../c_bin/console.o ../c_bin/game.o ../c_bin/game_list.o ../c_bin/game_list_sender.o ../c_bin/game_logic.o ../c_bin/game_status_sender.o ../c_bin/linked_list.o ../c_bin/linked_list_iterator.o ../c_bin/logger.o ../c_bin/main.o ../c_bin/message.o ../c_bin/message_list.o ../c_bin/player_list.o ../c_bin/player_list_sender.o ../c_bin/printer.o ../c_bin/request_parser.o ../c_bin/string_builder.o ../c_bin/string_utils.o ../c_bin/tcp_communicator.o ../c_bin/tcp_server.o ../c_bin/tcp_server_control.o

# sestaví spustitelný soubor z vytvořených objektových souborů
$(BIN): $(OBJ)
	$(CC) $^ -o $@ $(CFLAGS)

# přeloží soubory zdrojového kódu do objektových souborů
../c_bin/%.o: %.c
	$(CC) -c $< -o $@

# smaže vytvořené objektové soubory (spustitelný soubor nemaže)
clean:
	rm -f $(OBJ)
