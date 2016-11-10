/* 
 * Author: Petr Kozler
 * 
 * Modul client.c definuje funkce pro zpracování požadavků připojených klientů
 * v klientských vláknech.
 */

#include "client.h"
#include "defs.h"
#include "linked_list.h"
#include "message.h"
#include "log_printer.h"

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <stdio.h>

/**
 * Vrátí řetězcovou reprezentaci klienta obsahující ID a případně login.
 * 
 * @param client struktura klienta
 * 
 * @return řetězcová reprezentace
 */
char *client_to_string(client_t *client) {
    static char buf[CLIENT_STR_LENGTH];
    
    if (client->login == NULL) {
        sprintf(buf, "Klient %d: ", client->id);
    }
    else {
        sprintf(buf, "Klient %d (%s): ", client->id, client->login);
    }
    
    return buf;
}

/**
 * Přijme zprávu od klienta a vrátí vytvořenou strukturu zprávy.
 * 
 * @param client struktura klienta
 * 
 * @return struktura přijaté zprávy
 */
message_t *receive_client_request(client_t *client) {
    message_t *received;
    int result = receive_message(&received, client->sock);
    
    if (result < 0) {
        err("%sChyba při příjmu zprávy", client_to_string(client));

        return NULL;
    }
    
    if (result == 0) {
        out("%sKlient se odpojil", client_to_string(client));
        client->connected = false;
        
        return NULL;
    }
    
    if (received == NULL) {
        err("%sChyba při alokaci paměti pro přijatou zprávu",
                client_to_string(client));

        return NULL;
    }
    
    return received;
}

/**
 * Zpracuje nulovou zprávu klienta (prázdná zpráva s nulovou velikostí),
 * která je klientem automaticky odesílána v pravidelných intervalech
 * k otestování odezvy serveru, a pošle taktéž nulovou zprávu jako odpověď.
 * 
 * @param received struktura přijaté zprávy
 * @param client struktura klienta
 * 
 * @return struktura odeslané zprávy, nebo NULL v případě chyby
 */
message_t *handle_ping(message_t *received, client_t *client) {
    if (received->length > 0) {
        return NULL;
    }
    
    // out("%sPřijat ping", client_to_string(client));
    message_t *sent = create_message(0);
    
    if (sent == NULL) {
        /* err("%sChyba při vytváření odpovědi na ping",
                client_to_string(client)); */
        
        return NULL;
    }
    
    // out("%sVytvořena odpověď na ping", client_to_string(client));
    
    if (!send_message(sent, client->sock)) {
        /* err("%sChyba při odesílání odpovědi na ping",
                client_to_string(client)); */
        
        return sent;
    }
    
    // out("%sOdeslána odpověď na ping", client_to_string(client));
    
    return sent;
}

/**
 * Zpracuje počáteční zprávu od klienta, obsahující jeho přihlašovací jméno,
 * a odešle zprávu s potvrzením tohoto jména.
 * 
 * @param received struktura přijaté zprávy
 * @param client struktura klienta
 * 
 * @return struktura odeslané zprávy, nebo NULL v případě chyby
 */
message_t *handle_login(message_t *received, client_t *client) {
    if (client->login != NULL) {
        return NULL;
    }
    
    out("%sPřijata zpráva s loginem: \"%s\"",
                client_to_string(client), received->bytes);
    
    int32_t msg_total_len = LOGIN_ACK_LENGTH + 1 + received->length;
    message_t *sent = create_message(msg_total_len);
    
    if (sent == NULL) {
        err("%sChyba při vytváření zprávy pro potvrzení loginu",
                client_to_string(client));
        
        return NULL;
    }
    
    out("%sVytvořena zpráva pro potvrzení loginu",
            client_to_string(client));
    
    strcat(sent->bytes, LOGIN_ACK_STR);
    strcat(sent->bytes, " ");
    strcat(sent->bytes, received->bytes);
    
    if (!send_message(sent, client->sock)) {
        err("%sChyba při odesílání zprávy s potvrzením loginu",
                client_to_string(client));
        
        return sent;
    }
    
    out("%sOdeslána zpráva s potvrzením loginu: \"%s\"",
            client_to_string(client), sent->bytes);
    client->login = (char *) malloc(sizeof(char) * (received->length));
    client->login[0] = '\0';
    strcat(client->login, received->bytes);
        
    return sent;
}

/**
 * V případě dosažení limitu počtu požadavků deaktivuje klienta a odešle mu řetězec
 * představující zprávu o ukončení spojení.
 * 
 * @param received struktura přijaté zprávy
 * @param client struktura klienta
 * 
 * @return struktura odeslané zprávy, nebo NULL v případě chyby
 */
message_t *handle_logout(message_t *received, client_t *client) {
    if (client->messages_left > 0) {
        return NULL;
    }
    
    client->connected = false;
    message_t *sent = create_message(CLOSE_MSG_LENGTH);
    
    if (sent == NULL) {
        err("%sChyba při vytváření zprávy pro upozornění na odhlášení",
                client_to_string(client));
        
        return NULL;
    }
    
    out("%sVytvořena zpráva pro upozornění na odhlášení",
            client_to_string(client));
    
    strcat(sent->bytes, CLOSE_MSG_STR);
    
    if (!send_message(sent, client->sock)) {
        err("%sChyba při odesílání zprávy s upozorněním na odhlášení",
                client_to_string(client));
        
        return sent;
    }
    
    out("%sOdeslána zpráva s upozorněním na odhlášení: \"%s\"",
            client_to_string(client), sent->bytes);
    
    return sent;
}

/**
 * Sníží o hodnotu 1 počet zbývajících požadavků, které klient může serveru
 * odeslat, než bude automaticky odpojen.
 * 
 * @param client struktura klienta
 */
void decrement_message_count(client_t *client) {
    client->messages_left--;
    out("%sZbývá požadavků: %d",
            client_to_string(client), client->messages_left);
}

/**
 * Zpracuje běžný požadavek od přihlášeného klienta. Zpracování požadavku
 * spočívá v převrácení řetězce zprávy. Upravený řetězec zprávy je poté
 * odeslán klientovi jako odpověď.
 * 
 * @param received struktura přijaté zprávy
 * @param client struktura klienta
 * 
 * @return struktura odeslané zprávy, nebo NULL v případě chyby
 */
message_t *handle_communication(message_t *received, client_t *client) {
    if (received->bytes[0] == '#') {
        return NULL;
    }
    
    out("%sPřijata zpráva s požadavkem: \"%s\"",
            client_to_string(client), received->bytes);

    message_t *sent = create_message(received->length);
    
    if (sent == NULL) {
        err("%sChyba při vytváření zprávy pro odpověď na požadavek",
                client_to_string(client));
        
        return NULL;
    }
    
    out("%sVytvořena zpráva pro odpověď na požadavek",
            client_to_string(client));
    
    int32_t i;
    int32_t n = received->length;
    for (i = 0; i < n; i++) {
        sent->bytes[i] = received->bytes[n - i - 1];
    }
    sent->bytes[i] = '\0';
    
    if (!send_message(sent, client->sock)) {
        err("%sChyba při odesílání zprávy s odpovědí na požadavek",
                client_to_string(client));
        
        return sent;
    }
    
    out("%sOdeslána zpráva s odpovědí na požadavek: \"%s\"",
            client_to_string(client), sent->bytes);
    decrement_message_count(client);
    
    return sent;
}

/**
 * Rozešle předanou zpracovanou hromadnou zprávu všem klientům.
 * 
 * @param sent struktura zprávy k odeslání
 * @param cli_str řetězcová reprezentace klienta - odesílatele
 * 
 * @return true, pokud se podařilo odeslat zprávu všem klientům, jinak false
 */
bool broadcast_message(message_t *sent, char *cli_str) {
    linked_list_node_t *node;
    bool success = true;
    
    for (node = client_list->first; node != NULL; node = node->next) {
        client_t *client = (client_t *) node->value;
        
        if (!send_message(sent, client->sock)) {
            err("%sChyba při odesílání hromadné zprávy klientovi s ID %d",
                cli_str, client->id);
            success = false;
            
            continue;
        }
        
        out("%sOdeslána hromadná zpráva klientovi s ID %d",
            cli_str, sent->bytes);
    }
    
    return success;
}

/**
 * Zpracuje požadavek klienta určený k hromadnému odeslání. Zpracovaná zpráva
 * je poté rozeslána jako hromadná zpráva všem klientům.
 * 
 * @param received struktura přijaté zprávy
 * @param client struktura klienta
 * 
 * @return struktura odeslané zprávy, nebo NULL v případě chyby
 */
message_t *handle_broadcast(message_t *received, client_t *client) {
    out("%sPřijata zpráva s broadcastový požadavkem: \"%s\"",
            client_to_string(client), received->bytes + 1);

    message_t *sent = create_message(received->length - 1);
    
    if (sent == NULL) {
        err("%sChyba při vytváření zprávy pro odpověď na broadcastový požadavek",
                client_to_string(client));
        
        return NULL;
    }
    
    out("%sVytvořena zpráva pro odpověď na broadcastový požadavek",
            client_to_string(client));
    
    int32_t i;
    int32_t n = received->length;
    for (i = 1; i < n; i++) {
        sent->bytes[i - 1] = received->bytes[n - i];
    }
    sent->bytes[i - 1] = '\0';
    
    decrement_message_count(client);
    
    if (!broadcast_message(sent, client_to_string(client))) {
        err("%sChyba při rozesílání hromadné zprávy některému z klientů: \"%s\"",
                client_to_string(client));
        
        return sent;
    }
    
    out("%sRozeslána hromadná zpráva všem klientům: \"%s\"",
            client_to_string(client), sent->bytes);
    
    return sent;
}

/**
 * Zpracuje přijatou zprávu podle jejího typu, odešle odpověď, a v případě
 * úspěšného zpracování vrátí vytvořenou odpověď.
 * 
 * @param received struktura přijaté zprávy
 * @param client struktura klienta
 * 
 * @return struktura odeslané zprávy, nebo NULL v případě chyby
 */
message_t *send_server_response(message_t *received, client_t *client) {
    message_t *sent = NULL;
    
    sent = handle_ping(received, client);
    
    if (sent != NULL) {
        return sent;
    }
    
    sent = handle_login(received, client);
    
    if (sent != NULL) {
        return sent;
    }
    
    sent = handle_logout(received, client);
    
    if (sent != NULL) {
        return sent;
    }
    
    sent = handle_communication(received, client);
    
    if (sent != NULL) {
        return sent;
    }
    
    return handle_broadcast(received, client);
}

/**
 * Alokuje strukturu klienta s předaným ID a deskriptorem socketu.
 * 
 * @param id klientské ID
 * @param sock deskriptor socketu
 * 
 * @return alokovaná struktura klienta nebo NULL při chybě alokace
 */
client_t *create_client(int32_t id, int sock) {
    client_t *client = (client_t *) malloc(sizeof(client_t));
    
    if (client == NULL) {
        return NULL;
    }
    
    client->id = id;
    client->sock = sock;
    client->login = NULL;
    client->connected = true;
    client->messages_left = MSG_CNT;
    
    return client;
}

/**
 * Spustí vlákno předaného klienta pro paralelní zpracovávání jeho požadavků.
 * 
 * @param arg struktura klienta
 */
void *run_client(void *arg) {
    client_t *client = (client_t *) arg;
    
    while (client->connected == true) {
        message_t *received = receive_client_request(client);
        message_t *sent = NULL;
        
        if (received == NULL) {
            continue;
        }
        
        sent = send_server_response(received, client);
        delete_message(received);
        
        if (sent == NULL) {
            continue;
        }
        
        delete_message(sent);
    }
    
    delete_client(client);
}

/**
 * Uvolní paměť předané struktury klienta.
 * 
 * @param client struktura klienta
 */
void delete_client(client_t *client) {
    if (client == NULL) {
        err("Chyba při odstraňování dat klienta");
        
        return;
    }
    
    char *cli_str = client_to_string(client);
    
    if (close(client->sock) < 0) {
        err("%sChyba při uzavírání socketu", cli_str);
    }
    else {
        out("%sSocket byl úspěšně uzavřen", cli_str);
    }
    
    free(client);
    
    out("%sData byla úspěšně odstraněna", cli_str);
}