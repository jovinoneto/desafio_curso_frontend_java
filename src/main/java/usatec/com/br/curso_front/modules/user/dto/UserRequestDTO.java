package usatec.com.br.curso_front.modules.user.dto;

public record UserRequestDTO(
		String name,
		String email,
		String password,
		String passwordConfirm
) {
}
