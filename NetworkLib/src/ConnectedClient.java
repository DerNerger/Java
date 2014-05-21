import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;


public class ConnectedClient {
	private String sessionId;
	private Socket clientSocket;
	private PrintWriter output;
	
	private static SecureRandom randomNameGenerator = new SecureRandom();
	
	public ConnectedClient(String sessionId, Socket clientSocket) throws IOException
	{
		this.sessionId = sessionId;
		this.clientSocket = clientSocket;
		OutputStream os = clientSocket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		this.output = new PrintWriter(osw, true);
	}
	
	public void writeMessage(String msg)
	{
		output.println(msg);
		output.flush();
	}

	public String getName() {
		return sessionId;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}
	
	public static String generateSessionId()
	{
		return new BigInteger(130,randomNameGenerator).toString(32);
	}
}
