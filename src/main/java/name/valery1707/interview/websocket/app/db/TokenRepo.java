package name.valery1707.interview.websocket.app.db;

import name.valery1707.interview.websocket.domain.Account;
import name.valery1707.interview.websocket.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TokenRepo extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {
	@Modifying
	@Query("UPDATE Token t SET t.expiration = NOW() WHERE t.account = :account AND t.expiration > NOW()")
	int revokeActualTokens(@Param("account") Account account);

	@Query("SELECT COUNT(t) FROM Token t WHERE t.token = :token AND t.expiration <= NOW()")
	long countActualTokenByToken(@Param("token") UUID token);
}
