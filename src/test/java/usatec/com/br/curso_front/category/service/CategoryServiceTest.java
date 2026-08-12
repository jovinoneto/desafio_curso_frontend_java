package usatec.com.br.curso_front.category.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import usatec.com.br.curso_front.UtilsTest.HttpExceptionFactory;
import usatec.com.br.curso_front.UtilsTest.TestHeaderUtils;
import usatec.com.br.curso_front.exceptions.ApiErrorResponse;
import usatec.com.br.curso_front.exceptions.BackIntegrationException;
import usatec.com.br.curso_front.modules.category.dto.CategoryRequestDTO;
import usatec.com.br.curso_front.modules.category.dto.CategoryResponseDTO;
import usatec.com.br.curso_front.modules.category.service.CategoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

	@Mock
	private RestTemplate rt;

	@InjectMocks
	private CategoryService categoryService;

	private String baseUrl;
	private UUID categoryId;
	private String token;

	@BeforeEach
	public void setUp() {
		baseUrl = "http://localhost:8080";
		categoryId = UUID.randomUUID();
		token = "test-token";

		ReflectionTestUtils.setField(categoryService, "baseUrl", baseUrl);
	}

// ------------------------------- CREATE -----------------------------------------

	@Test
	@DisplayName("should_send_authorization_header_when_creating_category")
	public void shouldSendAuthorizationHeaderWhenCreatingCategory() {
		// Arrange
		CategoryRequestDTO request = createRequest("Java");
		CategoryResponseDTO response = createResponse(categoryId,"Java");

		when(rt.postForObject(
				contains("/categories"),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		)).thenReturn(response);

		// Acc
		categoryService.create(request, token);

		// Assert
		HttpEntity<CategoryRequestDTO> entity = TestHeaderUtils.capturePostEntity(rt, "/categories");
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		// Validação opcional do body retido no HttpEntity
		assertEquals("Java", entity.getBody().name());
	}

	@Test
	@DisplayName("should_be_able_to_create_a_category")
	public void shouldBeAbleToCreateACategory() {
		// Arrange
		CategoryRequestDTO request = createRequest("Java");
		CategoryResponseDTO response = createResponse(categoryId, "Java");

		when(rt.postForObject(
				contains("/categories"),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		)).thenReturn(response);

		// Act
		CategoryResponseDTO result = categoryService.create(request, token);

		// Assert
		assertNotNull(result);
		assertEquals("Java", result.name());
		assertEquals(categoryId, result.id());
		verify(rt).postForObject(
				contains("/categories"),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_throw_exception_when_name_already_exists")
	void shouldThrowExceptionWhenNameAlreadyExists() {
		CategoryRequestDTO request = createRequest("Java");
		String errorBody = ApiErrorResponse.field("name", "name already exists").toString();

		HttpClientErrorException e = HttpExceptionFactory.badRequest(errorBody);

		when(rt.postForObject(
				contains("/categories"),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		)).thenThrow(e);

		assertThrows(BackIntegrationException.class, () -> categoryService.create(request, token));

		verify(rt).postForObject(
				contains("/categories"),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		);
	}

// ------------------------------- UPDATE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_update_a_category")
	public void shouldBeAbleToUpdateACategory() {
		CategoryRequestDTO request = createRequest("Java");
		CategoryResponseDTO response = createResponse(categoryId,"Java");

		ResponseEntity<CategoryResponseDTO> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

		when(rt.exchange(
				contains("/categories/" + categoryId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		)).thenReturn(responseEntity);

		categoryService.update(categoryId, request, token);

		HttpEntity<CategoryRequestDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/categories/" + categoryId, HttpMethod.PUT);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		assertNotNull(entity.getBody());
		assertEquals("Java", entity.getBody().name());
	}

	@Test
	@DisplayName("should_throw_exception_when_category_not_found_on_update")
	void shouldThrowExceptionWhenCategoryNotFoundOnUpdate() {
		CategoryRequestDTO request = createRequest("Web Updated");
		String errorBody = ApiErrorResponse.field("id", "category not found").toString();

		HttpClientErrorException e = HttpExceptionFactory.notFound(errorBody);

		when(rt.exchange(
				contains("/categories/" + categoryId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		)).thenThrow(e);

		assertThrows(BackIntegrationException.class, () -> categoryService.update(categoryId, request, token));

		HttpEntity<CategoryRequestDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/categories/" + categoryId, HttpMethod.PUT);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		assertNotNull(entity.getBody());
		assertEquals("Web Updated", entity.getBody().name());
	}

	@Test
	@DisplayName("should_throw_exception_when_duplicate_name_on_update")
	void shouldThrowExceptionWhenDuplicateNameOnUpdate() {
		CategoryRequestDTO request = createRequest("Java");
		String errorBody = ApiErrorResponse.field("name", "name already exists").toString();

		HttpClientErrorException e = HttpExceptionFactory.badRequest(errorBody);

		when(rt.exchange(
				contains("/categories/" + categoryId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(CategoryResponseDTO.class)
		)).thenThrow(e);

		assertThrows(BackIntegrationException.class, () -> categoryService.update(categoryId, request, token));

		HttpEntity<CategoryRequestDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/categories/" + categoryId, HttpMethod.PUT);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		assertNotNull(entity.getBody());
		assertEquals("Java", entity.getBody().name());
	}

// ------------------------------- DELETE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_delete_a_category")
	public void shouldBeAbleToDeleteACategory() {
		ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

		when(rt.exchange(
				contains("/categories/" + categoryId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenReturn(responseEntity);

		categoryService.delete(categoryId, token);

		HttpEntity<Void> entity = TestHeaderUtils.captureExchangeEntity(rt, "/categories/" + categoryId, HttpMethod.DELETE);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);
	}

	@Test
	@DisplayName("should_throw_exception_when_category_not_found_on_delete")
	void shouldThrowExceptionWhenCategoryNotFoundOnDelete() {
		String errorBody = ApiErrorResponse.field("id", "category not found").toString();

		HttpClientErrorException e = HttpExceptionFactory.notFound(errorBody);

		when(rt.exchange(
				contains("/categories/" + categoryId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenThrow(e);

		assertThrows(BackIntegrationException.class, () -> categoryService.delete(categoryId, token));

		HttpEntity<Void> entity = TestHeaderUtils.captureExchangeEntity(rt, "/categories/" + categoryId, HttpMethod.DELETE);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);
	}

// ------------------------------- FIND BY ID -----------------------------------------

	@Test
	@DisplayName("should_find_category_by_id_when_exists")
	void shouldFindCategoryByIdWhenExists() {
		CategoryResponseDTO response = createResponse(categoryId,"Java");

		when(rt.getForObject(
				contains("/categories/" + categoryId),
				eq(CategoryResponseDTO.class)
		)).thenReturn(response);

		CategoryResponseDTO result = categoryService.findById(categoryId);

		assertNotNull(result);
		assertEquals("Java", result.name());
		assertEquals(categoryId, result.id());
		verify(rt).getForObject(
				contains("/categories/" + categoryId),
				eq(CategoryResponseDTO.class)
		);
		verifyNoMoreInteractions(rt);
	}

// ------------------------------- LIST ALL AND FILTER -----------------------------------------

	@Test
	@DisplayName("should_list_all_categories")
	void shouldListAllCategories() {
		// Arrange
		List<CategoryResponseDTO> categories = List.of(
				createResponse(UUID.randomUUID(), "Java"),
				createResponse(UUID.randomUUID(), "Python")
		);

		ResponseEntity<List<CategoryResponseDTO>> responseEntity = new ResponseEntity<>(categories, HttpStatus.OK);

		when(rt.exchange(
				contains("/categories"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				any(ParameterizedTypeReference.class)
		)).thenReturn(responseEntity);

		// Act
		List<CategoryResponseDTO> result = categoryService.list();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(rt).exchange(
				contains("/categories"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				any(ParameterizedTypeReference.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_list_all_categories_by_name")
	void shouldListAllCategoriesByName() {
		// Arrange
		String name = "Java";
		List<CategoryResponseDTO> categories = List.of(
				createResponse(UUID.randomUUID(), "Java")
		);

		when(rt.exchange(
				contains("/categories/search?name=" + name),
				eq(HttpMethod.GET),
				isNull(),
				any(ParameterizedTypeReference.class)
		)).thenReturn(ResponseEntity.ok(categories));

		//Act
		List<CategoryResponseDTO> result = categoryService.listFilter(name);

		//Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Java", result.get(0).name());
		verify(rt).exchange(
				contains("/categories/search"),
				eq(HttpMethod.GET),
				isNull(),
				any(ParameterizedTypeReference.class)
		);
		verifyNoMoreInteractions(rt);
	}

	// ------------------------------- HELPERS -----------------------------------------

	private CategoryRequestDTO createRequest(String name) {
		return new CategoryRequestDTO(name);
	}

	private CategoryResponseDTO createResponse(UUID id, String name) {
		LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
		return new CategoryResponseDTO(id, name, now, now);
	}
}
