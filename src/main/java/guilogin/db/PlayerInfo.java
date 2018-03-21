package guilogin.db;

import guilogin.GUILogin;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PlayerInfo {
	public final double X, Y, Z;

	/**
	 * 玩家登录的时间
	 */
	public final int tick;
	public final GameType gameType;

	public PlayerInfo(double x, double y, double z, int loginTick, GameType gameType) {
		X = x;
		Y = y;
		Z = z;
		this.tick = loginTick;
		this.gameType = gameType;
	}

	/**
	 * 玩家超时登录检查
	 */
	public static void check(int currentTick, MinecraftServer sv) {
		Map<String, PlayerInfo> nologins = GUILogin.instance.notLoggedins;
		synchronized (nologins) {
			Set<Map.Entry<String, PlayerInfo>> entries = nologins.entrySet();
			Iterator<Map.Entry<String, PlayerInfo>> it = entries.iterator();
			while (it.hasNext()) {
				Map.Entry<String, PlayerInfo> one = it.next();
				/*
				超时检测
				 */
				if (one.getValue().tick + (GUILogin.instance.config.getTimeOut() * 20) < currentTick) {
					EntityPlayerMP player = sv.getPlayerList().getPlayerByUsername(one.getKey());
					if (player != null)
						player.connection.disconnect(new TextComponentTranslation("gl.login.timeout", GUILogin.instance.config.getTimeOut()));
					it.remove();
				}
			}
		}
	}
}
