package usatec.com.br.curso_front.exceptions;

import org.springframework.security.core.AuthenticationException;

public class ApiAuthenticationException extends AuthenticationException {
	private final ApiErrorResponse apiError;

	public ApiAuthenticationException(ApiErrorResponse apiError) {
		super(apiError.message());
		this.apiError = apiError;
	}

	public ApiErrorResponse getApiError() {
		return apiError;
	}
}
