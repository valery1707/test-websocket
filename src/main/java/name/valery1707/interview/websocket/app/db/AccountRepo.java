package name.valery1707.interview.websocket.app.db;

import name.valery1707.interview.websocket.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepo extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
	Account getByEmail(String email);
}
