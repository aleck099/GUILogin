package guilogin.db;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountMgr {

	private Path cfgFile;

	private Set<Account> accounts;

	/**
	 * 用指定的密码文件创建用户数据库
	 * 如果给定的文件不存在，将会创建该文件
	 *
	 * @param cfgFile 密码配置文件，不可以是 null
	 * @throws NullPointerException 如果 cfgFile = null
	 * @throws IOException          如果文件不存在，且无法创建
	 */
	public AccountMgr(Path cfgFile) throws IOException {
		if (cfgFile == null)
			throw new NullPointerException("Config file is null");

		this.cfgFile = cfgFile;
		if (!Files.exists(cfgFile)) {
			Path parent = cfgFile.getParent();
			if (!Files.exists(parent))
				Files.createDirectories(parent);

			Files.createFile(cfgFile);
		}

		this.accounts = new HashSet<>();
	}

	@Nullable
	private Account findByName(String name) {
		for (Account a : accounts) {
			if (a.name.equals(name))
				return a;
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
	 * @return 如果成功，返回true
	 */
	public boolean resetPasswd(String name, String newPass) {
		Account a = findByName(name);
		if (a == null)
			return false;
		a.resetPasswd(byte2str(encrypt(newPass, name)));
		return true;
	}

	/**
	 *
	 * @param name 用户名
	 * @return 如果成功删除，返回true
	 */
	public boolean deleteAccount(String name) {
		return this.accounts.removeIf(a -> a.name.equals(name));
	}

	/**
	 * 把数据库写入文件
	 */
	public void writeToFile() {
		try (
				BufferedWriter oWriter = Files.newBufferedWriter(cfgFile, Charset.forName("utf-8"));
		) {
			for (Account a : this.accounts) {
				oWriter.write(a.name);
				oWriter.write(':');
				oWriter.write(a.getPasswd());
				oWriter.write('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从文件中读取数据
	 */
	public void readFromFile() {
		accounts.clear();
		try {
			Files
					.lines(cfgFile, Charset.forName("utf-8"))
					.forEach(s -> {
						String[] data = s.split(":", 2);
						if (data.length != 2)
							return;

						accounts.add(new Account(data[0], data[1]));
					});
		} catch (IOException e) {
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
		return findByName(name) != null;
	}

	private String byte2str(byte[] b) {
		if (b == null)
			return "";
		StringBuilder sb = new StringBuilder();
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
	public List<String> getNamesMatches(String prefix) {
		List<String> ls = new ArrayList<>();
		accounts.stream().filter(a -> a.name.startsWith(prefix)).forEach(a -> ls.add(a.name));
		return ls;
	}

	/**
	 * 获取所有用户
	 *
	 * @return 所有用户
	 */
	public List<String> getAccounts() {
		List<String> ls = new ArrayList<>();
		accounts.forEach(a -> ls.add(a.name));
		return ls;
	}

}
