package interaction;

import configuration.Config;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Petr Kozler
 */
public class CmdArgHandler {
    
    /**
     * Číslo portu serveru.
     */
    private int port = Config.DEFAULT_PORT;
    
    /**
     * Hostname nebo adresa serveru.
     */
    private String host = Config.DEFAULT_HOST;

    public CmdArgHandler(String[] args) {
        int i = 0;
        String arg;

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
            switch (arg) {
                case Config.HOST_OPTION:
                    System.out.print(setPort(i < args.length ? args[i++] : null));
                    break;
                case Config.PORT_OPTION:
                    System.out.print(setPort(i < args.length ? args[i++] : null));
                    break;
                default:
                    System.out.printf("TcpClient: neplatný argument %s\n" + arg);
                    break;
            }
        }
    }
    
    public String setPort(String portArg) {
        String result = "";
        
        if (portArg != null && !portArg.isEmpty()) {
            try {
                port = Integer.parseInt(portArg);
            }
            catch (NumberFormatException e) {
                result = String.format("%s není číslo, nastavena výchozí hodnota %d",
                        Config.PORT_OPTION, Config.DEFAULT_PORT);
                port = Config.DEFAULT_PORT;
            }
            finally {
                if (port >= Config.MAX_PORT) {
                    result = String.format("%s není v rozsahu <%d",
                            Config.PORT_OPTION, Config.MAX_PORT);
                }
            }
        }
        else {
            result = String.format("%s vyžduje číslo portu (<%d)",
                    Config.PORT_OPTION, Config.MAX_PORT);
        }
        
        return result + "\n";
    }

    public String setHost(String hostArg) {
        String result = "";
        
        if (hostArg != null && !hostArg.isEmpty()) {
            host = hostArg;
        }
        else {
            result = String.format("%s vyžaduje hostname nebo IP adresu serveru",
                    Config.HOST_OPTION);
        }
        
        return result + "\n";
    }
    
    public int getPort() {
        return port;
    }

    public InetAddress getHost() throws UnknownHostException {
        return InetAddress.getByName(host);
    }

    public static void usage() {
        System.out.println("TCP klient hry \"Piškvorky pro více hráčů\" (Verze 2.0)");
        System.out.println("Seminární práce z předmětu \"Úvod do počítačových sítí\" (KIV/UPS)");
        System.out.println("Autor: Petr Kozler (A13B0359P), 2017\n");
        System.out.println("Použití:   Client.jar [<host> [<port> [<nick>]]]");
        System.out.println("Popis parametrů:");
        System.out.println("   <host> ... IP adresa nebo název serveru");
        System.out.println("   <port> ... celé číslo v rozsahu " + Config.MIN_PORT + " - " + Config.MAX_PORT);
        System.out.println("   <nick> ... přezdívka hráče");
        System.out.println("Příklad:   Client.jar " + Config.DEFAULT_HOST + " " + Config.DEFAULT_PORT);

        System.exit(0);
    }
    
}
