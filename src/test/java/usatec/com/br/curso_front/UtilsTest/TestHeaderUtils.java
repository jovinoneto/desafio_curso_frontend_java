package usatec.com.br.curso_front.UtilsTest;

import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

public class TestHeaderUtils {

	@SuppressWarnings({"unchecked"})
	public static <T> HttpEntity<T> captureExchangeEntity(RestTemplate mockRt, String urlPattern, HttpMethod method) {
		ArgumentCaptor<HttpEntity<T>> captor = ArgumentCaptor.forClass(HttpEntity.class);

		// Verifica a interação e captura o HttpEntity enviado ao RestTemplate
		verify(mockRt).exchange(
				contains(urlPattern),
				eq(method),
				captor.capture(),
				any(Class.class)
		);

		return (HttpEntity<T>) captor.getValue();

	}

	@SuppressWarnings({"unchecked"})
	public static <T> HttpEntity<T> capturePostEntity(RestTemplate mockRt, String urlPattern) {
		ArgumentCaptor<HttpEntity<T>> captor = ArgumentCaptor.forClass(HttpEntity.class);

		// Verifica a interação e captura o HttpEntity enviado ao RestTemplate
		verify(mockRt).postForObject(
				contains(urlPattern),
				captor.capture(),
				any(Class.class)
		);

		return (HttpEntity<T>) captor.getValue();
	}

	// Valida o header de autorização
	public static void assertAuthorizationHeader(HttpEntity<?> entity, String expectedToken) {
		assertNotNull(entity, "HttpEntity should not be null");
		assertNotNull(entity.getHeaders(), "Authorization header should not be null");

		String authHeader = entity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		String expectedHeader = expectedToken.startsWith("Bearer ") ? expectedToken : "Bearer " + expectedToken;

		assertEquals(expectedHeader, authHeader, "Authorization header should match");
	}
}
