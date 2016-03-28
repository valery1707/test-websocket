package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.domain.WebProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Component
public class PureController extends TextWebSocketHandler {
	private static final Logger LOG = LoggerFactory.getLogger(PureController.class);

	@Inject
	private MessageProcessor processor;

	@Inject
	private MessageConverter converter;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage wire) throws Exception {
		WebProtocol payload = null;
		WebProtocol result = null;

		try {
			payload = (WebProtocol) converter.fromMessage(new GenericMessage<>(wire.getPayload()), WebProtocol.class);
		} catch (Exception e) {
			LOG.warn("MessageConverter.fromMessage({}): ", wire.getPayload(), e);
			result = WebProtocol.serverError(null, e);
		}

		if (payload != null) {
			result = processor.process(payload);
		}

		if (result != null) {
			session.sendMessage(new TextMessage(toJSON(result)));
		}
	}

	private String toJSON(WebProtocol message) {
		Map<String, Object> headers = new HashMap<>(1);
		headers.put(MessageHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8);
		Message<?> wire = converter.toMessage(message, new MessageHeaders(headers));
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
