package tk.dwcdn.p.db;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AccountMgr {

	private File cfgFile;

	private Set<Account> accounts;

	/**
	 * 用指定的密码文件创建用户数据库
	 * 如果给定的文件不存在，将会创建该文件
	 *
	 * @param cfgFile 密码配置文件，不可以是 null
	 * @throws NullPointerException 如果 cfgFile = null
	 * @throws IOException          如果文件不存在，且无法创建
	 */
	public AccountMgr(File cfgFile) throws IOException {
		if (cfgFile == null)
			throw new NullPointerException("Config file is null");

		this.cfgFile = cfgFile;
		if (!this.cfgFile.exists()) {
			File parent = this.cfgFile.getParentFile();
			if (!parent.exists())
				parent.mkdirs();

			this.cfgFile.createNewFile();
		}

		this.accounts = new HashSet<>();
	}

	@Nullable
	private Account findByName(String name) {
		synchronized (this.accounts) {
			for (Account a : this.accounts) {
				if (a.name.equals(name))
					return a;
			}
		}
		return null;
	}

	private byte[] encrypt(String clearText) {
		MessageDigest dg = null;
		try {
			dg = MessageDigest.getInstance("SHA-256");
			dg.update(clearText.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return dg.digest();
	}

	public void addAccount(String name, String clearPassword) {
		byte[] passwd = encrypt(clearPassword);
		this.accounts.add(new Account(name, byte2str(passwd)));
	}

	public void resetPasswd(String name, String newPass) {
		Account a = findByName(name);
		if (a == null)
			return;
		a.resetPasswd(byte2str(encrypt(newPass)));
	}

	public void deleteAccount(String name) {
		synchronized (this.accounts) {
			Iterator<Account> it = this.accounts.iterator();
			while (it.hasNext()) {
				Account a = it.next();
				if (a.name.equals(name))
					it.remove();
			}
		}
	}

	public void writeToFile() {
		try {
			this.cfgFile.delete();
			this.cfgFile.createNewFile();
			PrintWriter oWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.cfgFile), Charset.forName("utf-8")));
			synchronized (this.accounts) {
				for (Account a : this.accounts) {
					oWriter.println(a.name + ':' + a.getPasswd());
				}
			}
			oWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readFromFile() {
		try {
			BufferedReader iReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.cfgFile), Charset.forName("utf-8")));
			synchronized (this.accounts) {
				while (true) {
					String arg = iReader.readLine();
					if (arg == null)
						break;
					String[] sargs = arg.split(":", 2);
					if (sargs.length < 2)
						continue;
					this.accounts.add(new Account(sargs[0], sargs[1]));
				}
			}
			iReader.close();
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public boolean checkUser(String name, String itsPasswd) throws PlayerNotRegisteredException {
		Account a = findByName(name);
		if (a == null)
			throw new PlayerNotRegisteredException();
		if (a.getPasswd().equals(byte2str(encrypt(itsPasswd)))) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkName(String name) {
		// length must greater than 3 and less than 32
		if (name.length() < 3 || name.length() > 32) {
			return false;
		}

		char[] buf = name.toCharArray();
		for (char c : buf) {
			if (!((c >= 0x41 && c <= 0x5a) ||
					(c >= 0x61 && c <= 0x7a) ||
					(c >= 0x30 && c <= 0x39) ||
					c == '_' ||
					c == '-' ||
					c == '#'
			)) {
				return false;
			}
		}
		return true;
	}

	public boolean isRegistered(String name) {
		if (findByName(name) == null)
			return false;
		// else
		return true;
	}

	private String byte2str(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int lo = 0; lo < b.length; lo++) {
			byte bt = b[lo];
			sb.append(Integer.toHexString((int) bt & 0xff));
		}
		return sb.toString();
	}

	public class PlayerNotRegisteredException extends Exception {

	}

	public List<String> matches(String prefix) {
		List<String> ls = new ArrayList<>();
		synchronized (this.accounts) {
			for (Account a : accounts) {
				if (a.name.startsWith(prefix))
					ls.add(a.name);
			}
		}
		return ls;
	}

	public List<String> getAccounts() {
		List<String> ls = new ArrayList<>();
		synchronized (this.accounts) {
			for (Account a : accounts) {
				ls.add(a.name);
			}
		}
		return ls;
	}

}
