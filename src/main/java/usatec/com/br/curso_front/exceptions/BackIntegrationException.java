package usatec.com.br.curso_front.exceptions;

import lombok.Getter;

@Getter
public class BackIntegrationException extends RuntimeException {
	private final ApiErrorResponse error;

	public BackIntegrationException(ApiErrorResponse error) {
		super(error.message());
		this.error = error;
	}
}
