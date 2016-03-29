package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.app.db.AccountRepo;
import name.valery1707.interview.websocket.app.db.TokenRepo;
import name.valery1707.interview.websocket.domain.Account;
import name.valery1707.interview.websocket.domain.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthorizationService {
	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationService.class);

	@Value("${token.livePeriod}")
	private String tokenLivePeriodRaw;
	private Duration tokenLivePeriod;

	@Inject
	private AccountRepo accountRepo;
	@Inject
	private TokenRepo tokenRepo;

	@PostConstruct
	public void init() {//todo Private
		tokenLivePeriod = Duration.parse(tokenLivePeriodRaw);
	}

	@Nonnull
	@Transactional
	public Token auth(@Nonnull Account src) {
		Account account = accountRepo.getByEmailAndPassword(src.getEmail(), src.getPassword());
		if (account != null) {
			return createNewToken(account);
		} else {
			throw new IllegalStateException("Unknown account");
		}
	}

	private Token createNewToken(Account account) {
		int revoked = tokenRepo.revokeActualTokens(account);
		LOG.info("Revoke {} actual tokens for account({}, {})", revoked, account.getId(), account.getEmail());
		Token token = new Token();
		token.setAccount(account);
		token.setToken(UUID.randomUUID());
		token.setExpiration(ZonedDateTime.now().plus(tokenLivePeriod));
		return tokenRepo.save(token);
	}

	public boolean isTokenValid(Map<String, String> data) {
		String tokenRaw = data.get("api_token");
		if (tokenRaw == null) {
			return false;
		}
		UUID token;
		try {
			token = UUID.fromString(tokenRaw);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return tokenRepo.countActualTokenByToken(token) > 0;
	}
}
