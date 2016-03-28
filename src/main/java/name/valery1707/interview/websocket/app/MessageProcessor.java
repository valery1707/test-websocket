package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.domain.WebProtocol;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {

	public WebProtocol process(WebProtocol src) {
		try {
			Thread.sleep(300); // simulated delay
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (WebProtocol.AUTH.equals(src.getType())) {
			return WebProtocol.authSuccess(src, "token");
		} else if (WebProtocol.ECHO.equals(src.getType())) {
			return WebProtocol.echo(src);
		} else {
			return WebProtocol.unknownType(src);
		}
	}
}
