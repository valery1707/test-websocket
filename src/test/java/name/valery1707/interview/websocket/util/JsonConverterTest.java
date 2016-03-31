package name.valery1707.interview.websocket.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import name.valery1707.interview.websocket.domain.WebProtocol;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

import java.nio.charset.Charset;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JsonConverterTest {
	@Mock
	private MessageConverter messageConverter;

	@InjectMocks
	private JsonConverter jsonConverter;

	private <T> void testToJSON(Function<String, T> mapper) throws Exception {
		ObjectMapper jsonMapper = new ObjectMapper();
		when(messageConverter.toMessage(anyObject(), anyObject()))
				.thenAnswer(invocation -> {
					Object object = invocation.getArguments()[0];
					String json = jsonMapper.writeValueAsString(object);
					return new GenericMessage<>(mapper.apply(json));
				});
		WebProtocol src = WebProtocol.currentTime(null);
		String testJson = jsonConverter.toJSON(src);
		String realJson = jsonMapper.writeValueAsString(src);
		assertThat(testJson)
				.isNotNull()
				.isEqualToIgnoringWhitespace(realJson);
	}

	@Test
	public void testToJSON_bytes() throws Exception {
		testToJSON(s -> s.getBytes(Charset.forName("UTF-8")));
	}

	@Test
	public void testToJSON_string() throws Exception {
		testToJSON(s -> s);
	}

	@Test
	public void testToJSON_object() throws Exception {
		testToJSON(StringBuilder::new);
		testToJSON(StringBuffer::new);
	}
}