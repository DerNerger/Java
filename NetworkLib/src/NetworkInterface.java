
public interface NetworkInterface {
	void writeToAll(String msgToSend);
	void writeToClient(String msgToSend, String sessionID);
}
