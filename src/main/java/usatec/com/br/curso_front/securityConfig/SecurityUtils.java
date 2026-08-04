package usatec.com.br.curso_front.securityConfig;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import usatec.com.br.curso_front.modules.user.dto.UserResponseDTO;

@Component
public class SecurityUtils {

	public String getToken() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getDetails() instanceof String) {
			return (String) auth.getDetails();
		}
		return null;
	}

	public UserResponseDTO getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserResponseDTO) {
			return (UserResponseDTO) auth.getPrincipal();
		}
		return null;
	}
}
