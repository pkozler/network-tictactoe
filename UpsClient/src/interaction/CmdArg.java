package interaction;

import configuration.Config;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Třída CmdArg slouží ke zpracování argumentů příkazové řádky.
 * 
 * @author Petr Kozler
 */
public class CmdArg {
    
    /**
     * Číslo portu serveru.
     */
    private Integer port;
    
    /**
     * Hostname nebo adresa serveru.
     */
    private InetAddress host;
    
    /**
     * Zpracuje předané argumenty.
     * 
     * @param args argumenty příkazové řádky
     */
    public CmdArg(String[] args) {
        if (args.length < 1) {
            usage();
        }
        
        int i = 0;
        String arg;
        
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
            switch (arg) {
                case Config.HOST_OPTION:
                    InetAddress host = null;
                    
                    if (i < args.length) {
                        try {
                            host = InetAddress.getByName(args[i++]);
                        }
                        catch (UnknownHostException e) {
                            System.out.printf("%s je neznámý hostname nebo adresa\n",
                                    Config.HOST_OPTION);
                        }
                    }
                    else {
                        System.out.printf("%s vyžaduje hostname nebo IP adresu serveru\n",
                                Config.HOST_OPTION);
                    }
                    
                    if (host != null) {
                        this.host = host;
                    }
                    else {
                        try {
                            this.host = InetAddress.getByName(Config.DEFAULT_HOST);
                            System.out.printf("Použita výchozí hodnota %s\n", Config.DEFAULT_HOST);
                        }
                        catch (UnknownHostException ex) {
                            System.out.printf("Chyba při nastavování %s na výchozí hodnotu %s\n",
                                    Config.HOST_OPTION, Config.DEFAULT_HOST);
                            
                            System.exit(1);
                        }
                    }
                    break;
                case Config.PORT_OPTION:
                    Integer port = null;
                    
                    if (i < args.length) {
                        try {
                            port = Integer.parseInt(args[i++]);
                            
                            if (port < Config.MIN_PORT || port > Config.MAX_PORT) {
                                System.out.printf("%s není v rozsahu %d - %d\n",
                                        Config.PORT_OPTION, Config.MIN_PORT, Config.MAX_PORT);
                                port = null;
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.printf("%s není číslo\n", Config.PORT_OPTION);
                        }
                    }
                    else {
                        System.out.printf("%s vyžaduje číslo portu v rozsahu %d - %d\n",
                                Config.PORT_OPTION, Config.MIN_PORT, Config.MAX_PORT);
                    }
                    
                    if (port != null) {
                        this.port = port;
                    }
                    else {
                        this.port = Config.DEFAULT_PORT;
                        System.out.printf("Použita výchozí hodnota %d\n", Config.DEFAULT_PORT);
                    }
                    break;
                default:
                    System.out.printf("Neplatný parametr %s\n", arg);
                    break;
            }
        }
        
        boolean hasParams = true;
        
        if (this.host == null) {
            hasParams = false;
            System.out.printf("Nebyl zadán parametr %s\n", Config.HOST_OPTION);
        }
        
        if (this.port == null) {
            hasParams = false;
            System.out.printf("Nebyl zadán parametr %s\n", Config.PORT_OPTION);
        }
        
        if (!hasParams) {
            usage();
        }
    }
    
    /**
     * Vrátí adresu nebo název serveru.
     * 
     * @return adresa nebo název serveru
     */
    public InetAddress getHost() {
        return host;
    }
    
    /**
     * Vrátí port serveru.
     * 
     * @return port serveru
     */
    public Integer getPort() {
        return (int) port;
    }
    
    /**
     * Vypíše nápovědu ke spuštění programu.
     */
    private void usage() {
        final String name = "Client.jar";
        
        System.out.printf("\n");
        System.out.printf("TCP klient hry \"Piškvorky pro více hráčů\" (Verze 2.0)\n");
        System.out.printf("Seminární práce z předmětu \"Úvod do počítačových sítí\" (KIV/UPS)\n");
        System.out.printf("Autor: Petr Kozler (A13B0359P), 2017\n");
        System.out.printf("\n");
        System.out.printf("Použití:    %s %s <host> %s <port>\n",
                name, Config.HOST_OPTION, Config.PORT_OPTION);
        System.out.printf("Popis parametrů:\n");
        System.out.printf("    <host> ... IP adresa nebo název serveru\n");
        System.out.printf("    <port> ... celé číslo v rozsahu %d - %d\n",
                Config.MIN_PORT, Config.MAX_PORT);
        System.out.printf("Příklad:    %s %s %s %s %d\n",
                name, Config.HOST_OPTION, Config.DEFAULT_HOST, Config.PORT_OPTION, Config.DEFAULT_PORT);
        System.out.printf("\n");
        
        System.exit(0);
    }
    
}
