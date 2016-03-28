package name.valery1707.interview.websocket.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class WebProtocol {
	public static final String AUTH = "LOGIN_CUSTOMER";
	public static final String AUTH_SUCCESS = "CUSTOMER_API_TOKEN";
	public static final String AUTH_FAIL = "CUSTOMER_ERROR";
	public static final String ECHO = "ECHO";
	public static final String SERVER_ERROR = "SERVER_ERROR";
	public static final String UNKNOWN_TYPE = "UNKNOWN_TYPE";

	private String type;
	@JsonProperty("sequence_id")
	private String sequenceId;
	private Map<String, String> data;

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
		WebProtocol dst = new WebProtocol();
		dst.setType(SERVER_ERROR);
		dst.setSequenceId(src != null ? src.getSequenceId() : null);
		dst.setData(new HashMap<>(2));
		dst.getData().put("error_code", "server.internalError");
		dst.getData().put("error_description", e.getMessage());
		return dst;
	}

	public static WebProtocol authSuccess(WebProtocol src, String token) {
		WebProtocol dst = new WebProtocol();
		dst.setType(AUTH_SUCCESS);
		dst.setSequenceId(src.getSequenceId());
		dst.setData(new HashMap<>(2));
		dst.getData().put("api_token", token);
		return dst;
	}

	public static WebProtocol unknownType(WebProtocol src) {
		WebProtocol dst = new WebProtocol();
		dst.setType(UNKNOWN_TYPE);
		dst.setSequenceId(src.getSequenceId());
		dst.setData(new HashMap<>(2));
		dst.getData().put("error_code", "messageType.unknown");
		dst.getData().put("error_description", "Unknown message type: " + src.getType());
		return dst;
	}

	public static WebProtocol echo(WebProtocol src) {
		WebProtocol dst = new WebProtocol();
		dst.setType(ECHO);
		dst.setSequenceId(src.getSequenceId());
		dst.setData(new HashMap<>(src.getData()));
		return dst;
	}
}
