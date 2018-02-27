package tk.dwcdn.p;

import java.io.*;
import java.nio.charset.Charset;

public class ModConfig {
	private boolean isEnabled;
	private int timeOut; //单位是秒

	public ModConfig(File cfgFile) throws IOException {
		if (!cfgFile.exists()) {
			cfgFile.getParentFile().mkdirs();
			createConfigFile(cfgFile);
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
				if (data[1].equals("true"))
					this.isEnabled = true;
				else
					this.isEnabled = false;
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

	public boolean getIsEnabled() {
		return this.isEnabled;
	}

	public int getTimeOut() {
		return timeOut;
	}
}
