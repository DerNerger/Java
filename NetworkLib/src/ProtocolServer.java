import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ProtocolServer extends Server implements NetworkInterface{
	
	private NetworkController networkController;
	
	//Konstruktor
	public ProtocolServer(int port, IProtocol protocol) throws IOException {
		super(port);
		networkController = new NetworkController(protocol, this);
	}
	
	
	//Methoden
	@Override
	protected void handleClientMessage(String msg, ConnectedClient connectedClient)
	{
		String clientSessionId = connectedClient.getSessionId();
		networkController.addMessage(msg, clientSessionId);
	}
	
	@Override
	protected void handleClientConnected(ConnectedClient connectedClient) throws IOException
	{
		String clientSessionId = connectedClient.getSessionId();
		networkController.addEvent(NetworkEvent.ClientConnected, clientSessionId);
	}
	
	@Override
	protected void handleClientLeft(ConnectedClient connectedClient)
	{
		String clientSessionId = connectedClient.getSessionId();
		networkController.addEvent(NetworkEvent.ClientLeft, clientSessionId);
	}
	
	@Override
	public void writeMessage(String msgToSend, String target)
	{
		if(target.contains("all"))
			writeToAll(msgToSend);
		else
			writeToClient(msgToSend, target);
	}
	
	public void sendPacket(Packet p) {
		networkController.sendPacket(p);
	}
	
	public boolean hasPackets()
	{
		return networkController.hasPacketsToProcess();
	}
	
	public Packet getNextPacket()
	{
		if(!hasPackets())
			throw new RuntimeException("No Packets to Recieve");
		return networkController.getNextPacket();
	}

	
	public static void main(String args[]) throws IOException
	{
		IProtocol protocol = new ChatProtocol();
		ProtocolServer s = new ProtocolServer(5555,protocol);
		s.start();
		
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			String msg = sc.nextLine();
			ChatMessagePacket cmp = new ChatMessagePacket("server", "all", msg);
			s.sendPacket(cmp);
		}
	}
	
}
