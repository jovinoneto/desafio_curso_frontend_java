package usatec.com.br.curso_front.modules.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import usatec.com.br.curso_front.exceptions.ApiErrorResponse;
import usatec.com.br.curso_front.modules.auth.dto.LoginFormDTO;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

	@GetMapping("/login")
	public String loginPage(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);
		LoginFormDTO login = new LoginFormDTO("", "");

		if(session != null) {
			ApiErrorResponse apiError = (ApiErrorResponse) session.getAttribute("apiError");

			if(apiError != null) {
				model.addAttribute("apiError", apiError);
				session.removeAttribute("apiError");
			}

			String loginEmail = (String) session.getAttribute("loginEmail");

			if(loginEmail != null) {
				login = new LoginFormDTO(loginEmail, "");
				session.removeAttribute("loginEmail");
			}
		}
		model.addAttribute("login", login);

		return "auth/login";
	}
}
