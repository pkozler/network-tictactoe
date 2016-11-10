package application;

/**
 * Třída ArgParser uchovává argumenty programu a poskytuje metody
 * pro jejich zpracování.
 * 
 * @author Petr Kozler
 */
class ArgParser {

    /**
     * Číslo portu serveru.
     */
    private int port = Configuration.DEFAULT_PORT;
    
    /**
     * Hostname nebo adresa serveru.
     */
    private String host = Configuration.DEFAULT_HOST;

    /**
     * Zpracuje předané argumenty příkazové řádky.
     * 
     * @param args argumenty příkazové řádky (IP adresa a port pro naslouchání)
     */
    public ArgParser(String[] args) {
        int i = 0;
        String arg;

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
            switch (arg) {
                case Configuration.HOST_OPTION:
                    if (i < args.length) {
                        host = args[i++];
                    } else {
                        System.out.printf("%s vyžaduje hostname nebo IP adresu serveru",
                                Configuration.HOST_OPTION);
                    }   break;
                case Configuration.PORT_OPTION:
                    if (i < args.length) {
                        try {
                            port = Integer.parseInt(args[i++]);
                        } catch (NumberFormatException e) {
                            System.out.printf("%s není číslo, nastavena výchozí hodnota %d",
                                    Configuration.PORT_OPTION, Configuration.DEFAULT_PORT);
                            port = Configuration.DEFAULT_PORT;
                        } finally {
                            if (port >= Configuration.MAX_PORT) {
                                System.out.printf("%s není v rozsahu <%d",
                                        Configuration.PORT_OPTION, Configuration.MAX_PORT);
                            }
                        }
                    } else {
                        System.out.printf("%s vyžduje číslo portu (<%d)",
                                Configuration.PORT_OPTION, Configuration.MAX_PORT);
                    }   break;
                default:
                    System.out.printf("TcpClient: neplatný argument %s" + arg);
                    break;
            }
        }
    }

    /**
     * Vrátí číslo portu serveru.
     * 
     * @return číslo portu serveru
     */
    public int getPort() {
        return port;
    }

    /**
     * Vrátí hostname nebo adresu serveru.
     * 
     * @return hostname nebo adresu serveru
     */
    public String getHost() {
        return host;
    }
    
    /**
     * Vypíše nápovědu ke spuštění programu.
     */
    public void usage() {
        System.out.printf("Použití: TcpClient [%s <číslo portu>] [%s <hostname nebo IP adresa>]",
                Configuration.PORT_OPTION, Configuration.HOST_OPTION);
    }
    
}
