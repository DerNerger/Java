import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	private Socket clientSocket;
	
	public Client(int port, String host) throws UnknownHostException, IOException
	{
		clientSocket = new Socket(host,port);
		System.out.println("Connected");
		//lol
	}

}
