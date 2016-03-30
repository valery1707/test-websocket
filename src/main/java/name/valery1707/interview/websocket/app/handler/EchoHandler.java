package name.valery1707.interview.websocket.app.handler;

import name.valery1707.interview.websocket.app.MessageHandler;
import name.valery1707.interview.websocket.domain.WebProtocol;
import org.springframework.stereotype.Component;

@Component
public class EchoHandler implements MessageHandler {

	@Override
	public String getType() {
		return WebProtocol.ECHO;
	}

	@Override
	public WebProtocol handle(WebProtocol request) {
		return WebProtocol.echo(request);
	}
}
