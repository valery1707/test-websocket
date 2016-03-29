package name.valery1707.interview.websocket.util;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FilterNot implements PojoClassFilter {
	private final PojoClassFilter delegate;

	public FilterNot(PojoClassFilter delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean include(PojoClass pojoClass) {
		return !delegate.include(pojoClass);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FilterNot filterNot = (FilterNot) o;

		return new EqualsBuilder()
				.append(delegate, filterNot.delegate)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(delegate)
				.toHashCode();
	}
}
