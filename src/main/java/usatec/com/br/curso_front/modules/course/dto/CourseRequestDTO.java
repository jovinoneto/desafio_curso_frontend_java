package usatec.com.br.curso_front.modules.course.dto;

import java.util.UUID;

public record CourseRequestDTO(
		String name,
		UUID categoryId,
		UUID userId
) {}
