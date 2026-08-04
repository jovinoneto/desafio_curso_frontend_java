package usatec.com.br.curso_front.modules.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import usatec.com.br.curso_front.exceptions.ApiErrorResponse;
import usatec.com.br.curso_front.exceptions.BackIntegrationException;
import usatec.com.br.curso_front.modules.Utils;
import usatec.com.br.curso_front.modules.user.dto.*;
import usatec.com.br.curso_front.modules.user.enums.UserRole;
import usatec.com.br.curso_front.modules.user.service.UserService;
import usatec.com.br.curso_front.securityConfig.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

	private final SecurityUtils securityUtils;
	private final UserService userService;

	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("user", new UserRequestDTO("", "", "", ""));
		return "user/create";
	}

	@PostMapping("/create")
	public String saveCreateForm(UserRequestDTO request, Model model) {
		if(!request.password().equals(request.passwordConfirm())) {
			model.addAttribute("user", request);
			model.addAttribute(
					"apiError",
					ApiErrorResponse.global("Passwords do not match"));

			return "user/create";
		}
		try {
			userService.create(request);

			return "redirect:/auth/login";
		} catch (BackIntegrationException e) {
			model.addAttribute("user", request);
			Utils.addApiError(model, e);

			return "user/create";
		}
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile/me")
	public String showProfileForm(Model model) {
		UserResponseDTO user = securityUtils.getAuthenticatedUser();
		model.addAttribute("user", user);

		return "user/profile-me";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/update/me")
	public String showUpdateMeForm(@AuthenticationPrincipal UserResponseDTO request, Model model) {
		model.addAttribute("user", request);

		return "user/update-me";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/update/me")
	public String SaveUpdateMe(@ModelAttribute("request") UserUpdateMeRequestDTO request, Model model) {
		try {
			userService.updateMe(request, securityUtils.getToken());

			return "redirect:/user/profile-me";
		} catch (BackIntegrationException e) {
			model.addAttribute("user", request);
			Utils.addApiError(model, e);

			return "user/update-me";
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@GetMapping("/filter")
	public String showSearchForm(@RequestParam(value = "search", required = false, defaultValue = "false")
			boolean isSearching, UserFilterDTO filter, Model model) {

		if(filter == null) {
			filter = new UserFilterDTO(null, null);
		}

		model.addAttribute("filter", filter);
		model.addAttribute("allRoles", UserRole.values());

		if(isSearching) {
			List<UserResponseDTO> users = userService.listFilter(filter, securityUtils.getToken());
			model.addAttribute("users", users);
		} else {
			model.addAttribute("users", Collections.emptyList());
		}

		return "user/filter";
	}

	@GetMapping("/profile-redirect")
	public String redirectProfile(@RequestParam("selectedId") UUID selectedId) {
		return "redirect:/user/profile/" + selectedId;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@GetMapping("/profile/{id}")
	public String showProfileForm(@PathVariable UUID id, Model model) {
		UserResponseDTO currentUser = userService.findById(id, securityUtils.getToken());

		model.addAttribute("user", currentUser);

		return "user/profile";
	}

	@GetMapping("/update-redirect")
	public String redirectUpdate(@RequestParam("selectedId") UUID selectedId) {
		return "redirect:/user/update/" + selectedId;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@GetMapping("/update/{id}")
	public String showUpdateForm(@PathVariable UUID id, Model model) {
		UserResponseDTO currentUser = userService.findById(id, securityUtils.getToken());

		populateUpdateForm(model, currentUser, toUpdateRequest(currentUser));

		return "user/update";
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/update/{id}")
	public String saveUpdateForm(@PathVariable UUID id, UserUpdateRequestDTO request, Model model) {
		try {
			userService.update(id, request, securityUtils.getToken());

			return "redirect:/user/filter";
		} catch (BackIntegrationException e) {
			UserResponseDTO user = userService.findById(id, securityUtils.getToken());

			populateUpdateForm(model, user, request);
			Utils.addApiError(model, e);

			return "user/update";
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") UUID id, RedirectAttributes ra, Model model) {
		try {
			userService.delete(id, securityUtils.getToken());
			ra.addFlashAttribute("success", "User has been deleted");

			return "redirect:/user/filter";
		} catch(BackIntegrationException e) {
			UserResponseDTO currentUser = userService.findById(id, securityUtils.getToken());

			model.addAttribute("user", currentUser);
			Utils.addApiError(model, e);

			return "user/profile";

		}
	}

	private UserUpdateRequestDTO toUpdateRequest(UserResponseDTO user) {
		return new UserUpdateRequestDTO(
				user.name(),
				user.email(),
				user.getRoleId()
		);
	}

	private void populateUpdateForm(Model model, UserResponseDTO currentUser, UserUpdateRequestDTO form) {

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("userRequest", form);
		model.addAttribute("allRoles", UserRole.values());
	}
}
