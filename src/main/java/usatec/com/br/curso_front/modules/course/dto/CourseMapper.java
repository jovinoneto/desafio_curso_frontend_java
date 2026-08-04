package usatec.com.br.curso_front.modules.course.dto;

import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
	public CourseFormDTO toForm(CourseResponseDTO dto) {
		return new CourseFormDTO(
				dto.id(),
				dto.name(),
				dto.category() != null ? dto.category().id() : null,
				dto.user() != null ? dto.user().id() : null
		);
	}

	public CourseRequestDTO toRequest(CourseFormDTO form) {
		return new CourseRequestDTO(
				form.name(),
				form.categoryId(),
				form.userId()
		);
	}
}
