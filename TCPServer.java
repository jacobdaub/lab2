package l2;

import java.net.*;
import java.beans.Expression;
import java.io.*;
import java.util.*;
import l2.formatted_msg.CTRL;

public class TCPServer {
	static ServerSocket listenSocket;

	public static void main(String args[]) {
		int serverPort = 7896; // the server port
		List<Connection> all_connections = new ArrayList<Connection>();
		try {
			InetAddress addr = InetAddress.getLocalHost();

			// Get IP Address
			byte[] ipAddr = addr.getAddress();

			// Get hostname
			String hostname = addr.getHostName();
			System.out.println("Server Name: " + hostname + "\nServer Port: " + serverPort);
		} catch (UnknownHostException e) {
		}

		try {
			listenSocket = new ServerSocket(serverPort);
			System.out.println("Server is Ready");
			while (true) {
				System.out.println("listening to client sockets");
				Socket clientSocket = listenSocket.accept();
				System.out.println("connection found, creating a new connection thread");
				Connection c = new Connection(clientSocket);
				all_connections.add(c);
				c.all_connections = all_connections;
			}
		} catch (IOException e) {
			System.out.println("IOException Listen socket:" + e.getMessage());
		}
	}
}

class Connection extends Thread {
	ObjectInputStream in;
	ObjectOutputStream out;
	Socket clientSocket;
	String name;
	List<Connection> all_connections;

	public Connection(Socket aClientSocket) {
		System.out.println("creating a new connection for client");
		try {
			clientSocket = aClientSocket;
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}

	}

	public void set_my_name(String s) {
		name = s;
	}

	public String get_my_name() {
		return name;
	}

	public ObjectInputStream get_in() {
		return in;
	}

	public ObjectOutputStream get_out() {
		return out;
	}

	public void run() {
		System.out.println("server thread started");
		while (true){
			try {
				formatted_msg msg;
				msg = (formatted_msg) in.readObject();
				CTRL mode = msg.get_ctrl();
				boolean flag = false;// flag for checking if recipient exist
				switch (mode) {
				case NORMAL:
					System.out.println("This is message for " + msg.get_dest());
					Connection c;
					for (int i = 0; i < all_connections.size(); i++) {
						c = all_connections.get(i);
						if ((c.get_my_name().equals(msg.get_dest()))) {
							ObjectOutputStream reciver = c.get_out();
							reciver.writeObject(msg);
							flag = true;
						}
					}
					// checking if connection exists
					if (flag != true) {
						formatted_msg sendBack = new formatted_msg("Server", "Recipient does not exist");
						out.writeObject(sendBack);
					}
					break;
				case TERMINATE:
					msg.set_msg("Connection is terminated");
					out.writeObject(msg);
					Thread.sleep(1000);// waiting
					terminate();
					break;
				case LOOPBACK:
					System.out.println("This is loop back: " + msg);
					out.writeObject(msg);
					break;
				case BROADCAST:
					System.out.println("This is Broadcast");
					Connection b;
					for (int i = 0; i < all_connections.size(); i++) {
						b = all_connections.get(i);
						ObjectOutputStream reciver = b.get_out();
						reciver.writeObject(msg);
					}
					break;
				case SETUP:
					System.out.println("Connection with " + msg.get_from() + " is established");
					set_my_name(msg.get_from());
					break;
				case GET_ALL_CLIENTS:
					System.out.println("This is client check");
					Connection a;
					String clients = "";
					for (int i = 0; i < all_connections.size(); i++) {
						a = all_connections.get(i);
						clients += a.get_my_name();
						if (i != all_connections.size() + 1)
							clients += ",";
					}
					formatted_msg m = new formatted_msg("server", clients);
					out.writeObject(m);
					break;
				}

				System.out.println("num connection " + all_connections.size());
				Thread.sleep(50);
			}
		 catch (EOFException e) {
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("readline:" + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("readline:" + e.getMessage());
		}
			
		}
	}

	public void terminate() {
		try {
			clientSocket.close();
			for (int i = 0; i < all_connections.size(); i++) {
				if (this == (Connection) all_connections.get(i)) {
					System.out.println("Removing connection from the list, for " + name);
					System.out.println("num connection upon removing " + all_connections.size());
					all_connections.remove(i);
					break;
				}
			}
		} catch (IOException e) {
		}
	}

}