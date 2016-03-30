package name.valery1707.interview.websocket.crypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

@Component
@Singleton
public class PasswordService {
	private static final Logger LOG = LoggerFactory.getLogger(PasswordService.class);
	public static final String TYPE_CLEAR = "clear:";

	@Value("${password.salt.size}")
	private int saltSize;
	@Value("${password.hash.size}")
	private int hashSize;
	@Value("${password.hash.iterations}")
	private int hashIterations;

	public String createPasswordHash(String userPassword) {
		try {
			return PasswordStorage.createHash(userPassword.toCharArray(), saltSize, hashSize, hashIterations);
		} catch (PasswordStorage.CannotPerformOperationException e) {
			//todo MD5 passwords
			return TYPE_CLEAR + userPassword;
		}
	}

	public boolean verifyPassword(String userPasswordSuggest, String savedPasswordHash) {
		if (savedPasswordHash.startsWith(TYPE_CLEAR)) {
			return savedPasswordHash.substring(TYPE_CLEAR.length()).equals(userPasswordSuggest);
		} else {
			try {
				return PasswordStorage.verifyPassword(userPasswordSuggest, savedPasswordHash);
			} catch (PasswordStorage.CannotPerformOperationException | PasswordStorage.InvalidHashException e) {
				LOG.error("Catch exception while verifyPassword('{}', '{}'):", userPasswordSuggest, savedPasswordHash, e);
				return false;
			}
		}
	}
}
