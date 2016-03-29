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
import org.junit.Test;

public class PojoTest {
	private static final String POJO_PACKAGE = "name.valery1707.interview.websocket.domain";

	@Test
	public void testPojoStructureAndBehavior() {
		RandomFactory.addRandomGenerator(new ZonedDateTimeRandomGenerator());
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
}
