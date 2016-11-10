package communication;

import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.AMessageType;
import configuration.Config;

/**
 * Třída sloužící jako přepravka pro uchovávání zpráv vyměňovaných mezi serverem
 * a klientem v rámci textového aplikačního protokolu postaveného nad transportním
 * protokolem TCP, a pro usnadnění manipulace s těmito zprávami.
 * Kromě atributů představujících jednotlivé části zprávy (typ zprávy a seznam argumentů)
 * třída obsahuje metody pro kontrolu typu zprávy, pro validaci jejích argumentů
 * a pro snadný převod mezi instancí přepravky zprávy a řetězcovou podobou zprávy
 * určenou k přenosu po síti.
 * 
 * @author Petr Kozler
 */
public class Message {
    private String type;
    private String[] args;
    private int current;
    
    public Message(String type, String... args) {
        this.type = type;
        this.args = args;
        current = 0;
    }
    
    public Message(String msgStr) {
        if (msgStr.isEmpty()) {
            type = null;
            
            return;
        }
        
        int firstDelimIndex = msgStr.indexOf(Config.SEPARATOR);
        
        if (firstDelimIndex < 0) {
            type = msgStr;
            
            return;
        }
        
        type = msgStr.substring(0, firstDelimIndex);
        args = msgStr.substring(firstDelimIndex + 1).split(Config.SEPARATOR);
    }
    
    public Message() {
        this(null);
    }
    
    public boolean hasTypeToken() {
        return type != null;
    }
    
    public boolean isTypeOf(AMessageType messageType) {
        return hasTypeToken() && type.equals(messageType.KEYWORD);
    }
    
    public boolean hasArgs() {
        return args != null && args.length > 0;
    }
    
    public boolean isPing() {
        return !hasTypeToken() && !hasArgs();
    }
    
    public boolean hasNextArg() {
        return current < args.length;
    }
    
    public String getNextArg() throws MissingMessageArgsException {
        if (!hasNextArg()) {
            throw new MissingMessageArgsException();
        }
        
        return args[current++];
    }
    
    public int getNextIntArg() throws InvalidMessageArgsException, MissingMessageArgsException {
        try {
            return Integer.parseInt(getNextArg());
        }
        catch (NumberFormatException ex) {
            throw new InvalidMessageArgsException();
        }
    }
    
    public int getNextIntArg(int minValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        int i = getNextIntArg();
        
        if (i < minValue) {
            throw new InvalidMessageArgsException();
        }
        
        return i;
    }
    
    public int getNextIntArg(int minValue, int maxValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        int i = getNextIntArg(minValue);
        
        if (i > maxValue) {
            throw new InvalidMessageArgsException();
        }
        
        return i;
    }
    
    public byte getNextByteArg() throws InvalidMessageArgsException, MissingMessageArgsException {
        try {
            return Byte.parseByte(getNextArg());
        }
        catch (NumberFormatException ex) {
            throw new InvalidMessageArgsException();
        }
    }
    
    public byte getNextByteArg(byte minValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        byte b = getNextByteArg();
        
        if (b < minValue) {
            throw new InvalidMessageArgsException();
        }
        
        return b;
    }
    
    public byte getNextByteArg(byte minValue, byte maxValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        byte b = getNextByteArg(minValue);
        
        if (b > maxValue) {
            throw new InvalidMessageArgsException();
        }
        
        return b;
    }
    
    public boolean getNextBoolArg() throws InvalidMessageArgsException, MissingMessageArgsException {
        String arg = getNextArg();
        
        if (Config.MSG_TRUE.equals(arg)) {
            return true;
        }
        
        if (Config.MSG_FALSE.equals(arg)) {
            return false;
        }
        
        throw new InvalidMessageArgsException();
    }

    @Override
    public String toString() {
        if (hasArgs()) {
            String msgStr = type;

            if (!hasTypeToken()) {
                msgStr += Config.SEPARATOR + String.join(Config.SEPARATOR, args);
            }
            
            return msgStr;
        }
        
        if (!hasTypeToken() && !hasArgs()) {
            return "";
        }
        
        // zpráva má argumenty, ale nemá typ - neplatná zpráva
        return null;
    }
    
}
