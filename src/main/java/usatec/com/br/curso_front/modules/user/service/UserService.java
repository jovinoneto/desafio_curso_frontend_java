package usatec.com.br.curso_front.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import usatec.com.br.curso_front.exceptions.ApiErrorParser;
import usatec.com.br.curso_front.exceptions.BackIntegrationException;
import usatec.com.br.curso_front.modules.user.dto.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
	private final RestTemplate rt;

	@Value("${backend.url}")
	private String baseUrl;

	public void create(UserRequestDTO user) {
		try {
			HttpEntity<UserRequestDTO> request = new HttpEntity<>(user, getHeaders(null));
			rt.postForObject(
					buildUrl("/users"),
					request,
					UserRequestDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public void updateMe(UserUpdateMeRequestDTO user, String token) {
		try {
			HttpEntity<UserUpdateMeRequestDTO> request = new HttpEntity<>(user, getHeaders(token));
			rt.exchange(
					buildUrl("/users/me"),
					HttpMethod.PUT,
					request,
					UserResponseDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public void update(UUID id, UserUpdateRequestDTO user, String token) {
		try {
			HttpEntity<UserUpdateRequestDTO> request = new HttpEntity<>(user, getHeaders(token));
			ResponseEntity<UserResponseDTO> response = rt.exchange(
					buildUrl("/users/" + id),
					HttpMethod.PUT,
					request,
					UserResponseDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public void delete(UUID id, String token) {
		try {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));
			rt.exchange(
					buildUrl("/users/" + id),
					HttpMethod.DELETE,
					request,
					Void.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public UserResponseDTO findById(UUID id, String token) {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));
			var response = rt.exchange(
					buildUrl("/users/" + id),
					HttpMethod.GET,
					request,
					UserResponseDTO.class
			);
			return response.getBody();
	}

	public UserResponseDTO profile(String token) {
			HttpEntity<String> request = new HttpEntity<>(getHeaders(token));
			var response = rt.exchange(
					buildUrl("/users/me"),
					HttpMethod.GET,
					request,
					UserResponseDTO.class
			);

			return response.getBody();
	}

	public List<UserResponseDTO> listUser(String token)  {
			HttpEntity<String> request = new HttpEntity<>(getHeaders(token));
			ParameterizedTypeReference<List<UserResponseDTO>> type = new ParameterizedTypeReference<List<UserResponseDTO>>() {};
			return rt.exchange(
					buildUrl("/users"),
					HttpMethod.GET,
					request,
					type
			).getBody();
	}

	public List<UserResponseDTO> listFilter(UserFilterDTO filter, String token) {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));
			ParameterizedTypeReference<List<UserResponseDTO>> type = new ParameterizedTypeReference<>() {};
			String url = buildUrlWithFilters("/users/filter", filter);
			return rt.exchange(
					url,
					HttpMethod.GET,
					request,
					type
			).getBody();
	}

	public List<UserResponseDTO> listTeachers(String token) {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));
			ParameterizedTypeReference<List<UserResponseDTO>> type = new ParameterizedTypeReference<>() {};
			return rt.exchange(
					buildUrl("/users/teachers"),
					HttpMethod.GET,
					request,
					type
			).getBody();
	}

	private HttpHeaders getHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (token != null && !token.isEmpty()) {
			headers.setBearerAuth(token);
		}

		return headers;
	}

	private String buildUrl(String path) {
		return UriComponentsBuilder.fromUriString(baseUrl).path(path).build().toUriString();
	}

	private String buildUrlWithFilters(String path, UserFilterDTO filter) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl).path(path);

		if (filter != null) {
			if(filter.name() != null && !filter.name().trim().isEmpty()) {
				builder.queryParam("name", filter.name().trim());
			}
			if(filter.role() != null) {
				builder.queryParam("role", filter.role());
			}
		}

		return builder.build().toUriString();
	}
}
