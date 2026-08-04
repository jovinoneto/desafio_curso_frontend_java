package usatec.com.br.curso_front.modules.auth.dto;


import usatec.com.br.curso_front.modules.user.dto.UserResponseDTO;

public record AuthResponseDTO(
		String token,
		UserResponseDTO user
) {}
