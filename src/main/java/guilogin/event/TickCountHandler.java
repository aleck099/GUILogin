package guilogin.event;

import guilogin.GUILogin;
import guilogin.db.PlayerInfo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickCountHandler {

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		PlayerInfo.check(GUILogin.server.getTickCounter(), GUILogin.server);
	}
}
