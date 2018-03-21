package guilogin;

import guilogin.db.AccountMgr;
import guilogin.db.PlayerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@Mod(modid = GUILogin.MODID, version = GUILogin.VERSION, name = GUILogin.NAME)
public class GUILogin {

	public static final String
			MODID = "guilogin",
			VERSION = "1.0",
			NAME = "GUILogin";

	public static Logger modLogger;
	@Mod.Instance
	public static GUILogin instance;

	/**
	 * mod的配置文件
	 */
	public ModConfig config;

	/*
	for server side
	 */
	public AccountMgr accountMgr;
	public LinkedHashMap<String, PlayerInfo> notLoggedins;

	/**
	 * 服务器的一个实例
	 */
	public static MinecraftServer server;

	/**
	 * 网络数据包发送机
	 */
	public static SimpleNetworkWrapper netWrapper;

	/**
	 * 代理器
	 */
	@SidedProxy(clientSide = "guilogin.CommonProxy$ClientProxy", serverSide = "guilogin.CommonProxy", modId = MODID)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		/*读取配置文件*/
		try {
			GUILogin.instance.config = new ModConfig(new File(event.getModConfigurationDirectory(), NAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*只有在配置文件中启用mod，才会生效*/
		if (config.isModEnabled())
			proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (config.isModEnabled())
			proxy.init(event);
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		server = event.getServer();
	}

}
