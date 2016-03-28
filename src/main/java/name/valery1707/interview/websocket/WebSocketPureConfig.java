package name.valery1707.interview.websocket;

import name.valery1707.interview.websocket.app.PureController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.inject.Inject;

@Configuration
@EnableWebSocket
public class WebSocketPureConfig implements WebSocketConfigurer {
	@Inject
	private PureController controller;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(controller, "/pure");
	}
}
