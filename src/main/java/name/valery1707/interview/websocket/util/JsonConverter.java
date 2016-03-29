package name.valery1707.interview.websocket.util;

import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Component
@Singleton
public class JsonConverter {
	@Inject
	private MessageConverter converter;

	@SuppressWarnings("unchecked")
	public <T> T fromJSON(String value, Class<T> type) {
		return (T) converter.fromMessage(new GenericMessage<>(value), type);
	}

	public <T> String toJSON(T value) {
		Map<String, Object> headers = new HashMap<>(1);
		headers.put(MessageHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8);
		Message<?> wire = converter.toMessage(value, new MessageHeaders(headers));
		Object payload = wire.getPayload();
		String json;
		if (payload instanceof byte[]) {
			json = new String((byte[]) payload);
		} else if (payload instanceof String) {
			json = (String) payload;
		} else {
			json = payload.toString();
		}
		return json;
	}
}
