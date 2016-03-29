package name.valery1707.interview.websocket.util;

import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;

public class ZonedDateTimeRandomGenerator implements RandomGenerator {
	@Override
	public Collection<Class<?>> getTypes() {
		return Collections.singletonList(ZonedDateTime.class);
	}

	@Override
	public Object doGenerate(Class<?> type) {
		ZonedDateTime value = ZonedDateTime.now();
//		value = value.plusYears(RandomFactory.getRandomValue(Short.class));
//		value = value.plusMonths(RandomFactory.getRandomValue(Short.class));
//		value = value.plusWeeks(RandomFactory.getRandomValue(Short.class));
//		value = value.plusDays(RandomFactory.getRandomValue(Short.class));
//		value = value.plusHours(RandomFactory.getRandomValue(Short.class));
		value = value.plusMinutes(RandomFactory.getRandomValue(Short.class));
		value = value.plusSeconds(RandomFactory.getRandomValue(Short.class));
		value = value.plusNanos(RandomFactory.getRandomValue(Short.class));
		return value;
	}
}
