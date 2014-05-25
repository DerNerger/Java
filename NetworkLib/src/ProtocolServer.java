import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ProtocolServer extends Server{
	
	private IProtocol protocol;
	private Thread packetSenderThread;
	
	private Queue<Packet> packetsToSend;
	private Lock packetsToSendLock;
	
	private Queue<Packet> receivedPackets;
	private Lock receivedPacketsLock;
	

	public ProtocolServer(int port, IProtocol protocol) throws IOException {
		super(port);
		this.protocol = protocol;
		
		packetsToSend = new LinkedList<Packet>();
		receivedPackets = new LinkedList<>();
		packetsToSendLock = new ReentrantLock();
		receivedPacketsLock = new ReentrantLock();
		
		packetSenderThread = new Thread(new PacketSender());
		packetSenderThread.start();
	}
	
	//fuegt das Packet p in die queue ein, sodass es gesendet werden kann
	public void sendPacket(Packet p)
	{
		packetsToSendLock.lock();
		packetsToSend.offer(p);
		packetsToSendLock.unlock();
	}

	//gibt das Packet zurueck, was am laengsten in der queue wartet
	public Packet getNextPacket()
	{
		receivedPacketsLock.lock();
		Packet p = receivedPackets.poll();
		receivedPacketsLock.unlock();
		return p;
	}
	
	@Override
	public void handleClientMessage(String msg, ConnectedClient connectedClient)
	{
		Packet p = protocol.parsePacket(msg, connectedClient.getSessionId());
		receivedPacketsLock.lock();
		receivedPackets.offer(p);
		receivedPacketsLock.unlock();
	}
	
	@Override
	public void handleClientConnected(ConnectedClient connectedClient) throws IOException
	{
		Packet p = protocol.getClientConnectedPacket(connectedClient.getSessionId());
		receivedPacketsLock.lock();
		receivedPackets.offer(p);
		receivedPacketsLock.unlock();
	}
	
	@Override
	public void handleClientLeft(ConnectedClient connectedClient)
	{
		Packet p = protocol.getClientLeftPacket(connectedClient.getSessionId());
		receivedPacketsLock.lock();
		receivedPackets.offer(p);
		receivedPacketsLock.unlock();
	}
	
	public boolean hasPacketsToProcess()
	{
		receivedPacketsLock.lock();
		boolean hptp = receivedPackets.size()>0;
		receivedPacketsLock.unlock();
		return hptp;
	}
	
	private void send(Packet p)
	{
		String msgToSend = protocol.printPacket(p);
		super.writeToClient(msgToSend, p.getReceiverSessionId());
	}
	
	//inner classes
	private class PacketSender implements Runnable
	{
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted())
			{
				packetsToSendLock.lock();
				Packet packetToSend = packetsToSend.poll();
				send(packetToSend);
				packetsToSendLock.unlock();
			}
		}
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
			s.writeToAll(msg);
		}
	}
}
