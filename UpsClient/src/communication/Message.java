package communication;

import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.AMessageStringToken;
import configuration.Protocol;

/**
 * Třída Message slouží jako přepravka pro uchovávání zpráv vyměňovaných mezi serverem
 a klientem v rámci textového aplikačního protokolu postaveného nad transportním
 protokolem TCP, a pro usnadnění manipulace s těmito zprávami.
 * Kromě atributů představujících jednotlivé části zprávy (typ zprávy a seznam argumentů)
 * třída obsahuje metody pro kontrolu typu zprávy, pro validaci jejích argumentů
 * a pro snadný převod mezi instancí přepravky zprávy a řetězcovou podobou zprávy
 * určenou k přenosu po síti.
 * 
 * @author Petr Kozler
 */
public class Message {
    
    /**
     * typ zprávy
     */
    private String type;
    
    /**
     * argumenty zprávy
     */
    private String[] args;
    
    /**
     * index aktuálního argumentu zprávy
     */
    private int current;
    
    /**
     * Vytvoří novou zprávu z předaného typu a argumentů.
     * 
     * @param type typ zprávy
     * @param args argumenty zprávy
     */
    public Message(String type, String... args) {
        this.type = type;
        this.args = args;
        
        current = 0;
    }
    
    /**
     * Vytvoří novou zprávu z předaného řetězce.
     * 
     * @param msgStr řetězec zprávy
     */
    public Message(String msgStr) {
        if (msgStr == null) {
            msgStr = "";
        }
        
        if (msgStr.isEmpty()) {
            type = null;
            args = null;
            
            return;
        }
        
        int firstDelimIndex = msgStr.indexOf(Protocol.SEPARATOR);
        
        if (firstDelimIndex < 0) {
            type = msgStr;
            
            return;
        }
        
        type = msgStr.substring(0, firstDelimIndex);
        args = msgStr.substring(firstDelimIndex + 1).split(Protocol.SEPARATOR);
    }
    
    /**
     * Vytvoří novou prázdnou zprávu pro testování odezvy.
     */
    public Message() {
        this(null);
    }
    
    /**
     * Otestuje, zda má zpráva určený typ.
     * 
     * @return true, pokud má zpráva typ, jinak false
     */
    public boolean hasTypeToken() {
        return type != null;
    }
    
    /**
     * Otestuje, zda typ zprávy odpovídá předanému.
     * 
     * @param messageType typ zprávy
     * @return true, pokud typ odpovídá, jinak false
     */
    public boolean isTypeOf(AMessageStringToken messageType) {
        return hasTypeToken() && type.equals(messageType.KEYWORD);
    }
    
    /**
     * Otestuje, zda má zpráva argumenty.
     * 
     * @return true, má-li zpráva argumenty, jinak false
     */
    public boolean hasArgs() {
        return args != null && args.length > 0;
    }
    
    /**
     * Otestuje, zda zpráva slouží k testování odezvy.
     * 
     * @return true, pokud zpráva testuje odezvu, jinak false
     */
    public boolean isPing() {
        return !hasTypeToken() && !hasArgs();
    }
    
    /**
     * Otestuje, zda má zpráva další argument.
     * 
     * @return true, pokud má další argument, jinak false
     */
    public boolean hasNextArg() {
        return current < args.length;
    }
    
    /**
     * Získá další argument zprávy.
     * 
     * @return argument
     * @throws MissingMessageArgsException 
     */
    public String getNextArg() throws MissingMessageArgsException {
        if (!hasNextArg()) {
            throw new MissingMessageArgsException();
        }
        
        return args[current++];
    }
    
    /**
     * Získá další argument zprávy převedený na celé číslo.
     * 
     * @return argument převedený na celé číslo
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public int getNextIntArg() throws InvalidMessageArgsException, MissingMessageArgsException {
        try {
            return Integer.parseInt(getNextArg());
        }
        catch (NumberFormatException ex) {
            throw new InvalidMessageArgsException();
        }
    }
    
    /**
     * Získá další argument zprávy převedený na celé číslo
     * o zadané minimální hodnotě.
     * 
     * @param minValue minimální hodnota
     * @return argument převedený na celé číslo
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public int getNextIntArg(int minValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        int i = getNextIntArg();
        
        if (i < minValue) {
            throw new InvalidMessageArgsException();
        }
        
        return i;
    }
    
    /**
     * Získá další argument zprávy převedený na celé číslo
     * o zadané minimální a maximální hodnotě.
     * 
     * @param minValue minimální hodnota
     * @param maxValue maximální hodnota
     * @return argument převedený na celé číslo
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public int getNextIntArg(int minValue, int maxValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        int i = getNextIntArg(minValue);
        
        if (i > maxValue) {
            throw new InvalidMessageArgsException();
        }
        
        return i;
    }
    
    /**
     * Získá další argument zprávy převedený na bajt.
     * 
     * @return argument převedený na bajt
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public byte getNextByteArg() throws InvalidMessageArgsException, MissingMessageArgsException {
        try {
            return Byte.parseByte(getNextArg());
        }
        catch (NumberFormatException ex) {
            throw new InvalidMessageArgsException();
        }
    }
    
    /**
     * Získá další argument zprávy převedený na bajt
     * o zadané minimální hodnotě.
     * 
     * @param minValue
     * @return argument převedený na bajt
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public byte getNextByteArg(byte minValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        byte b = getNextByteArg();
        
        if (b < minValue) {
            throw new InvalidMessageArgsException();
        }
        
        return b;
    }
    
    /**
     * Získá další argument zprávy převedený na bajt
     * o zadané minimální a maximální hodnotě.
     * 
     * @param minValue
     * @param maxValue
     * @return argument převedený na bajt
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public byte getNextByteArg(byte minValue, byte maxValue) throws InvalidMessageArgsException, MissingMessageArgsException {
        byte b = getNextByteArg(minValue);
        
        if (b > maxValue) {
            throw new InvalidMessageArgsException();
        }
        
        return b;
    }
    
    /**
     * Získá další argument zprávy převedený logickou hodnotu.
     * 
     * @return argument převedený na logickou hodnotu
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public boolean getNextBoolArg() throws InvalidMessageArgsException, MissingMessageArgsException {
        String arg = getNextArg();
        
        if (Protocol.MSG_TRUE.equals(arg)) {
            return true;
        }
        
        if (Protocol.MSG_FALSE.equals(arg)) {
            return false;
        }
        
        throw new InvalidMessageArgsException();
    }

    /**
     * Převede zprávu na řetězcovou reprezentaci.
     * 
     * @return řetězcová reprezentace zprávy
     */
    @Override
    public String toString() {
        if (hasTypeToken()) {
            String msgStr = type;
            
            if (hasArgs()) {
                return msgStr + Protocol.SEPARATOR + joinArgs(Protocol.SEPARATOR, args);
            }
            
            return msgStr;
        }
        
        if (!hasTypeToken() && !hasArgs()) {
            return "";
        }
        
        // zpráva má argumenty, ale nemá typ - neplatná zpráva
        return null;
    }
    
    /**
     * Spojí argumenty zprávy do řetězce.
     * 
     * @param separator oddělovač argumentů
     * @param args argumenty
     * @return řetězec
     */
    private static String joinArgs(String separator, String[] args) {
        if (args.length < 1) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(args[0]);
        
        for (int i = 1; i < args.length; i ++) {
            sb.append(separator).append(args[i]);
        }
        
        return sb.toString();
    }
    
}
