package application;

import java.nio.charset.StandardCharsets;

/**
 * Třída Message představuje přepravku, která uchovává data zprávy.
 * 
 * @author Petr Kozler
 */
public class Message {

    /**
     * Znaky zprávy.
     */
    private byte[] string;
    
    /**
     * Typ zprávy.
     */
    private MessageType messageType;
    
    /**
     * Vytvoří test odezvy.
     */
    public Message() {
        this.string = null;
    }
    
    /**
     * Vytvoří novou přepravku pro zprávu z pole bajtů představujících znaky
     * přijaté zprávy a rozpozná typ této zprávy.
     * 
     * @param string znaky zprávy
     */
    public Message(byte[] string) {
        this.string = string;
        String s = new String(this.string, StandardCharsets.US_ASCII);
        
        if (s.startsWith(Configuration.LOGIN_ACK_STR + " ")
                && s.length() > Configuration.LOGIN_ACK_STR.length() + 1) {
            messageType = MessageType.LOGIN_ACK;
        }
        else if (s.toLowerCase().equals(Configuration.CLOSE_MSG_STR.toLowerCase())
                && s.length() == Configuration.CLOSE_MSG_STR.length()) {
            messageType = MessageType.LOGOUT_NOTIFICATION;
        }
        else {
            messageType = MessageType.MESSAGE_RESPONSE;
        }
    }
    
    /**
     * Vytvoří novou přepravku pro zprávu ze standardního řetězce určenou
     * k odeslání.
     * 
     * @param string znaky zprávy
     */
    public Message(String string) {
        this.string = string.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * Určí, zda je zpráva test odezvy (nulová zpráva).
     * 
     * @return true, pokud je zpráva nulová, jinak false
     */
    public boolean isPing() {
        return string == null;
    }
    
    /**
     * Vrátí rozpoznaný typ zprávy nebo hodnotu null, pokud je zpráva vytvořena
     * klientem k odeslání.
     * 
     * @return typ zprávy, pokud je přijata ze serveru, jinak null
     */
    public MessageType getMessageType() {
        return messageType;
    }
    
    /**
     * Vrátí znaky zprávy k odeslání.
     * 
     * @return znaky zprávy
     */
    public byte[] getString() {
        return string;
    }

    /**
     * Vrátí řetězcovou reprezentaci přijaté zprávy.
     * 
     * @return řetězec zprávy
     */
    @Override
    public String toString() {
        return new String(string, StandardCharsets.US_ASCII);
    }
    
}
