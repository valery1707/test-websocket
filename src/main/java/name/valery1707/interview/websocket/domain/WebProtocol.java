package name.valery1707.interview.websocket.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class WebProtocol {
	public static final String AUTH = "LOGIN_CUSTOMER";
	public static final String AUTH_SUCCESS = "CUSTOMER_API_TOKEN";
	public static final String AUTH_FAIL = "CUSTOMER_ERROR";
	public static final String ECHO = "ECHO";
	public static final String SERVER_ERROR = "SERVER_ERROR";
	public static final String UNKNOWN_TYPE = "UNKNOWN_TYPE";
	public static final String INVALID_TOKEN = "INVALID_TOKEN";
	public static final String CURRENT_TIME = "CURRENT_TIME";

	private String type;
	@JsonProperty("sequence_id")
	private String sequenceId;
	private Map<String, String> data = new HashMap<>();

	public WebProtocol() {
	}

	public WebProtocol(String type, String sequenceId) {
		this();
		this.type = type;
		this.sequenceId = sequenceId;
	}

	public WebProtocol(String type, @Nullable WebProtocol src) {
		this(type, src != null ? src.getSequenceId() : null);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public static WebProtocol serverError(@Nullable WebProtocol src, Exception e) {
		WebProtocol dst = new WebProtocol(SERVER_ERROR, src);
		dst.getData().put("error_code", "server.internalError");
		dst.getData().put("error_description", e.getMessage());
		return dst;
	}

	public static WebProtocol authSuccess(WebProtocol src, Token token) {
		WebProtocol dst = new WebProtocol(AUTH_SUCCESS, src);
		dst.getData().put("api_token", token.getToken().toString());
		dst.getData().put("api_token_expiration_date", token.getExpiration().format(DateTimeFormatter.ISO_DATE_TIME));
		return dst;
	}

	public static WebProtocol authFail(WebProtocol src) {
		WebProtocol dst = new WebProtocol(AUTH_FAIL, src);
		dst.getData().put("error_code", "customer.notFound");
		dst.getData().put("error_description", "Customer not found");
		return dst;
	}

	public static WebProtocol unknownType(WebProtocol src) {
		WebProtocol dst = new WebProtocol(UNKNOWN_TYPE, src);
		dst.getData().put("error_code", "messageType.unknown");
		dst.getData().put("error_description", "Unknown message type: " + src.getType());
		dst.getData().put("type", src.getType());
		return dst;
	}

	public static WebProtocol echo(WebProtocol src) {
		WebProtocol dst = new WebProtocol(ECHO, src);
		dst.setData(new HashMap<>(src.getData()));
		return dst;
	}

	public static WebProtocol invalidToken(WebProtocol src) {
		WebProtocol dst = new WebProtocol(INVALID_TOKEN, src);
		dst.getData().put("api_token", src.getData().get("api_token"));
		dst.getData().put("error_code", "token.invalid");
		dst.getData().put("error_description", "Token invalid");
		return dst;
	}

	public static WebProtocol currentTime(WebProtocol request) {
		WebProtocol response = new WebProtocol(CURRENT_TIME, request);
		response.getData().put("current_time", ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_DATE_TIME));
		return response;
	}
}
