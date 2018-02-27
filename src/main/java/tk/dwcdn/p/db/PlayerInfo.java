package tk.dwcdn.p.db;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import tk.dwcdn.p.GUILogin;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PlayerInfo {
	public final double X, Y, Z;
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
				if (one.getValue().tick + (60 * 20) < currentTick) {
					sv.getPlayerList().getPlayerByUsername(one.getKey()).connection.disconnect(new TextComponentString("你没有在60秒内完成登录，系统把你踢出"));
					it.remove();
				}
			}
		}
	}
}
