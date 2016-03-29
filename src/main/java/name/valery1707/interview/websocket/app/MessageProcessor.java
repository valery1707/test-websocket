package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.domain.Account;
import name.valery1707.interview.websocket.domain.Token;
import name.valery1707.interview.websocket.domain.WebProtocol;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class MessageProcessor {
	@Inject
	private AuthorizationService authorizationService;

	public WebProtocol process(WebProtocol src) {
		try {
			Thread.sleep(300); // simulated delay
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (WebProtocol.AUTH.equals(src.getType())) {
			try {
				Token token = authorizationService.auth(new Account(src.getData()));
				return WebProtocol.authSuccess(src, token);
			} catch (IllegalStateException e) {
				return WebProtocol.authFail(src);
			}
		}
		if (!authorizationService.isTokenValid(src.getData())) {
			return WebProtocol.invalidToken(src);
		}
		if (WebProtocol.ECHO.equals(src.getType())) {
			return WebProtocol.echo(src);
		} else {
			return WebProtocol.unknownType(src);
		}
	}
}
