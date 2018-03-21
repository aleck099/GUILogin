package guilogin.network.packets;

/**
 * 由服务端发出，客户端收到此包后将会显示一个登录窗口
 */
public class ServerRequestLoginPacket extends AbstractPacket {
	public final String extraInfo;

	public ServerRequestLoginPacket(String extraInfo) {
		this.extraInfo = extraInfo;
	}
}
