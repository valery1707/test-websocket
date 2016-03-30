package name.valery1707.interview.websocket.app.handler;

import name.valery1707.interview.websocket.app.MessageHandler;
import name.valery1707.interview.websocket.domain.WebProtocol;
import org.springframework.stereotype.Component;

@Component
public class CurrentTimeHandler implements MessageHandler {
	@Override
	public String getType() {
		return WebProtocol.CURRENT_TIME;
	}

	@Override
	public WebProtocol handle(WebProtocol request) {
		return WebProtocol.currentTime(request);
	}
}
