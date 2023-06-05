import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	//The server will have a password stored in afile in the system, the fille is called token.txt. After being authorized,
	// The client will just write down command which will be executed in the server's terminal and the answers will besend back to the client via
	//socket connection.
	// We will use TCP
	private static File token;
	//the password file
	//The process which will be executed
	private static ServerSocket server;
	//The TCP server
	private static Socket[] clients;
	//An Array representing the clients, max 4
	private static  int num_cli;
	//The number of concurrents connexions
	private static DataInputStream dis;
	private static DataOutputStream dos;
	//
	private static Thread t1;

	public Server() {
		token = new File("token.txt");
		clients = new Socket[4];
		num_cli = 0;
	}
	
	public static boolean Authenticate(String passwd) throws FileNotFoundException {
		Scanner sc = new Scanner(token);
		String truth = sc.nextLine();
		
		if(truth.equals(passwd)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server s = new Server();
		server = new ServerSocket(6000);
		while(num_cli < 4) {
			clients[num_cli] = server.accept();
			String passwd = "";
			dis = new DataInputStream(clients[num_cli].getInputStream());
			dos = new DataOutputStream(clients[num_cli].getOutputStream());
			try {
				passwd = dis.readUTF();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(passwd.trim());
			boolean access = Authenticate(passwd.trim());
			if(access) {
				dos.writeUTF("Ok");
				t1 = new Thread(new MyRunnable(clients[num_cli]));
				t1.start();
			}
			// if the password is incorrect, there are three attempts
			else{
				dos.writeUTF("Error");
				for(int i =0; i<3; i++) {
					if(Authenticate(passwd.trim())) {
						i=3;
						dos.writeUTF("Ok");
						t1 = new Thread(new MyRunnable(clients[num_cli]));
						t1.start();
					}
					else {
						dos.writeUTF("Error");
					}
				}
				
			}
			num_cli++;
			}
			
		}
}

class MyRunnable implements Runnable{
	Socket client;
	public MyRunnable(Socket client) {
		this.client = client;
	}
	@Override
	public void run() {
	// TODO Auto-generated method stub
		try {
			Terminal(this.client);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	//The method to access to the server's prompt
	public void Terminal(Socket client) throws IOException, InterruptedException {
		//"end" is used to end the connection
		Process run;
		System.out.println("entered");

		DataInputStream dis = new DataInputStream(client.getInputStream());
		DataOutputStream dos = new DataOutputStream(client.getOutputStream());
		while(true) {
			try {
				String rep = dis.readUTF().trim();
				while(!(rep.equals("end"))){
					run = Runtime.getRuntime().exec(rep);
					run.waitFor();
					System.out.println("outside");
					BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()));
					String answer = reader.readLine();
					dos.writeUTF(answer);
					rep = dis.readUTF().trim();
					}
			}catch(Exception E) {
			}
	}
}
}
