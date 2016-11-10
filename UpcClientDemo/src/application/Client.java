
package application;

import java.io.*;
import java.net.*;

/**
 * Třída Client implementuje rozhraní, poskytující metody pro základní
 * síťové operace s využitím transportního protokolu TCP a představuje
 * tak jádro TCP klienta.
 * 
 * @author Petr Kozler
 */
public class Client implements IClient {

    /**
     * TCP socket pro spojení se serverem.
     */
    private Socket socket;
    
    /**
     * Objekt čtečky přijatých zpráv.
     */
    private DataInputStream reader;
    
    /**
     * Objekt zapisovače zpráv k odeslání.
     */
    private DataOutputStream writer;
    
    /**
     * Přepravka uchovávající adresu serveru.
     */
    private InetAddress address;
    
    /**
     * Číslo portu serveru.
     */
    private Integer port;
    
    /**
     * Přihlašovací jméno klienta.
     */
    private String login;

    /**
     * Vytvoří spojení se serverem a otevře vstupní a výstupní proudy
     * pro příjem a odesílání zpráv.
     * 
     * @param host adresa nebo hostname serveru
     * @param port číslo portu serveru
     * 
     * @throws ClientException chyba síťové operace
     */
    @Override
    public void connect(String host, int port) throws ClientException {
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(Configuration.SOCKET_TIMEOUT_MILLIS);
        } catch (IOException e) {
            throw new ClientException("Připojení k %s:%d odmítnuto.", host, port);
        } catch (IllegalArgumentException e) {
            throw new ClientException("Neplatný port serveru - číslo musí být v rozsahu %d - %d.",
                    0, Configuration.MAX_PORT);
        } catch (NullPointerException e) {
            throw new ClientException("Nezadán hostname nebo IP adresa serveru.");
        }
        
        try {
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new ClientException("Chyba otevření socketu: %s",
                e.getLocalizedMessage());
        }
        
        this.address = socket.getInetAddress();
        this.port = socket.getPort();
    }

    /**
     * Uzavře vstupní a výstupní proudy pro příjem a odesílání zpráv
     * a zruší spojení se serverem.
     * 
     * @throws ClientException chyba síťové operace
     */
    @Override
    public void disconnect() throws ClientException {
        login = null;
        address = null;
        port = null;
        
        try {
            reader.close();
            writer.close();
        } catch (IOException | NullPointerException e) {
            throw new ClientException("Chyba uzavření I/O proudu: %s",
                e.getLocalizedMessage());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            } catch (IOException e) {
                throw new ClientException("Chyba uzavření socketu: %s",
                e.getLocalizedMessage());
            }
        }
    }
    
    /**
     * Zjistí, zda je vytvořeno spojení se serverem.
     * 
     * @return true, pokud je klient připojen k serveru, jinak false
     */
    @Override
    public boolean isConnected() {
        return socket != null;
    }

    /**
     * Odešle zprávu jako požadavek klienta na server.
     * 
     * @param msg přepravka se zprávou k odeslání
     * 
     * @throws ClientException chyba síťové operace
     */
    @Override
    public void sendMessage(Message msg) throws ClientException {
        try {
            if (msg.isPing()) {
                writer.writeInt(0);
                writer.flush();
                
                return;
            }
            
            writer.writeInt(msg.getString().length);
            writer.flush();
            writer.write(msg.getString());
            writer.flush();
        } catch (IOException e) {
            disconnect();
            
            throw new ClientException("Chyba typu %s při zápisu dat do socketu: %s",
                e.getClass().getSimpleName(), e.getLocalizedMessage());
        }
    }
    
    /**
     * Přijme zprávu jako odpověď klientovi ze serveru.
     * 
     * @return přepravka s přijatou zprávou
     * 
     * @throws ClientException chyba síťové operace
     */
    @Override
    public Message receiveMessage() throws ClientException {
        int length;
        byte[] string;
                
        try {
            length = reader.readInt();
            
            if (length < 0) {
                throw new ClientException("Přijata neplatná délka zprávy: %s", length);
            }
            
            string = new byte[length];
            reader.read(string);
        }
        catch (EOFException eofe) {
            disconnect();
            
            throw new ClientException("Server se odpojil.");
        }
        catch (SocketTimeoutException ste) {
            disconnect();
            
            throw new ClientException("Detekován výpadek spojení.");
        }
        catch (IOException ioe) {
            throw new ClientException("Chyba typu %s při čtení dat ze socketu: %s",
                ioe.getClass().getSimpleName(), ioe.getLocalizedMessage());
        }
        
        if (length == 0) {
            return new Message();
        }
        
        Message message = new Message(string);
        
        if (message.getMessageType() == MessageType.LOGIN_ACK) {
            if (!isLogged()) {
                setLogin(message);
            }
        }
        else if (message.getMessageType() == MessageType.LOGOUT_NOTIFICATION) {
            if (isConnected()) {
                disconnect();
            }
        }
        else if (message.getMessageType() != MessageType.MESSAGE_RESPONSE) {
            throw new ClientException("Nebyl rozpoznán platný typ zprávy.");
        }
        
        return message;
    }

    /**
     * Vrátí přepravku s adresou serveru, ke kterému je klient připojen.
     * 
     * @return přepravka s adresou, pokud je klient připojen, jinak null
     */
    @Override
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Vrátí číslo portu serveru, ke kterému je klient připojen.
     * 
     * @return číslo portu, pokud je klient připojen, jinak null
     */
    @Override
    public Integer getPort() {
        return port;
    }
    
    /**
     * Vrátí uživatelem zvolené a serverem potvrzené přihlašovací jméno
     * (login) klienta po přihlášení k serveru nebo hodnotu null, pokud
     * není klient připojen k serveru nebo nebyl dosud přihlášen.
     * 
     * @return login klienta, pokud je připojen a přihlášen, jinak null
     */
    @Override
    public String getLogin() {
        return login;
    }
    
    /**
     * Nastaví serverem potvrzené přihlašovací jméno klienta z přijaté zprávy.
     * 
     * @param msg přepravka zprávy obsahující login spolu s potvrzením
     */
    @Override
    public void setLogin(Message msg) {
        login = msg.toString().substring(Configuration.LOGIN_ACK_STR.length()).trim();
    }
    
    /**
     * Zjistí, zda je klient přihlášen (má uživatelem zadané a serverem potvrzené
     * přihlašovací jméno).
     * 
     * @return true, pokud je klient přihlášen, jinak false
     */
    @Override
    public boolean isLogged() {
        return login != null;
    }

}
