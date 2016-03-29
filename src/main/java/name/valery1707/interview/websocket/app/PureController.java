package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.domain.WebProtocol;
import name.valery1707.interview.websocket.util.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;

@Component
public class PureController extends TextWebSocketHandler {
	private static final Logger LOG = LoggerFactory.getLogger(PureController.class);

	@Inject
	private MessageProcessor processor;

	@Inject
	private JsonConverter converter;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage wire) throws Exception {
		WebProtocol payload = null;
		WebProtocol result = null;

		try {
			payload = converter.fromJSON(wire.getPayload(), WebProtocol.class);
		} catch (Exception e) {
			LOG.warn("MessageConverter.fromMessage({}): ", wire.getPayload(), e);
			result = WebProtocol.serverError(null, e);
		}

		if (payload != null) {
			result = processor.process(payload);
		}

		session.sendMessage(new TextMessage(converter.toJSON(result)));
	}
}
