package usatec.com.br.curso_front.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ApiResponseErrorHandler implements ResponseErrorHandler {

	private final ObjectMapper objectMapper;

	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isError();
	}

	public void handleError(ClientHttpResponse response) throws IOException {

		ApiErrorResponse apiError;

		try {

			apiError = objectMapper.readValue(
					response.getBody(),
					ApiErrorResponse.class);

		} catch (Exception ex) {

			apiError = new ApiErrorResponse(
					"Unexpected error.",
					Collections.emptyMap());

		}

		throw new BackIntegrationException(apiError);
	}
}
