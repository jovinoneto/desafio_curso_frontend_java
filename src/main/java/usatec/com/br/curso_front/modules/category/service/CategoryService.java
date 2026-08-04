package usatec.com.br.curso_front.modules.category.service;

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
import usatec.com.br.curso_front.modules.category.dto.CategoryRequestDTO;
import usatec.com.br.curso_front.modules.category.dto.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final RestTemplate rt;

	@Value("${backend.url}")
	private String baseUrl;

	public CategoryResponseDTO create(CategoryRequestDTO category, String token) {
		try {
			HttpEntity<CategoryRequestDTO> request = new HttpEntity<>(category, getHeaders(token));
			return rt.postForObject(
					buildUrl("/categories"),
					request,
					CategoryResponseDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public void update(UUID id, CategoryRequestDTO category, String token) {
		try {
			HttpEntity<CategoryRequestDTO> request = new HttpEntity<>(category, getHeaders(token));
			ResponseEntity<CategoryResponseDTO> response = rt.exchange(
					buildUrl("/categories/" + id),
					HttpMethod.PUT,
					request,
					CategoryResponseDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public void delete(UUID id, String token) {
		try {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));
			rt.exchange(
					buildUrl("/categories/" + id),
					HttpMethod.DELETE,
					request,
					Void.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public CategoryResponseDTO findById(UUID id) {
			return rt.getForObject(
					buildUrl( "/categories/" + id),
					CategoryResponseDTO.class
			);
	}

	public List<CategoryResponseDTO> listFilter(String name) {
			ParameterizedTypeReference<List<CategoryResponseDTO>> type = new ParameterizedTypeReference<>() {};
			String url = buildUrlWithFilters("/categories/search", name);

			return rt.exchange(
					url,
					HttpMethod.GET,
					null,
					type
			).getBody();
	}

	public List<CategoryResponseDTO> list() {
		HttpEntity<CategoryResponseDTO> request = new HttpEntity<>(getHeaders(null));
		ParameterizedTypeReference<List<CategoryResponseDTO>> type = new ParameterizedTypeReference<List<CategoryResponseDTO>>() {};

		return rt.exchange(
				buildUrl("/categories"),
				HttpMethod.GET,
				request,
				type).getBody();
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

	private String buildUrlWithFilters(String path, String filter) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl).path(path);

		if (filter != null && !filter.isEmpty()) {
			builder.queryParam("name", filter.trim());
		}

		return builder.build().toUriString();
	}
}
