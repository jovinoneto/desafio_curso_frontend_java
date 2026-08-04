package usatec.com.br.curso_front.exceptions;

import java.util.Map;

public record ApiErrorResponse(
		String message,
		Map<String, String> errors
) {
	public static ApiErrorResponse global(String message) {
		return new ApiErrorResponse("", Map.of("global", message));
	}

	public static ApiErrorResponse field(String field, String message) {
		return new ApiErrorResponse("", Map.of(field, message));
	}

	public boolean hasGlobalError() {
		return errors != null && errors.containsKey("global");
	}

	public String globalError() {
		return hasGlobalError() ? errors.get("global") : null;
	}

	public boolean hasFieldError(String field) {
		return errors != null && errors.containsKey(field);
	}

	public String fieldError(String field) {
		return hasFieldError(field) ? errors.get(field) : null;
	}
}
