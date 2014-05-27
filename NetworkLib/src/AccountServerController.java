import java.io.IOException;
import java.util.Scanner;

public class AccountServerController {
	private AccountServerModel model;
	private Thread answerThread;
	private ProtocolServer protocolServer;
	
	public AccountServerController(int port, AccountServerModel model) throws IOException
	{
		this.model = model;
		IProtocol protocol= new ChatProtocol();
		protocolServer = new ProtocolServer(port, protocol);
		protocolServer.start();
		answerThread = new Thread(new AnswerThread());
		answerThread.start();
	}
	
	//hier werden eingehende Packete verarbeitet
	private void processPacket(Packet p) {
		switch (p.getType()) {
		case ChatMessagePacket:
			ChatMessagePacket cmp = (ChatMessagePacket)p;
			System.out.println(cmp.getMsg());
			break;

		default:
			throw new RuntimeException("PacketType unbekannt");
		}
	}
	
	//inner classes
	//dieser Thread verarbeitet Packete der CLients
	private class AnswerThread implements Runnable
	{
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted())
			{
				if(protocolServer.hasPackets())
				{
					Packet p = protocolServer.getNextPacket();
					processPacket(p);
				}
			}
		}
	}
	
	public static void main(String args[]) throws IOException
	{
		AccountServerController server = new AccountServerController(5555, null);
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			String msg = sc.nextLine();
			Packet p = new ChatMessagePacket("server", "all", msg);
			server.protocolServer.sendPacket(p);
		}
	}
}
