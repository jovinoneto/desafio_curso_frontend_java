package usatec.com.br.curso_front.modules.course.dto;

import java.util.UUID;

public record CourseFormDTO(
		UUID id,
		String name,
		UUID categoryId,
		UUID userId
) {}
