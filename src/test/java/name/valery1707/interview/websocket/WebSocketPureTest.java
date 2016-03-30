package name.valery1707.interview.websocket;

import name.valery1707.interview.websocket.domain.WebProtocol;
import name.valery1707.interview.websocket.util.JsonConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
	private JsonConverter converter;

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
				messages.put(converter.fromJSON(wire.getPayload(), WebProtocol.class));
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
		return send(converter.toJSON(request));
	}

	private String toJSON(String src) {
		return src.replace('\'', '"');
	}

	@Test(timeout = 3_000)
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

	@Test(timeout = 3_000)
	public void testIncorrectFormat_token() throws Exception {
		WebProtocol request = new WebProtocol();
		request.setType("UNKNOWN");
		request.setSequenceId(UUID.randomUUID().toString());
		request.getData().put("api_token", "token");
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.INVALID_TOKEN);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "error_code", "error_description")
				.containsEntry("api_token", "token")
				.containsEntry("error_code", "token.invalid")
				.containsEntry("error_description", "Token invalid");
	}

	@Test(timeout = 3_000)
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

	@Test(timeout = 3_000)
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

	@Test(timeout = 3_000)
	public void testAuth_correct() throws Exception {
		auth_correct();
	}

	private WebProtocol auth_correct() throws Exception {
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
		return response;
	}

	private WebProtocol makeEcho(String token, String message) {
		WebProtocol request = new WebProtocol();
		request.setType(WebProtocol.ECHO);
		request.setSequenceId(UUID.randomUUID().toString());
		request.getData().put("api_token", token);
		request.getData().put("message", message);
		return request;
	}

	@Test(timeout = 3_000)
	public void testEcho_unknownToken() throws Exception {
		String token = UUID.randomUUID().toString();
		WebProtocol request = makeEcho(token, "message");
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.INVALID_TOKEN);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "error_code", "error_description")
				.containsEntry("api_token", token)
				.containsEntry("error_code", "token.invalid")
				.containsEntry("error_description", "Token invalid");
	}

	@Test(timeout = 3_000)
	public void testEcho_nullToken() throws Exception {
		WebProtocol request = makeEcho(null, "message");
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.INVALID_TOKEN);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "error_code", "error_description")
				.containsEntry("api_token", null)
				.containsEntry("error_code", "token.invalid")
				.containsEntry("error_description", "Token invalid");
	}

	@Test(timeout = 3_000)
	public void testEcho_nonActualToken() throws Exception {
		String token = "00000000-0000-0000-0000-000000000000";
		WebProtocol request = makeEcho(token, "message");
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.INVALID_TOKEN);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "error_code", "error_description")
				.containsEntry("api_token", token)
				.containsEntry("error_code", "token.invalid")
				.containsEntry("error_description", "Token invalid");
	}

	@Test(timeout = 3_000 * 2)
	public void testEcho_validToken() throws Exception {
		WebProtocol auth = auth_correct();
		String token = auth.getData().get("api_token");
		String message = "message";

		WebProtocol request = makeEcho(token, message);
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.ECHO);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "message")
				.containsEntry("api_token", token)
				.containsEntry("message", message);
	}

	@Test(timeout = 3_000 * 5)
	public void testEcho_renewToken() throws Exception {
		//Auth
		WebProtocol auth = auth_correct();
		String token1 = auth.getData().get("api_token");
		String message1 = "message1";

		//Send message with actual token
		WebProtocol request = makeEcho(token1, message1);
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.ECHO);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "message")
				.containsEntry("api_token", token1)
				.containsEntry("message", message1);

		//Auth again - old tokens will be revoked
		auth = auth_correct();
		String token2 = auth.getData().get("api_token");
		String message2 = "message2";

		//Send message with old token
		request = makeEcho(token1, message1);
		response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.INVALID_TOKEN);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "error_code", "error_description")
				.containsEntry("api_token", token1)
				.containsEntry("error_code", "token.invalid")
				.containsEntry("error_description", "Token invalid");

		//Send message with actual token
		request = makeEcho(token2, message2);
		response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.ECHO);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("api_token", "message")
				.containsEntry("api_token", token2)
				.containsEntry("message", message2);
	}

	@Test(timeout = 3_000 * 2)
	public void testUnknownMessageType() throws Exception {
		WebProtocol auth = auth_correct();
		String token = auth.getData().get("api_token");

		WebProtocol request = new WebProtocol();
		request.setType("UNKNOWN");
		request.setSequenceId(UUID.randomUUID().toString());
		request.getData().put("api_token", token);
		WebProtocol response = send(request);
		assertThat(response)
				.isNotNull();
		assertThat(response.getType())
				.isNotEmpty()
				.isEqualTo(WebProtocol.UNKNOWN_TYPE);
		assertThat(response.getSequenceId())
				.isNotNull()
				.isEqualTo(request.getSequenceId());
		assertThat(response.getData())
				.containsOnlyKeys("type", "error_code", "error_description")
				.containsEntry("type", request.getType())
				.containsEntry("error_code", "messageType.unknown")
				.containsEntry("error_description", "Unknown message type: " + request.getType());
	}

}
