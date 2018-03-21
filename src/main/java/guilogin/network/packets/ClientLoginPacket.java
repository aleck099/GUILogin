package guilogin.network.packets;

/**
 * 客户端发送给服务端的数据包，用于登录或注册
 */
public class ClientLoginPacket extends AbstractPacket {
	public final String password;

	public ClientLoginPacket(String password) {
		this.password = password;
	}
}
