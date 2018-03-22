package guilogin.event;

import guilogin.GUILogin;
import guilogin.db.AccountMgr;
import guilogin.db.PlayerInfo;
import guilogin.network.LoginMessage;
import guilogin.network.packets.ServerRequestLoginPacket;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerSessionHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayerMP player;
		if (event.player instanceof EntityPlayerMP && !(event.player instanceof FakePlayer)) {
			player = (EntityPlayerMP) event.player;
		} else {
			// System.out.println("DBG::player is not EntityPlayerMP");
			return;
		}
		String name = player.getName();
		player.mcServer.logInfo("§bPlayer " + name + " tried to log in");
		if (!AccountMgr.checkName(name)) {
			/*非法名*/
			GUILogin.modLogger.warn("Player " + name + " tried to log in but failed because of his illegal name.");
			GUILogin.server.addScheduledTask(() -> player.connection.disconnect(new TextComponentTranslation("gl.login.illegalname")));
			return;
		}

		GUILogin.instance.notLoggedins.put(name, new PlayerInfo(player.posX, player.posY, player.posZ, player.mcServer.getTickCounter(), player.interactionManager.getGameType()));
		player.setGameType(GameType.SPECTATOR);

		/*要求登录*/
		GUILogin.netWrapper.sendTo(new LoginMessage(new ServerRequestLoginPacket(GUILogin.instance.accountMgr.isRegistered(player.getName()) ? "gl.login.request.login" : "gl.login.request.register")), player);
		GUILogin.modLogger.info("LoginMessage has been sent to the target player.");
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		if (!(event.player instanceof FakePlayer) && GUILogin.instance.notLoggedins.containsKey(event.player.getName())) {
			GameType type = GUILogin.instance.notLoggedins.get(event.player.getName()).gameType;
			event.player.setGameType(type);
		}
	}
}
