import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Server {
	private int port;
	private HashMap<String, Socket> connectedClients;
	private ServerSocket serverSocket;
	private Thread acceptThread;
	
	public Server(int port) throws IOException
	{
		this.port = port;
		connectedClients = new HashMap<>();
	}
	
	public void start() throws IOException
	{
		serverSocket = new ServerSocket(port);
		this.acceptThread = new Thread(new AcceptThread());
		this.acceptThread.start();
	}
	
	//inner Classes
	/**
	 * Dieser Thread wartet auf einen Client, der sich verbinden möchte
	 * Falls eine Anfrage kommt wird der Client akzeptiert
	 * und ein neuer CommunicationThread gestartet, der auf eingehende
	 * Nachrichten horcht.
	 * */
	private class AcceptThread implements Runnable
	{
		@Override
		public void run() {
			Socket socket = null;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					socket = serverSocket.accept();
					//start new communication Thread
					System.out.println("Client connected");
					//TODO: Add to HashMap
					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}		
			}
		}
	}
	
	/**
	 * Dieser Thread wartet auf Nachricht eines Clients
	 * */
	private class CommunicationThread implements Runnable
	{
		private BufferedReader input;
		
		public CommunicationThread(Socket clientSocket) throws IOException
		{
			InputStream is = clientSocket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			input = new BufferedReader(isr);
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted())
			{
				String msg = null;
				try {
					msg = input.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(msg);
			}
		}
	}//inner class ends

}
