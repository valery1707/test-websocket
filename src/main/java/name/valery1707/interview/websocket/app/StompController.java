package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.domain.WebProtocol;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller
public class StompController {

	@Inject
	private MessageProcessor processor;

	@MessageMapping("/stomp")
	@SendTo("/topic/stomp")
	public WebProtocol process(WebProtocol message) {
		//todo At current time this work in broadcast mode - this is incorrect
		return processor.process(message);
	}
}
