package guilogin.network;

import guilogin.GUILogin;
import guilogin.network.packets.AbstractPacket;
import guilogin.network.packets.ClientLoginPacket;
import guilogin.network.packets.ServerRequestLoginPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerMessageHandler implements IMessageHandler<LoginMessage, IMessage> {
	@Override
	public IMessage onMessage(LoginMessage message, MessageContext ctx) {
		AbstractPacket packet = message.getPacket();
		if (packet instanceof ClientLoginPacket) {
			String passwd = ((ClientLoginPacket) packet).password;
			EntityPlayerMP player = ctx.getServerHandler().player;

			GUILogin.modLogger.info("Player " + player.getName() + " sent a ClientLoginPacket to server");

			if (GUILogin.instance.accountMgr.isRegistered(player.getName())) {
				/*对于已经注册过了的玩家*/
				if (GUILogin.instance.accountMgr.checkUser(player.getName(), passwd)) {
					/*密码正确*/
					/*恢复GameType*/
					player.setGameType(GUILogin.instance.notLoggedins.get(player.getName()).gameType);
					GUILogin.instance.notLoggedins.remove(player.getName());
					player.sendMessage(new TextComponentTranslation("gl.login.success"));
					GUILogin.modLogger.info("Player " + player.getName() + " entered §bcorrect§r password");
				} else {
					/*密码错误，重新登陆去*/
					GUILogin.modLogger.info("Player " + player.getName() + " entered §cwrong§r password");
					return new LoginMessage(new ServerRequestLoginPacket("gl.login.request.wrong"));
				}
			} else {
				/*对于还没有注册过的玩家*/
				GUILogin.instance.accountMgr.addAccount(player.getName(), passwd);
				player.setGameType(GUILogin.instance.notLoggedins.get(player.getName()).gameType);
				GUILogin.instance.notLoggedins.remove(player.getName());
				player.sendMessage(new TextComponentTranslation("gl.reg.success"));
				GUILogin.modLogger.info("Player " + player.getName() + " registered");
			}
		}

		return null;
	}
}
