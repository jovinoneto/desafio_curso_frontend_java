package usatec.com.br.curso_front.modules;

import org.springframework.ui.Model;
import usatec.com.br.curso_front.exceptions.BackIntegrationException;

public final class Utils {

	private Utils() {}

	public static void addApiError(
			Model model,
			BackIntegrationException e) {

		model.addAttribute("apiError", e.getError());
	}
}
