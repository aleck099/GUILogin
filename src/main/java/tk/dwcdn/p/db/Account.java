package tk.dwcdn.p.db;

public class Account {

	public final String name;
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
