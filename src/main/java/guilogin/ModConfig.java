package guilogin;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

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
	 *
	 * @param cfgFile config file
	 * @throws IOException If any I/O error ???s
	 */
	public ModConfig(Path cfgFile) throws IOException {
		if (!Files.exists(cfgFile)) {
			createConfigFile(cfgFile);
			GUILogin.modLogger.info("Config file not found, creating one.");
		}

		Files
				.lines(cfgFile, Charset.forName("utf-8"))
				.filter(p -> !p.startsWith("#"))
				.forEach(s -> {
			String[] data = s.split("=", 2);
			if (data.length != 2)
				return;

			match(data[0], data[1]);
		});

		GUILogin.modLogger.info("ModConfig: enabled = " + isEnabled);
		GUILogin.modLogger.info("ModConfig: timeout = " + timeOut);
	}

	private void match(String key, String value) {
		if ("enabled".equals(key)) {
			this.isEnabled = "true".equals(value);
		} else if ("timeout".equals(key)) {
			Integer tmout;
			try {
				tmout = Integer.valueOf(value);
			} catch (NumberFormatException e) {
				return;
			}

			this.timeOut = tmout;
		}
	}

	private static void createConfigFile(Path file) throws IOException {
		// In
		byte[] buf;
		try (
				InputStream res = ModConfig.class.getResourceAsStream("/guilogin.cfg")
		) {
			if (res == null)
				throw new FileNotFoundException("guilogin.cfg");
			int len = res.available();
			buf = new byte[len];
			res.read(buf);
		}

		// Out
		FileOutputStream fOut = null;
		if (!Files.exists(file))
			Files.createFile(file);

		Files.write(file, buf);
	}

	public boolean isModEnabled() {
		return this.isEnabled;
	}

	public int getTimeOut() {
		return timeOut;
	}
}
