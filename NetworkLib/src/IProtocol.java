public interface IProtocol {
	
	Packet parsePacket(String msg, String sessionId);
	Packet getClientConnectedPacket(String sessionId);
	Packet getClientLeftPacket(String sessionId);
	String printPacket(Packet p);
}
