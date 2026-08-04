package usatec.com.br.curso_front.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import usatec.com.br.curso_front.exceptions.ApiAuthenticationException;
import usatec.com.br.curso_front.exceptions.ApiErrorParser;
import usatec.com.br.curso_front.modules.auth.dto.AuthRequestDTO;
import usatec.com.br.curso_front.modules.auth.dto.AuthResponseDTO;
import usatec.com.br.curso_front.modules.user.dto.UserResponseDTO;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExternalAuthenticationProvider implements AuthenticationProvider {

	private final RestTemplate rt;
	@Value("${backend.url}")
	private String baseUrl;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
		String password = (String) authentication.getCredentials();

		try {

			// 1. Chama API externa aqui (o código que estava no seu Service)
			AuthRequestDTO requestDTO = new AuthRequestDTO(email, password);
			// ... (lógica do RestTemplate para pegar o AuthResponseDTO)
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<AuthRequestDTO> request = new HttpEntity<>(requestDTO, headers);

			String url = UriComponentsBuilder.fromUriString(baseUrl).path("/auth/login").toUriString();

			AuthResponseDTO response = rt.postForObject(url, request, AuthResponseDTO.class);

			// 2. Se a autenticação na API externa tiver sucesso:
			if(response != null && response.token() != null) {
				UserResponseDTO userProfile = response.user();
				List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userProfile.role().toUpperCase()));

				// 3. Retorna o token de autenticação que o Spring vai usar
				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(userProfile, null, authorities);

				authenticationToken.setDetails(response.token());

				return authenticationToken;
			}

			return null;
		} catch(ApiAuthenticationException e) {
			throw e;
		} catch (RestClientResponseException e) {
			throw new ApiAuthenticationException(
					ApiErrorParser.parse(e));
		} catch(Exception e) {
			throw new AuthenticationServiceException("Error internal system");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
