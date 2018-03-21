package guilogin.db;

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

	/**
	 * 把明文加密
	 *
	 * @param clearText 明文密码
	 * @param salt      不用说了吧
	 * @return 加密后的字节，如果加密失败将返回 <code>null</code>
	 */
	private byte[] encrypt(String clearText, String salt) {
		MessageDigest dg = null;
		try {
			dg = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		StringBuilder bu = new StringBuilder();
		bu.append(clearText);
		/*给密码加盐*/
		bu.append(salt);
		dg.update(bu.toString().getBytes(Charset.forName("utf-8")));
		return dg.digest();
	}

	/**
	 * 添加账户，对于新注册的用户
	 *
	 * @param name          用户名
	 * @param clearPassword 明文密码
	 */
	public void addAccount(String name, String clearPassword) {
		byte[] passwd = encrypt(clearPassword, name);
		this.accounts.add(new Account(name, byte2str(passwd)));
	}

	/**
	 * 重设用户的密码
	 *
	 * @param name    用户名
	 * @param newPass 新密码
	 */
	public void resetPasswd(String name, String newPass) {
		Account a = findByName(name);
		if (a == null)
			return;
		a.resetPasswd(byte2str(encrypt(newPass, name)));
	}

	/**
	 * 删除帐号
	 *
	 * @param name 要删除的用户名
	 */
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

	/**
	 * 把数据库写入文件
	 */
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

	/**
	 * 从文件中读取数据
	 */
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

	/**
	 * 用户密码验证
	 *
	 * @param name      用户名
	 * @param itsPasswd 明文密码
	 * @return true 代表密码正确， false 代表密码错误
	 */
	public boolean checkUser(String name, String itsPasswd) {
		Account a = findByName(name);
		if (a == null)
			return false;
		byte[] encryptedPassword = encrypt(itsPasswd, name);
		return a.getPasswd().equals(byte2str(encryptedPassword));
	}

	/**
	 * 检查用户名合法性，含有火星文的一律T掉
	 *
	 * @param name 用户名
	 * @return 用户名是否合法
	 */
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

	/**
	 * 查询数据库，判断用户是否已经注册
	 *
	 * @param name 用户名
	 * @return true 代表用户已经注册过了， false 代表用户是新来的
	 */
	public boolean isRegistered(String name) {
		if (findByName(name) == null)
			return false;
		// else
		return true;
	}

	private String byte2str(byte[] b) {
		if (b == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int lo = 0; lo < b.length; lo++) {
			byte bt = b[lo];
			sb.append(Integer.toHexString((int) bt & 0xff));
		}
		return sb.toString();
	}

	/**
	 * 找出符合 prefix 前缀的用户
	 *
	 * @param prefix 前缀
	 * @return 匹配的用户
	 */
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

	/**
	 * 获取所有用户
	 *
	 * @return 所有用户
	 */
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
