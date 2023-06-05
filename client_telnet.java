import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client_telnet {
	
	private static String passwd;
	private static String server_ip;
	private static Socket client;
	private static int default_port;
	private static int attempts; //max number of trials for a wrong password
	static DataInputStream dis;
	static DataOutputStream dos;
	//
	public client_telnet() {
		
		passwd = "";
		System.out.println("Enter the server's ip @ please...");
		Scanner sc = new Scanner(System.in);
		server_ip = sc.nextLine();
		attempts = 0;
		default_port = 6000;
	}
	//returns true if the connection has been established, false if not
	public static boolean connection() throws UnknownHostException, IOException {
		
		client = new Socket(server_ip,default_port);
		//Get the password
		System.out.println("Enter the session password pls...");
		Scanner sc = new Scanner(System.in);
		passwd = sc.nextLine();
		dos = new DataOutputStream(client.getOutputStream());
		dos.writeUTF(passwd);
		//Handling errors or going through
		dis = new DataInputStream(client.getInputStream());
		while(!(dis.readUTF().equals("Ok")) && attempts <3) {
			System.out.println("Wrong password, try again please...");
			sc = new Scanner(System.in);
			passwd = sc.nextLine();
			dos.writeUTF(passwd);
			attempts++;
		}
		if(attempts == 3) {
			System.out.println("You've used all of your trials, please try again later");
			return false;
		}
		else {
			return true;
		}
		
	}
	
	//Send commands to the server
	public static void tele_shell(String password) throws IOException {
		System.out.println("Type an instruction for the server's shell, TYPE 0000 if you want to stop");
		Scanner sc = new Scanner(System.in);
		String cmd = sc.nextLine();
		dos.writeUTF(cmd);
		System.out.println(dis.readUTF());
	}
	
	//main function
	public static void main(String[] args) throws UnknownHostException, IOException {
		client_telnet cli = new client_telnet();
		boolean access = connection();
		if(access) {
			tele_shell(passwd);
		}
		else {
			System.exit(1);
		}
	}
}
