package usatec.com.br.curso_front.exceptions;

import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

public class ApiErrorParser {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static ApiErrorResponse parse(RestClientResponseException e) {

		try {
			return mapper.readValue(
					e.getResponseBodyAsString(),
					ApiErrorResponse.class);

		} catch (Exception ex) {

			return new ApiErrorResponse(
					"An unexpected error occurred.",
					Collections.emptyMap());
		}
	}

	public static ApiErrorResponse parseError(String errorJson) {
		try {
			return mapper.readValue(errorJson, ApiErrorResponse.class);

		} catch (Exception ex) {

			return new ApiErrorResponse(
					"An error occurred, please try again later.",
					Collections.emptyMap()
			);
		}

		/*
		try {
			JsonNode root = mapper.readTree(errorJson);

			// 1. Verifica erros de validação de campos (objeto "errors")
			if (root.has("errors")) {
				JsonNode errors = root.get("errors");

				if (errors.isObject() && errors.iterator().hasNext()) {
					JsonNode firstErrorMessageNode = errors.iterator().next();

					// Substituído asText() por textValue() para atender à especificação do tools.jackson
					String message = firstErrorMessageNode.textValue();
					if (message != null && !message.isBlank()) {
						return message;
					}
				}
			}

			// 2. Verifica mensagens de erro globais (chave "message")
			if (root.has("message")) {
				// Substituído asText() por textValue()
				String message = root.get("message").textValue();
				if (message != null && !message.isBlank()) {
					return message;
				}
			}

		} catch (Exception ignored) {
			// Se falhar o parse, segue para o retorno padrão
		}


		return "An error occurred, please try again later.";

		 */
	}
}
