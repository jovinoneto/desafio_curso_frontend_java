package usatec.com.br.curso_front.modules.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import usatec.com.br.curso_front.exceptions.BackIntegrationException;
import usatec.com.br.curso_front.modules.Utils;
import usatec.com.br.curso_front.modules.category.dto.CategoryRequestDTO;
import usatec.com.br.curso_front.modules.category.dto.CategoryResponseDTO;
import usatec.com.br.curso_front.modules.category.service.CategoryService;
import usatec.com.br.curso_front.securityConfig.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {

	private final SecurityUtils securityUtils;
	private final CategoryService categoryService;

	@GetMapping("/list")
	public String getList(Model model) {
		List<CategoryResponseDTO> categories = categoryService.list();

		model.addAttribute("categories", categories);

		return "category/list";
	}

	@GetMapping("/create")
	public String showCreateForm(Model model) {
		prepareCreateForm(model, new CategoryRequestDTO(""));

		return "category/create";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/create")
	public String saveCreateForm(@ModelAttribute("category") CategoryRequestDTO request, Model model) {
		try {
			categoryService.create(request, securityUtils.getToken());

			return "redirect:/category/list";
		} catch (BackIntegrationException e) {
			prepareCreateForm(model, request);
			Utils.addApiError(model, e);

			return "category/create";
		}
	}

	@GetMapping("/update-redirect")
	public String redirectUpdate(@RequestParam("selectedId") UUID selectedId) {
		return "redirect:/category/update/" + selectedId;
	}

	@GetMapping("/update/{id}")
	public String showUpdateForm(@PathVariable("id") UUID id, Model model) {
		CategoryResponseDTO currentCategory = categoryService.findById(id);

		prepareUpdateForm(model, currentCategory, new CategoryRequestDTO(currentCategory.name()));

		return "category/update";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/update/{id}")
	public String updateForm(
			@PathVariable("id") UUID id,
			@ModelAttribute("category") CategoryRequestDTO request,
			Model model) {
		try {
			categoryService.update(id, request, securityUtils.getToken());

			return "redirect:/category/filter";
		} catch (BackIntegrationException e) {
			CategoryResponseDTO currentCategory = categoryService.findById(id);

			prepareUpdateForm(model, currentCategory, request);
			Utils.addApiError(model, e);

			return "category/update";
		}
	}

	@GetMapping("/details-redirect")
	public String redirectDetails(@RequestParam("selectedId") UUID selectedId) {
		return "redirect:/category/details/" + selectedId;
	}

	@GetMapping("/details/{id}")
	public String showDetailsForm(@PathVariable("id") UUID id, Model model) {
		CategoryResponseDTO currentCategory = categoryService.findById(id);

		model.addAttribute("category", currentCategory);

		return "category/details";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") UUID id, RedirectAttributes ra, Model model) {
		try {
			categoryService.delete(id, securityUtils.getToken());
			ra.addFlashAttribute("success", "Category has been deleted");

			return "redirect:/category/filter";
		} catch (BackIntegrationException e) {
			CategoryResponseDTO currentCategory = categoryService.findById(id);

			model.addAttribute("category", currentCategory);
			Utils.addApiError(model, e);

			return "category/details";
		}
	}

	@GetMapping("/filter")
	public String getNameForm(@RequestParam(value = "name", required = false) String name,
			@RequestParam(defaultValue = "false") boolean search,
			Model model) {

		model.addAttribute("name", name);
		if (search) {
			model.addAttribute("categories", categoryService.listFilter(name));
		} else {
			model.addAttribute("categories", Collections.emptyList());
		}

		return "category/filter";
	}

	private void prepareCreateForm(
			Model model,
			CategoryRequestDTO form) {

		model.addAttribute("category", form);
	}

	private void prepareUpdateForm(
			Model model,
			CategoryResponseDTO currentCategory,
			CategoryRequestDTO form) {

		model.addAttribute("currentCategory", currentCategory);
		model.addAttribute("category", form);
	}


}
