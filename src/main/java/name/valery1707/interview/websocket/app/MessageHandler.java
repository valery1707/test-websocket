package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.domain.WebProtocol;

public interface MessageHandler {
	String getType();

	WebProtocol handle(WebProtocol request);
}
