package name.valery1707.interview.websocket.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
public class Token extends ABaseEntity {
	@NotNull
	@ManyToOne
	private Account account;
	@NotNull
	private UUID token;
	@NotNull
	private ZonedDateTime expiration;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public UUID getToken() {
		return token;
	}

	public void setToken(UUID token) {
		this.token = token;
	}

	public ZonedDateTime getExpiration() {
		return expiration;
	}

	public void setExpiration(ZonedDateTime expiration) {
		this.expiration = expiration;
	}
}
