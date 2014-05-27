public interface IProtocol {
	
	Packet parsePacket(String msg, String sender);
	Packet getClientConnectedPacket(String sessionId);
	Packet getClientLeftPacket(String sessionId);
	String printPacket(Packet p);
}
