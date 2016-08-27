/* 
 * Author: Petr Kozler
 */

#include <stdbool.h>

#include "message.h"

/**
 * Načte délku řetězce přijaté zprávy od klienta se zadaným číslem socketu
 * a následně načte jednotlivé znaky zprávy ze socketu, které upraví do podoby
 * standardního řetězce zakončeného nulovým znakem. Tento řetězec poté předá
 * volající funkci v návratové hodnotě. V případě, že délka zprávy má hodnotu 0
 * (testování odezvy) vrací pouze nulový znak. V případě chyby vrací hodnotu NULL.
 * 
 * @param sock deskriptor socketu klienta - odesílatele
 * @return zpráva v textové formě (standardní řetězec, délka smí být nulová) nebo NULL
 */
char *read_from_socket(int sock) {
    // TODO implementovat !!!
}

/**
 * Zjistí délku řetězce zprávy určené k odeslání klientovi. Klientovi poté zapíše
 * do socketu tuto hodnotu, následovanou jednotlivými znaky zprávy. V případě, že
 * zadaný řetězec obsahuje pouze nulový znak, odešle klientovi pouze délku zprávy
 * s hodnotou 0 (testování odezvy).
 * 
 * @param msg_str zpráva v textové formě (standardní řetězec, délka smí být nulová)
 * @param sock deskriptor socketu klienta - příjemce
 */
void write_to_socket(char *msg_str, int sock) {
    // TODO implementovat !!!
}

/**
 * Provede syntaktickou kontrolu zprávy - ověří, zda může být načtený řetězec
 * platnou zprávou aplikačního protokolu.
 * Na počátku každé platné zprávy je uveden řetězec reprezentující typ předávané
 * zprávy, za ním pak následují argumenty (v závazném pořadí) platné pro daný typ.
 * Jednotlivé tokeny jsou odděleny znakem definovaným v konfiguračním souboru.
 * Výjimkou z uvedených pravidel je řetězec obsahující pouze nulový znak.
 * Takový řetězec značí zprávu s nulovou délkou, která je základem protokolu
 * pro periodické testování odezvy mezi klienty a serverem (heartbeat protokolu).
 * 
 * @param msg_str zpráva v textové formě
 * @return true v případě platné zprávy, jinak false
 */
bool is_message_valid(char *msg_str) {
    // TODO implementovat !!!
}

/**
 * Vytvoří novou strukturu zprávy zadaného typu se zadaným počtem parametrů
 * určenou k odeslání klientovi.
 * 
 * @param msg_type typ zprávy
 * @param msg_argc počet parametrů
 * @return alokovaná struktura zprávy
 */
message_t *create_message(char *msg_type, int32_t msg_argc) {
    message_t *message = (message_t *) malloc(sizeof(message_t));
    
    message->msg_type = msg_type;
    message->msg_argc = msg_argc;
    message->msg_argv = (char **) malloc(sizeof(char *) * msg_argc);
    
    return message;
}

/**
 * Odstraní vytvořenou strukturu zprávy.
 * 
 * @param msg alokovaná struktura zprávy
 */
void delete_message(message_t *msg) {
    int32_t i;
    
    for (i = 0; i < msg->msg_argc; i++) {
        free(msg->msg_argv[i]);
    }
    
    free(msg->msg_argv);
    free(msg);
}

/**
 * Přijme zprávu od klienta se zadaným číslem socketu, spustí kontrolu zprávy,
 * a je-li zpráva platná, převede ji z textové formy do formy struktury určené
 * pro další zpracování v programu, kterou předá volající funkci v návratové
 * hodnotě. V případě zjištění neplatné zprávy vrací hodnotu NULL.
 * 
 * @param sock deskriptor socketu klienta - odesílatele
 * @return přijatá platná zpráva ve formě struktury nebo NULL
 */
message_t *receive_message(int sock) {
    // TODO implementovat !!!
}

/**
 * Převede zadanou zprávu z formy struktury do textové formy určené k přenosu
 * po síti a odešle ji v této podobě klientovi se zadaným číslem socketu.
 * 
 * @param msg odesílaná zpráva ve formě struktury
 * @param sock deskriptor socketu klienta - příjemce
 */
void send_message(message_t *msg, int sock) {
    // TODO implementovat !!!
}