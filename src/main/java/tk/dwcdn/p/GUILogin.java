package tk.dwcdn.p;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import tk.dwcdn.p.db.AccountMgr;
import tk.dwcdn.p.db.PlayerInfo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@Mod(modid = GUILogin.MODID, version = GUILogin.VERSION)
public class GUILogin {

	public static final String
			MODID = "guilogin",
			VERSION = "1.0";

	public Logger modLogger;
	@Mod.Instance
	public static GUILogin instance;
	public ModConfig config;
	public AccountMgr accountMgr;
	public LinkedHashMap<String, PlayerInfo> notLoggedins;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		modLogger = event.getModLog();

		try {
			config = new ModConfig(event.getSuggestedConfigurationFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			accountMgr = new AccountMgr(new File(event.getModConfigurationDirectory(), "passwd.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		notLoggedins = new LinkedHashMap<>();
	}

	public void init(FMLInitializationEvent event) {

	}

	public void serverStarting(FMLServerStartingEvent event) {

	}

}
