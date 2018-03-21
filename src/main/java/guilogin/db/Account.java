package guilogin.db;

public class Account {

	/**
	 * 用户名
	 */
	public final String name;

	/**
	 * 密码（加密）
	 */
	private String passwd;

	public String getPasswd() {
		return this.passwd;
	}

	public void resetPasswd(String passwd) {
		if (passwd == null)
			throw new NullPointerException();
		this.passwd = passwd;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Account) && ((Account) obj).name.equals(this.name);
	}

	public Account(String name, String passwd) {
		this.passwd = passwd;
		this.name = name;
	}

}
