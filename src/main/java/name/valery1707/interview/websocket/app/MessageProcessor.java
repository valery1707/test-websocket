package name.valery1707.interview.websocket.app;

import name.valery1707.interview.websocket.domain.Account;
import name.valery1707.interview.websocket.domain.Token;
import name.valery1707.interview.websocket.domain.WebProtocol;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
public class MessageProcessor {
	@Inject
	private AuthorizationService authorizationService;

	@Inject
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private List<MessageHandler> handlerList;
	private Map<String, MessageHandler> handlerMap;

	@PostConstruct
	public void init() {
		handlerMap = handlerList.stream()
				.collect(toMap(MessageHandler::getType, h -> h));
	}

	public WebProtocol process(WebProtocol src) {
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
		MessageHandler handler = handlerMap.get(src.getType());
		if (handler != null) {
			return handler.handle(src);
		} else {
			return WebProtocol.unknownType(src);
		}
	}
}
