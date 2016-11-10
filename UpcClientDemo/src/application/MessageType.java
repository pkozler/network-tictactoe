package application;

/**
 * Výčtový typ ResponseType představuje jednotlivé typy odpovědí serveru.
 * 
 * @author Petr Kozler
 */
public enum MessageType {
    LOGIN_ACK, // potvrzení loginu
    MESSAGE_RESPONSE, // odpověď na běžnou zprávu
    LOGOUT_NOTIFICATION // oznámení o odhlášení ze strany serveru
}
