package usatec.com.br.curso_front.UtilsTest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

public class HttpExceptionFactory {

	public static HttpClientErrorException create(HttpStatus status, String responseBody) {
		return HttpClientErrorException.create(
				status,
				status.getReasonPhrase(),
				HttpHeaders.EMPTY,
				responseBody != null ? responseBody.getBytes(StandardCharsets.UTF_8) : new byte[0],
				StandardCharsets.UTF_8
		);
	}

	public static HttpClientErrorException notFound(String responseBody) {
		return create(HttpStatus.NOT_FOUND, responseBody);
	}

	public static HttpClientErrorException badRequest(String responseBody) {
		return create(HttpStatus.BAD_REQUEST, responseBody);
	}

	public static HttpClientErrorException conflict(String responseBody) {
		return create(HttpStatus.CONFLICT, responseBody);
	}
}
