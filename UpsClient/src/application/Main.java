package application;

import java.awt.EventQueue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import communication.ConnectionManager;
import configuration.Config;
import visualization.MainWindow;

/**
 * Hlavní třída aplikace, která slouží jako TCP klient pro hru Piškvorky pro více hráčů.
 */
class Main {
	
	/**
	 * Vypíše nápovědu do příkazové řádky a ukončí program.
	 */
	private static void printHelpAndExit() {
		System.out.println("TCP klient hry \"Piškvorky pro více hráčů\" (Verze 1.0)");
		System.out.println("Seminární práce z předmětu \"Úvod do počítačových sítí\" (KIV/UPS)");
		System.out.println("Autor: Petr Kozler (A13B0359P), 2016\n");
		System.out.println("Použití:   Client.jar [<host>:<port>]");
	    System.out.println("Příklad:   Client.jar 127.0.0.1:10001");
	    System.out.println("Specifikace parametrů:");
	    System.out.println("   <host> ... IP adresa nebo název serveru");
	    System.out.println("   <port> ... celé číslo v rozsahu 1 - 65535");
	    
	    System.exit(0);
	}
	
	/**
	 * Provede validaci portu.
	 */
	private static int validatePort(String str) throws NumberFormatException {
		int port = Integer.parseInt(str);

		if (port < 1 || port > 65535) {
			throw new NumberFormatException();
		}
		
		return port;
	}
	
	/**
	 * Provede validaci IP adresy.
	 */
	private static InetAddress validateHost(String str) throws UnknownHostException {
		return InetAddress.getByName(str);
	}
	
	/**
	 * Získá IP adresu a port socketu od uživatele prostřednictvím dialogového okna.
	 */
	private static InetSocketAddress enterHostAndPort(boolean invalidArgs) {
		boolean errorMessage = invalidArgs;

		JLabel hostLabel = new JLabel("Zadejte IP adresu nebo název serveru");
		JTextField hostInput = new JTextField();
		hostInput.setText(Config.DEFAULT_HOST);
		JLabel portLabel = new JLabel("Zadejte port");
		JTextField portInput = new JTextField();
		portInput.setText(Integer.toString(Config.DEFAULT_PORT));
		JComponent[] inputs;
		
		while (true) {
			if (errorMessage) {
				inputs = new JComponent[] {new JLabel("Neplatný vstup."),
						hostLabel, hostInput, portLabel, portInput};
			}
			else {
				inputs = new JComponent[] {hostLabel, hostInput, portLabel, portInput};
			}
			
			if (JOptionPane.showConfirmDialog(
					null, inputs, "Adresa a port serveru", JOptionPane.OK_CANCEL_OPTION)
					== JOptionPane.CANCEL_OPTION) {
				System.exit(0);
			}

			InetAddress host = null;
			int port = 0;
			
			try {
				host = validateHost(hostInput.getText());
				port = validatePort(portInput.getText());
			}
			catch (UnknownHostException | NumberFormatException e) {
				errorMessage = true;
				continue;
			}
			
			return new InetSocketAddress(host, port);
		}
	}
	
	/**
	 * Získá IP adresu a port socketu z argumentu příkazové řádky.
	 */
	private static InetSocketAddress parseAddress(String arg) throws IllegalArgumentException {
		if (!arg.contains(":")) {
			throw new IllegalArgumentException();
		}
		
		StringTokenizer st = new StringTokenizer(arg, ":");
		InetAddress host = null;
		int port = 0;
		
		try {
			host = validateHost(st.nextToken());
			port = validatePort(st.nextToken());
		} 
		catch (UnknownHostException | NumberFormatException e) {
			throw new IllegalArgumentException();
		}
		
		return new InetSocketAddress(host, port);
	}
	
	/**
	 * Zkontroluje argumenty příkazové řádky a pokud je argumentem
	 * platná adresa a port, předá je jako návratovou hodnotu.
	 * Pokud není, vyžádá je od uživatele.
	 */
	private static InetSocketAddress getAddressFromArgs(String args[]) {
		if (args.length > 0) {
			if ("-h".equals(args[0]) | "--help".equals(args[0])) {
				printHelpAndExit();
			}
			
			try {
				return parseAddress(args[0]);
			}
			catch (IllegalArgumentException e) {
				return enterHostAndPort(true);
			}
		}
		else {
			return enterHostAndPort(false);
		}
	}
	
	/**
	 * Vytvoří hlavní okno aplikace.
	 */
	private static void launchMainWindow(final ConnectionManager connectionManager) {
		EventQueue.invokeLater(new Runnable() {
			
			/*
			 * vytvoření okna
			 */
			@Override
			public void run() {
				try {
					new MainWindow(connectionManager);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Vstupní bod programu.
	 */
	public static void main(String args[]) throws Exception {
		InetSocketAddress address = getAddressFromArgs(args);
		ConnectionManager connectionManager = new ConnectionManager(address);
		launchMainWindow(connectionManager);
	}
	
}