package usatec.com.br.curso_front.modules.user.dto;

public record UserUpdateRequestDTO(
		String name,
		String email,
		Integer role
) {}
