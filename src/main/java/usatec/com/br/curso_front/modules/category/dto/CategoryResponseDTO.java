package usatec.com.br.curso_front.modules.category.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponseDTO(
		UUID id,
		String name,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
