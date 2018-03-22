package guilogin;

import guilogin.db.AccountMgr;
import guilogin.event.IgnoreHandler;
import guilogin.event.PlayerSessionHandler;
import guilogin.event.TickCountHandler;
import guilogin.network.ClientMessageHandler;
import guilogin.network.LoginMessage;
import guilogin.network.ServerMessageHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class CommonProxy {

	public void configInit(File cfgDir) {
		try {
			GUILogin.instance.config = new ModConfig(new File(cfgDir, GUILogin.NAME + ".cfg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void preInit(FMLPreInitializationEvent event) {

		try {
			GUILogin.instance.accountMgr = new AccountMgr(new File(event.getModConfigurationDirectory(), "passwd.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*从文件中加载用户信息*/
		GUILogin.instance.accountMgr.readFromFile();

		GUILogin.instance.notLoggedins = new LinkedHashMap<>();

		GUILogin.netWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(GUILogin.MODID);
	}

	public void init(FMLInitializationEvent event) {
		/*注册服务端消息处理器*/
		GUILogin.netWrapper.registerMessage(new ServerMessageHandler(), LoginMessage.class, 233, Side.SERVER);
		/*注册事件监听器*/
		EventBus bus = MinecraftForge.EVENT_BUS;
		bus.register(new IgnoreHandler());
		bus.register(new PlayerSessionHandler());
		bus.register(new TickCountHandler());
	}

	public static class ClientProxy extends CommonProxy {

		@Override
		public void configInit(File cfgDir) {
			GUILogin.instance.config = new ModConfig();
		}

		@Override
		public void preInit(FMLPreInitializationEvent event) {
			GUILogin.netWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(GUILogin.MODID);
		}

		@Override
		public void init(FMLInitializationEvent event) {
			/*注册客户端消息处理器*/
			GUILogin.netWrapper.registerMessage(new ClientMessageHandler(), LoginMessage.class, 233, Side.CLIENT);
		}
	}
}
