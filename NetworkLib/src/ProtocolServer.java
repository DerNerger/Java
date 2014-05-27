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
	private int receivedPacketsCount;
	private int packetsToSendCont;
	

	public ProtocolServer(int port, IProtocol protocol) throws IOException {
		super(port);
		this.protocol = protocol;
		
		packetsToSend = new LinkedList<Packet>();
		receivedPackets = new LinkedList<>();
		packetsToSendLock = new ReentrantLock();
		receivedPacketsLock = new ReentrantLock();
		receivedPacketsCount = 0;
		
		packetSenderThread = new Thread(new PacketSender());
		packetSenderThread.start();
	}
	
	//fuegt das Packet p in die queue ein, sodass es gesendet werden kann
	public void sendPacket(Packet p)
	{
		packetsToSendLock.lock();
		packetsToSend.offer(p);
		packetsToSendLock.unlock();
		packetsToSendCont ++;
	}

	//gibt das Packet zurueck, was am laengsten in der queue wartet
	public Packet getNextPacket()
	{
		if(receivedPacketsCount==0)
			throw new RuntimeException("No Packets to recieve");
		receivedPacketsLock.lock();
		Packet p = receivedPackets.poll();
		receivedPacketsLock.unlock();
		receivedPacketsCount --;
		return p;
	}
	
	@Override
	public void handleClientMessage(String msg, ConnectedClient connectedClient)
	{
		Packet p = protocol.parsePacket(msg, connectedClient.getSessionId());
		receivedPacketsLock.lock();
		receivedPackets.offer(p);
		receivedPacketsLock.unlock();
		receivedPacketsCount ++;
	}
	
	@Override
	public void handleClientConnected(ConnectedClient connectedClient) throws IOException
	{
		Packet p = protocol.getClientConnectedPacket(connectedClient.getSessionId());
		receivedPacketsLock.lock();
		receivedPackets.offer(p);
		receivedPacketsLock.unlock();
		receivedPacketsCount ++;
	}
	
	@Override
	public void handleClientLeft(ConnectedClient connectedClient)
	{
		Packet p = protocol.getClientLeftPacket(connectedClient.getSessionId());
		receivedPacketsLock.lock();
		receivedPackets.offer(p);
		receivedPacketsLock.unlock();
		receivedPacketsCount ++;
	}
	
	public boolean hasPacketsToProcess()
	{
		receivedPacketsLock.lock();
		boolean hptp = receivedPackets.size()>0;
		receivedPacketsLock.unlock();
		return hptp;
	}
	
	public int getReceivedPacketsCount()
	{
		return receivedPacketsCount;
	}
	
	public int getPacketsToSendCount()
	{
		return packetsToSendCont;
	}
	
	private void send(Packet p)
	{
		String msgToSend = protocol.printPacket(p);
		if(p.getReceiverSessionId().contains("all"))
			super.writeToAll(msgToSend);
		else
			super.writeToClient(msgToSend, p.getReceiverSessionId());
	}
	
	//inner classes
	private class PacketSender implements Runnable
	{
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted())
			{
				if(packetsToSendCont>0)
				{
					packetsToSendLock.lock();
					Packet packetToSend = packetsToSend.poll();
					send(packetToSend);
					packetsToSendLock.unlock();
					packetsToSendCont --;
				}
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
			ChatMessagePacket cmp = new ChatMessagePacket("server", "all", msg);
			s.sendPacket(cmp);
		}
	}
}
