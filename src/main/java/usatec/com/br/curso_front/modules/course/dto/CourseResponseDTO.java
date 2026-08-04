package usatec.com.br.curso_front.modules.course.dto;

import usatec.com.br.curso_front.modules.category.dto.CategoryResponseDTO;
import usatec.com.br.curso_front.modules.user.dto.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseResponseDTO(
		UUID id,
		String name,
		Boolean active,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		CategoryResponseDTO category,
		UserResponseDTO user
) {}
