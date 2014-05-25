

public class ChatProtocol implements IProtocol{

	@Override
	public Packet parsePacket(String msg, String sessionId) {
		String [] content = msg.split(",");
		String receiver = content[1].split("=")[1];
		String message = content[2].split("=")[1];
		return new ChatMessagePacket(sessionId, receiver, message);
	}

	@Override
	public Packet getClientConnectedPacket(String sessionId) {
		String msg = "Client connected";
		String receiver = "server";
		return new ChatMessagePacket(sessionId, receiver, msg);
	}

	@Override
	public Packet getClientLeftPacket(String sessionId) {
		String msg = "Client left";
		String receiver = "server";
		return new ChatMessagePacket(sessionId, receiver, msg);
	}

	@Override
	public String printPacket(Packet p) {
		if(p.getType()!=PacketType.ChatMessagePacket)
			throw new RuntimeException("Packet ist vom fehlerhaften Typ "+p.getType());
		
		ChatMessagePacket cmp = (ChatMessagePacket) p;
		String sender = cmp.getSenderSessionId();
		String receiver = cmp.getReceiverSessionId();
		String msg = cmp.getMsg();
		return "sender="+sender+",receiver="+receiver+",msg="+msg;
	}

}