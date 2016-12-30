package interaction;

import configuration.Config;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Petr Kozler
 */
public class CmdArg {
    
    /**
     * Číslo portu serveru.
     */
    private int port = Config.DEFAULT_PORT;
    
    /**
     * Hostname nebo adresa serveru.
     */
    private String host = Config.DEFAULT_HOST;

    public CmdArg(String[] args) {
        int i = 0;
        String arg;

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
            switch (arg) {
                case Config.HOST_OPTION:
                    if (i < args.length) {
                        host = args[i++];
                    }
                    else {
                        System.out.printf("%s vyžaduje hostname nebo IP adresu serveru",
                                Config.HOST_OPTION);
                    }
                    break;
                case Config.PORT_OPTION:
                    if (i < args.length) {
                        try {
                            port = Integer.parseInt(args[i++]);
                        }
                        catch (NumberFormatException e) {
                            System.out.printf("%s není číslo, nastavena výchozí hodnota %d",
                                    Config.PORT_OPTION, Config.DEFAULT_PORT);
                            port = Config.DEFAULT_PORT;
                        }
                        finally {
                            if (port > Config.MAX_PORT || port < Config.MIN_PORT) {
                                System.out.printf("%s není v rozsahu %d - %d",
                                        Config.PORT_OPTION, Config.MIN_PORT, Config.MAX_PORT);
                            }
                        }
                    }
                    else {
                        System.out.printf("%s vyžaduje číslo portu (%d - %d)",
                                Config.PORT_OPTION, Config.MIN_PORT, Config.MAX_PORT);
                    }
                    break;
                default:
                    System.out.printf("Client.jar: neplatný argument %s\n" + arg);
                    break;
            }
        }
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
        System.out.println(String.format("Použití:   Client.jar [%s <host>] [%s <port>]",
                Config.HOST_OPTION, Config.PORT_OPTION));
        System.out.println("Popis parametrů:");
        System.out.println("   <host> ... IP adresa nebo název serveru");
        System.out.println(String.format("   <port> ... celé číslo v rozsahu %d - %d",
                Config.MIN_PORT, Config.MAX_PORT));
        System.out.println(String.format("Příklad:   Client.jar %s %s %s %d",
                Config.HOST_OPTION, Config.DEFAULT_HOST, Config.PORT_OPTION, Config.DEFAULT_PORT));
    }
    
}
