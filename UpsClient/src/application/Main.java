package application;

import configuration.Config;
import visualisation.MainWindow;

/**
 *
 * @author Petr Kozler
 */
public class Main {
    
    private static final int MAX_ARGS = 3;
    
    private static void printHelpAndExit() {
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
    
    public static void main(String[] args) {
        if (args.length > MAX_ARGS) {
            printHelpAndExit();
        }

        String[] argv = new String[MAX_ARGS];

        int i;
        for (i = args.length; i < MAX_ARGS; i++) {
            argv[i] = null;
        }

        new MainWindow(argv[0], argv[1], argv[2]);
    }
    
}
