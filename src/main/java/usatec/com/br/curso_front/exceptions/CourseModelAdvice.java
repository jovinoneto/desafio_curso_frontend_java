package usatec.com.br.curso_front.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import usatec.com.br.curso_front.modules.category.dto.CategoryResponseDTO;
import usatec.com.br.curso_front.modules.category.service.CategoryService;
import usatec.com.br.curso_front.modules.course.controller.CourseController;
import usatec.com.br.curso_front.modules.user.dto.UserResponseDTO;
import usatec.com.br.curso_front.modules.user.service.UserService;
import usatec.com.br.curso_front.securityConfig.SecurityUtils;

import java.util.List;

@ControllerAdvice(assignableTypes = CourseController.class)
@RequiredArgsConstructor
public class CourseModelAdvice {

	private final CategoryService categoryService;
	private final UserService userService;
	private final SecurityUtils securityUtils;

	@ModelAttribute("categories")
	public List<CategoryResponseDTO> categories() {
		return categoryService.list();
	}

	@ModelAttribute("users")
	public List<UserResponseDTO> users() {
		return userService.listUser(securityUtils.getToken());
	}
}
