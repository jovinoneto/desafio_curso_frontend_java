package usatec.com.br.curso_front.modules.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import usatec.com.br.curso_front.exceptions.BackIntegrationException;
import usatec.com.br.curso_front.modules.Utils;
import usatec.com.br.curso_front.modules.category.service.CategoryService;
import usatec.com.br.curso_front.modules.course.dto.CourseFilterDTO;
import usatec.com.br.curso_front.modules.course.dto.CourseFormDTO;
import usatec.com.br.curso_front.modules.course.dto.CourseMapper;
import usatec.com.br.curso_front.modules.course.dto.CourseResponseDTO;
import usatec.com.br.curso_front.modules.course.service.CourseService;
import usatec.com.br.curso_front.modules.user.service.UserService;
import usatec.com.br.curso_front.securityConfig.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

	private final SecurityUtils securityUtils;
	private final CourseMapper courseMapper;
	private final UserService userService;
	private final CourseService courseService;
	private final CategoryService categoryService;

	@GetMapping("/list")
	public String getList(Model model) {
		List<CourseResponseDTO> courses = courseService.list();
		model.addAttribute("courses", courses);

		return "course/list";
	}

	@GetMapping("/create")
	public String showCreateForm(Model model) {
		prepareCreateForm(model, new CourseFormDTO(null, null, null, null));

		return "course/create";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/create")
	public String saveCreateForm(@ModelAttribute("course") CourseFormDTO form, Model  model) {
		try {
			courseService.create(
					courseMapper.toRequest(form),
					securityUtils.getToken());

			return "redirect:/course/list";
		} catch (BackIntegrationException e) {
			prepareCreateForm(model, form);
			Utils.addApiError(model, e);

			return "course/create";
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@GetMapping("/filter")
	public String showActiveForm(
			@RequestParam(defaultValue = "false") boolean search,
			@ModelAttribute("filter") CourseFilterDTO filter, Model model) {

		if(filter == null) {
			filter = new CourseFilterDTO(null, null);
		}

		model.addAttribute("filter", filter);

		if(search) {
			model.addAttribute(
					"courses",
					courseService.listFilter(
							filter,
							securityUtils.getToken())
					);
		} else {
			model.addAttribute("courses", Collections.emptyList());
		}
		return "course/filter";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/{id}/active")
	public String saveActiveForm(@PathVariable UUID id, Model model) {
	 	CourseResponseDTO updateCourse = courseService.toggle(id, securityUtils.getToken());

		model.addAttribute("course", updateCourse);

		return "course/filter :: courseActiveToggle";
	}

	@GetMapping("/update-redirect")
	public String redirectUpdateForm(@RequestParam("selectedId") UUID selectedId) {

		return "redirect:/course/update/" + selectedId;
	}

	@GetMapping("/details-redirect")
	public String redirectDetailsForm(@RequestParam("selectedId") UUID selectedId) {
		return "redirect:/course/details/" + selectedId;
	}

	@GetMapping("/details/{id}")
	public String getDetails(@PathVariable("id") UUID id, Model model) {
		CourseResponseDTO currentCourse = courseService.findById(id);

		model.addAttribute("course", currentCourse);

		return "course/details";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") UUID id, RedirectAttributes ra, Model model) {
		try {
			courseService.delete(id,  securityUtils.getToken());
			ra.addFlashAttribute("success", "Course has been deleted");

			return "redirect:/course/filter";
		} catch(BackIntegrationException e) {
			CourseResponseDTO currentCourse = courseService.findById(id);

			model.addAttribute("course", currentCourse);
			Utils.addApiError(model, e);

			return "course/details";
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@GetMapping("/update/{id}")
	public String showUpdateForm(@PathVariable UUID id, Model model) {
		CourseResponseDTO currentCourse = courseService.findById(id);

		prepareUpdateForm(model, currentCourse, courseMapper.toForm(currentCourse));
		loadFormData(model);

		return "course/update";
	}

	// atualizar curso
	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/update/{id}")
	public String saveUpdateForm(@PathVariable UUID id, @ModelAttribute("course") CourseFormDTO form, Model  model) {
		try {
			courseService.update(
					id,
					courseMapper.toRequest(form),
					securityUtils.getToken()
			);

			return "redirect:/course/filter";
		} catch (BackIntegrationException e) {
			CourseResponseDTO currentCourse = courseService.findById(id);

			prepareUpdateForm(model, currentCourse, form);
			Utils.addApiError(model, e);

			return "course/update";
		}
	}

	private void loadFormData(Model model) {
		model.addAttribute("categories",
				categoryService.list());
		model.addAttribute("users",
				userService.listTeachers(
						securityUtils.getToken()));
	}

	private void prepareCreateForm(Model model,
			CourseFormDTO form) {

		model.addAttribute("courseRequest", form);
		loadFormData(model);
	}

	private void prepareUpdateForm(Model model,
			CourseResponseDTO currentCourse,
			CourseFormDTO form) {

		model.addAttribute("currentCourse", currentCourse);
		model.addAttribute("courseRequest", form);
		loadFormData(model);
	}
}
