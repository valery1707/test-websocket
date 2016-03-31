package name.valery1707.interview.websocket.crypt;

import name.valery1707.interview.websocket.Launcher;
import org.assertj.core.api.Condition;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PasswordServiceTest {
	@Inject
	private PasswordService service;

	private Condition<String> verify(String description, String passwordSuggest) {
		return new Condition<>(hash -> service.verifyPassword(passwordSuggest, hash), "verify(" + description + ")");
	}

	@Test
	@Ignore
	public void testCreate_secure() throws Exception {
		String hash = service.createPasswordHash("");
		assertThat(hash)
				.isNotNull()
				.isNotEmpty()
				.is(verify("same", ""))
				.isNot(verify("other", "other"));
	}

	@Test
	@Ignore
	public void testVerify_clear() throws Exception {
		String password = "password";
		String hash = PasswordService.TYPE_CLEAR + password;
		assertThat(hash)
				.isNotNull()
				.isNotEmpty()
				.is(verify("same", password))
				.isNot(verify("other", "other"))
				.isNot(verify("null", null));
	}
}
