package name.valery1707.interview.websocket.domain;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Map;

@SuppressWarnings("unused")
@Entity
public class Account extends ABaseEntity {
	@NotNull
	private String email;
	@NotNull
	private String password;

	public Account() {
	}

	public Account(Map<String, String> data) {
		this(data.getOrDefault("email", ""), data.getOrDefault("password", ""));
	}

	public Account(String email, String password) {
		this();
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
