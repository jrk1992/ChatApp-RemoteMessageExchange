

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	public static void main(String args[]) {

		ArrayList<InetAddress> allClients = new ArrayList<InetAddress>();
		ArrayList<Socket> clientSockets = new ArrayList<Socket>();
		ArrayList<EchoServer> allObjects = new ArrayList<EchoServer>();
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		
		try {
			if(args[0].trim().length()>1)
			{
				if(Integer.parseInt(args[0].trim())<1024)
						{
							System.out.println("Error:Enter valid port");
							System.exit(0);
						}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			System.out.println("Enter valid port");
			System.exit(0);
		}
		try {
			serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			System.out.println("Server is listening on port"+args[0]);
		} catch (IOException e) {
			System.err.println(e);
		}

		try {

			while (true) {

				clientSocket = serverSocket.accept();
				allClients.add(clientSocket.getInetAddress());
				clientSockets.add(clientSocket);
				String all = "";
				for (int i = 0; i < allClients.size(); i++) {

					all = all + i + "|" + allClients.get(i).toString() + "  " + clientSockets.get(i).getPort()
							+ "		";
				}
				System.out.println("\n Client"+clientSocket.getInetAddress().toString()+" connected to Server");
			
				EchoServer serverHandler = new EchoServer(clientSocket, allClients, all, clientSockets);
				allObjects.add(serverHandler);
				for (EchoServer e : allObjects) {
					e.setAllClients(allClients);
					e.setAll(all);
					e.setClientSockets(clientSockets);
				}
				serverHandler.start();
			}

		} catch (IOException e) {
			System.err.println(e);
		}
	}

}

// To Implement Threading
class EchoServer extends Thread {
	Socket clientSocket;
	String all = null;
	ArrayList<InetAddress> allClients = new ArrayList<InetAddress>();
	ArrayList<Socket> clientSockets = new ArrayList<Socket>();

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public String getAll() {
		return all;
	}

	public void setAll(String all) {
		this.all = all;
	}

	public ArrayList<InetAddress> getAllClients() {
		return allClients;
	}

	public void setAllClients(ArrayList<InetAddress> allClients) {
		this.allClients = allClients;
	}

	public ArrayList<Socket> getClientSockets() {
		return clientSockets;
	}

	public void setClientSockets(ArrayList<Socket> clientSockets) {
		this.clientSockets = clientSockets;
	}

	EchoServer(Socket client, ArrayList<InetAddress> allClients, String all, ArrayList<Socket> clientSockets) {
		this.clientSocket = client;
		this.allClients = allClients;
		this.all = all;
		this.clientSockets = clientSockets;
	}

	public void run() {
		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println(" type 'exit' to close connection");

			// enables server
			while (true) {
				String line = in.readLine();
				if(line.trim().equals("help")){
					out.println("HELP = myip :display IP address  connect: connect to server  send: send messages to peers exit: exit the program");
				}
				if (line.trim().contains("terminate")) {
					for (Socket peer : clientSockets) {
						if (clientSocket.getInetAddress().toString().contains(peer.getInetAddress().toString())) {
							clientSocket.shutdownInput();
							clientSocket.shutdownOutput();
							clientSocket.close();
							all = all.replace(clientSocket.getInetAddress().toString(), "");
							allClients.remove(peer);
							clientSockets.remove(peer);
							break;

						}

					}

				}
				if (line.trim().equals("exit")) {
					out.println("bye!");
					break;
				}
				if (line.trim().equals("list")) {
					out.println(all);

				}
				if (line.contains("send")) {
					
					for (Socket peer : clientSockets) {

						String[] s = line.split(" ");
						String temp = "";
						for (int i = 2; i < s.length; i++)
							temp = temp + s[i] + " ";
						System.out.println("sent from server>>" + "send*" + s[1] + "*" + temp);

						if (peer.getInetAddress().toString().contains(s[1])) {
							PrintWriter o = new PrintWriter(peer.getOutputStream(), true); 
							o.println("send*" + s[1] + "*"+clientSocket.getInetAddress()+"*"+clientSocket.getPort()   + "*" + temp);
						}
					}
				}
				if (line.contains("myip"))
					out.println(clientSocket.getInetAddress());
				if (line.contains("myport")) {
					out.println(clientSocket.getPort());
				}
			}
		} catch (Exception e) {
			System.err.println("Client Logoff");
		} finally {
			try {
				clientSocket.close();
			} catch (Exception e) {
				;
			}
		}
	}
}
