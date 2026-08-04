package usatec.com.br.curso_front.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
/*
	// captura erros de integração com o Backend
	@ExceptionHandler(BackIntegrationException.class)
	public String handleBackIntegrationException(
			BackIntegrationException e,
			RedirectAttributes ra) {
		// guarda a mensagem de erro para exibir após o redirecionamento
		ra.addFlashAttribute("message",
				e.getError().message());

		ra.addFlashAttribute("errors",
				e.getError().errors());

		return "redirect:/auth/login";
	}
*/


	// captura erros
	@ExceptionHandler(Exception.class)
	public ModelAndView handleGenericException(Exception e) {

		ModelAndView modelAndView = new ModelAndView("error/500");
		modelAndView.addObject("errorMessage", e.getMessage());

		return modelAndView;
	}
}