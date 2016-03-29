package name.valery1707.interview.websocket.domain;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.filters.FilterClassName;
import com.openpojo.reflection.filters.FilterPackageInfo;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import name.valery1707.interview.websocket.util.FilterNot;
import name.valery1707.interview.websocket.util.ZonedDateTimeRandomGenerator;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PojoTest {
	private static final String POJO_PACKAGE = "name.valery1707.interview.websocket.domain";

	@Before
	public void setUp() throws Exception {
		RandomFactory.addRandomGenerator(new ZonedDateTimeRandomGenerator());
	}

	@Test
	public void testPojoStructureAndBehavior() {
		Validator validator = ValidatorBuilder.create()
				// Add Rules to validate structure for POJO_PACKAGE
				// See com.openpojo.validation.rule.impl for more ...
				.with(new GetterMustExistRule())
				.with(new SetterMustExistRule())
				.with(new NoFieldShadowingRule())
				.with(new NoPublicFieldsExceptStaticFinalRule())
				// Add Testers to validate behaviour for POJO_PACKAGE
				// See com.openpojo.validation.test.impl for more ...
				.with(new SetterTester())
				.with(new GetterTester())
				.build();

		validator.validate(POJO_PACKAGE, new FilterPackageInfo(), new FilterNot(new FilterClassName(".*\\.PojoTest.*")));
	}

	@Test
	public void testPojoEquals() throws Exception {
		Account account1 = randomAccount();
		Account account2 = randomAccount();
		Account account3 = randomAccount();
		account3.setId(null);
		Token token = randomToken();
		assertThat(account1.getId())
				.isNotNull();
		assertThat(account3.getId()).isNull();;
		assertThat(account1)
				.isEqualTo(account1)
				.isNotEqualTo(account2)
				.isNotEqualTo(account3)
				.isNotEqualTo(token)
				.isNotEqualTo(null);
		assertEquals(account1, account1);
		assertNotEquals(account1, account2);
		assertNotEquals(account1, account3);
		assertNotEquals(account1, token);
		assertNotEquals(account1, null);
		assertThat(account3).isEqualTo(account3);
	}

	private Account randomAccount() {
		Account account = new Account();
		account.setId(RandomFactory.getRandomValue(Long.class));
		account.setEmail(RandomFactory.getRandomValue(String.class));
		account.setPassword(RandomFactory.getRandomValue(String.class));
		return account;
	}

	private Token randomToken() {
		Token token = new Token();
		token.setId(RandomFactory.getRandomValue(Long.class));
		token.setAccount(randomAccount());
		token.setToken(RandomFactory.getRandomValue(UUID.class));
		token.setExpiration(RandomFactory.getRandomValue(ZonedDateTime.class));
		return token;
	}
}
