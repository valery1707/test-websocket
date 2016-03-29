package name.valery1707.interview.websocket;

import name.valery1707.interview.websocket.domain.WebProtocol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class WebSocketPureTest {

	@Inject
	private MessageConverter converter;

	@Value("${local.server.port}")
	int port;

	private final BlockingQueue<WebProtocol> messages = new LinkedTransferQueue<>();
	private WebSocketSession session;

	@Before
	public void setUp() throws Exception {
		BlockingQueue<WebSocketSession> sessions = new LinkedTransferQueue<>();
		WebSocketClient client = new JettyWebSocketClient();
		WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(client, new TextWebSocketHandler() {
			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				sessions.put(session);
			}

			@Override
			protected void handleTextMessage(WebSocketSession session, TextMessage wire) throws Exception {
				WebProtocol payload = (WebProtocol) converter.fromMessage(new GenericMessage<>(wire.getPayload()), WebProtocol.class);
				messages.put(payload);
			}
		}, "ws://localhost:{port}/pure", port);
		connectionManager.start();
		session = sessions.take();
	}

	private WebProtocol send(String request) throws IOException, InterruptedException {
		session.sendMessage(new TextMessage(request));
		return messages.take();
	}

	private WebProtocol send(WebProtocol request) throws IOException, InterruptedException {
		return send(toJSON(request));
	}

	//todo Create common bean
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

	private String toJSON(String src) {
		return src.replace('\'', '"');
	}

	@Test(timeout = 10_000)
	public void testIncorrectFormat() throws Exception {
		WebProtocol response = send(toJSON("{'data':'" + WebProtocol.AUTH + "'}"));
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.SERVER_ERROR);
		assertThat(response.getSequenceId())
				.isNullOrEmpty();
		assertThat(response.getData())
				.containsOnlyKeys("error_code", "error_description")
				.containsEntry("error_code", "server.internalError");
	}

	@Test(timeout = 10_000)
	public void testAuth_empty() throws Exception {
		WebProtocol response = send(toJSON("{'type':'" + WebProtocol.AUTH + "'}"));
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.AUTH_FAIL);
		assertThat(response.getSequenceId())
				.isNullOrEmpty();
		assertThat(response.getData())
				.containsOnlyKeys("error_code", "error_description")
				.containsEntry("error_code", "customer.notFound")
				.containsEntry("error_description", "Customer not found");
	}

	private WebProtocol makeAuth(String email, String password) {
		WebProtocol request = new WebProtocol();
		request.setType(WebProtocol.AUTH);
		request.setSequenceId(UUID.randomUUID().toString());
		request.setData(new HashMap<>(2));
		request.getData().put("email", email);
		request.getData().put("password", password);
		return request;
	}

	@Test(timeout = 10_000)
	public void testAuth_unknown() throws Exception {
		WebProtocol request = makeAuth("unknown", "bad");
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.AUTH_FAIL);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("error_code", "error_description")
				.containsEntry("error_code", "customer.notFound")
				.containsEntry("error_description", "Customer not found");
	}

	@Test(timeout = 10_000)
	public void testAuth_correct() throws Exception {
		WebProtocol request = makeAuth("fpi@bk.ru", "123123");
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.AUTH_SUCCESS);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "api_token_expiration_date");
	}
}
