package usatec.com.br.curso_front.modules.course.service;

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
import usatec.com.br.curso_front.modules.course.dto.CourseFilterDTO;
import usatec.com.br.curso_front.modules.course.dto.CourseRequestDTO;
import usatec.com.br.curso_front.modules.course.dto.CourseResponseDTO;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
	private final RestTemplate rt;

	@Value("${backend.url}")
	private String baseUrl;

	public CourseResponseDTO create(CourseRequestDTO course, String token) {
		try {
			HttpEntity<CourseRequestDTO> request = new HttpEntity<>(course, getHeaders(token));
			return rt.postForObject(
					buildUrl( "/courses"),
					request,
					CourseResponseDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public void update(UUID id, CourseRequestDTO course, String token) {
		try {
			HttpEntity<CourseRequestDTO> request = new HttpEntity<>(course, getHeaders(token));
			rt.exchange(
					buildUrl("/courses/" + id),
					HttpMethod.PUT,
					request,
					CourseResponseDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public void delete(UUID id, String token) {
		try {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));
			rt.exchange(
					buildUrl("/courses/" + id),
					HttpMethod.DELETE,
					request,
					Void.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public CourseResponseDTO findById(UUID id) {
		try {
			return rt.getForObject(
					buildUrl( "/courses/" + id),
					CourseResponseDTO.class
			);
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public CourseResponseDTO toggle(UUID id, String token) {
		try {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));

			return rt.exchange(
					buildUrl("/courses/"+id+"/active"),
					HttpMethod.PATCH,
					request,
					CourseResponseDTO.class
			).getBody();
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public List<CourseResponseDTO> list() {
		try {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(null));
			ParameterizedTypeReference<List<CourseResponseDTO>> type = new ParameterizedTypeReference<>() {};
			return rt.exchange(
					buildUrl("/courses"),
					HttpMethod.GET,
					request,
					type
			).getBody();
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
	}

	public List<CourseResponseDTO> listFilter(CourseFilterDTO filter,  String token) {
		try {
			HttpEntity<Void> request = new HttpEntity<>(getHeaders(token));
			ParameterizedTypeReference<List<CourseResponseDTO>> type = new ParameterizedTypeReference<>() {};
			String url = buildUrlWithFilters(filter);

			return rt.exchange(url, HttpMethod.GET, request, type).getBody();
		} catch (RestClientResponseException e) {
			throw new BackIntegrationException(ApiErrorParser.parse(e));
		}
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

	private String buildUrlWithFilters(CourseFilterDTO filter) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl).path("/courses/list");

		if (filter != null) {
			if(filter.name() != null && !filter.name().isBlank()) {
				builder.queryParam("name", filter.name().trim());
			}
			if(filter.active() != null) {
				builder.queryParam("active", filter.active());
			}
		}

		return builder.build().toUriString();
	}
}
