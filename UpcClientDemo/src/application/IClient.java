package application;

import java.net.InetAddress;

/**
 * Rozhraní IClient obsahuje metody pro základní síťové operace klienta,
 * zahrnující vytváření a rušení spojení se serverem, přihlašování a odhlašování
 * a vlastní komunikaci (příjem a odesílání zpráv).
 * 
 * @author Petr Kozler
 */
public interface IClient {

    public void connect(String host, int port) throws ClientException;

    public void disconnect() throws ClientException;

    public boolean isConnected();
    
    public void sendMessage(Message msg) throws ClientException;

    public Message receiveMessage() throws ClientException;
    
    public InetAddress getAddress();

    public Integer getPort();
    
    public String getLogin();
    
    public void setLogin(Message msg);
    
    public boolean isLogged();
    
}
