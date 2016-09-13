

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {

		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		System.out.println("------------MultiClient Chat Application-----------------\n");
		System.out.println("---  MENU  ---");
		System.out.println("help");
		System.out.println("myip");
		System.out.println("myport");
		System.out.println("connect Example: (connect <destination> <port no>))");
		System.out.println("list");
		System.out.println("terminate");
		System.out.println("send");
		System.out.println("exit\n\n");

		try {
			Scanner s = new Scanner(System.in);
			System.out.println("Enter server's IP address and PORT to connect :");
			String ip = s.nextLine();
			String[] connect = ip.split(" ");
			socket = new Socket(connect[1], Integer.parseInt(connect[2]));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host.");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection.");
		}

		try {

			BufferedReader con = new BufferedReader(new InputStreamReader(System.in));
			con.mark(0);
			String line;
			do {
				line = in.readLine();
				if (line != null) {
					if (line.contains("send") && !line.contains("HELP")) {
						String[] s = line.split("\\*");
						System.out.println("SENT FROM :" +s[2]);
						System.out.println("Port"+s[3]);
						System.out.println("Message :" + s[4]);

					} else
						System.out.print(" Server :" + line);
				} else {
					System.out.println("Client terminated");
				}
				line = con.readLine();
				out.println(line);

			} while (!line.trim().equals("exit"));

			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
