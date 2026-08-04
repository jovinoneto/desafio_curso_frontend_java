package usatec.com.br.curso_front.modules.auth.dto;

public class AuthMapper {
	public AuthRequestDTO toRequest(LoginFormDTO form) {
		return new AuthRequestDTO(
				form.email(),
				form.password()
		);
	}
}
