package guilogin;

import java.io.*;
import java.nio.charset.Charset;

public class ModConfig {
	private boolean isEnabled = false;
	private int timeOut = 60; //单位是秒

	/**
	 * for client side
	 */
	public ModConfig() {
		this.isEnabled = true;
		this.timeOut = 60;
	}

	/**
	 * for server side
	 * @param cfgFile config file
	 * @throws IOException If any I/O error ???s
	 */
	public ModConfig(File cfgFile) throws IOException {
		if (!cfgFile.exists()) {
			cfgFile.getParentFile().mkdirs();
			createConfigFile(cfgFile);
			GUILogin.modLogger.info("Config file not found, creating one.");
		}

		BufferedReader iReader = null;
		try {
			iReader = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFile), Charset.forName("utf-8")));
		} catch (FileNotFoundException e) {
		}

		String buf;
		while ((buf = iReader.readLine()) != null) {
			if (buf.length() < 1 || buf.charAt(0) == '#')
				continue;
			String[] data = buf.split("=", 2);
			if (data.length < 2)
				continue;

			// 匹配
			if (data[0].equals("enabled")) {
				this.isEnabled = data[1].equals("true");
			} else if (data[0].equals("timeout")) {
				Integer timeout;
				try {
					timeout = Integer.valueOf(data[1]);
				} catch (NumberFormatException exc) {
					continue;
				}
				this.timeOut = timeout;
			}
		}

		GUILogin.modLogger.info("ModConfig: enabled = " + isEnabled);
		GUILogin.modLogger.info("ModConfig: timeout = " + timeOut);
	}

	private static void createConfigFile(File file) throws IOException {
		// In
		InputStream res = ModConfig.class.getResourceAsStream("/guilogin.cfg");
		if (res == null)
			throw new NullPointerException();

		int len = res.available();

		byte[] buf = new byte[len];
		res.read(buf);
		res.close();

		// Out
		FileOutputStream fOut = null;
		if (!file.exists())
			file.createNewFile();
		fOut = new FileOutputStream(file);
		fOut.write(buf);
		fOut.close();
	}

	public boolean isModEnabled() {
		return this.isEnabled;
	}

	public int getTimeOut() {
		return timeOut;
	}
}
