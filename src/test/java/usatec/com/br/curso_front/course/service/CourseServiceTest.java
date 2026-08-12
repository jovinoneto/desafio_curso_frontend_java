package usatec.com.br.curso_front.course.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import usatec.com.br.curso_front.modules.course.dto.CourseRequestDTO;
import usatec.com.br.curso_front.modules.course.dto.CourseResponseDTO;
import usatec.com.br.curso_front.modules.course.service.CourseService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

	@Mock
	private RestTemplate rt;

	@InjectMocks
	private CourseService courseService;

	private String baseUrl;
	private UUID courseId;
	private UUID categoryId;
	private UUID userId;
	private String token;

	@BeforeEach
	public void setUp() {
		baseUrl = "http://localhost:8080";
		courseId = UUID.randomUUID();
		categoryId = UUID.randomUUID();
		userId = UUID.randomUUID();
		token = "test-token";

		ReflectionTestUtils.setField(courseService, "baseUrl", baseUrl);
	}

	// ------------------------------- CREATE -----------------------------------------

	@Test
	@DisplayName("should_send_authorization_header_when_creating_course")
	public void shouldSendAuthorizationHeaderWhenCreatingCourse() {
		// Arrange
		CourseRequestDTO request = createRequest("Java");
		CourseResponseDTO response = createResponse(courseId, "Java");

		when(rt.postForObject(
				contains("/courses"),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		)).thenReturn(response);

		// Act
		courseService.create(request, token);

		// Assert
		HttpEntity<CourseRequestDTO> entity = TestHeaderUtils.capturePostEntity(rt, "/courses");
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		assertEquals("Java", entity.getBody().name());
		assertEquals(categoryId, entity.getBody().categoryId());
		assertEquals(userId, entity.getBody().userId());
	}

	@Test
	@DisplayName("should_be_able_to_create_a_course")
	public void shouldBeAbleToCreateACourse() {
		// Arrange
		CourseRequestDTO request = createRequest("Java");
		CourseResponseDTO response = createResponse(courseId, "Java");

		when(rt.postForObject(
				contains("/courses"),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		)).thenReturn(response);

		// Act
		CourseResponseDTO result = courseService.create(request, token);

		// Assert
		assertNotNull(result);
		assertEquals("Java", result.name());
		assertEquals(courseId, result.id());

		verify(rt).postForObject(
				contains("/courses"),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_throw_exception_when_name_already_exists")
	void shouldThrowExceptionWhenNameAlreadyExists() {
		// Arrange
		CourseRequestDTO request = createRequest("Java");
		String errorBody = ApiErrorResponse.field("name", "name already exists").toString();

		HttpClientErrorException e = HttpExceptionFactory.badRequest(errorBody);

		when(rt.postForObject(
				contains("/courses"),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> courseService.create(request, token));

		verify(rt).postForObject(
				contains("/courses"),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		);
	}

	// ------------------------------- UPDATE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_update_a_course")
	public void shouldBeAbleToUpdateACourse() {
		// Arrange
		CourseRequestDTO request = createRequest("Java Updated");
		CourseResponseDTO response = createResponse(courseId, "Java Updated");

		ResponseEntity<CourseResponseDTO> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

		when(rt.exchange(
				contains("/courses/" + courseId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		)).thenReturn(responseEntity);

		// Act
		courseService.update(courseId, request, token);

		// Assert
		HttpEntity<CourseRequestDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/courses/" + courseId, HttpMethod.PUT);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		assertNotNull(entity.getBody());
		assertEquals("Java Updated", entity.getBody().name());
		assertEquals(categoryId, entity.getBody().categoryId());
		assertEquals(userId, entity.getBody().userId());

		verify(rt).exchange(
				contains("/courses/" + courseId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_throw_exception_when_course_not_found_on_update")
	void shouldThrowExceptionWhenCourseNotFoundOnUpdate() {
		// Arrange
		CourseRequestDTO request = createRequest("Java Updated");
		String errorBody = ApiErrorResponse.field("id", "course not found").toString();

		HttpClientErrorException e = HttpExceptionFactory.notFound(errorBody);

		when(rt.exchange(
				contains("/courses/" + courseId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> courseService.update(courseId, request, token));

		HttpEntity<CourseRequestDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/courses/" + courseId, org.springframework.http.HttpMethod.PUT);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		assertEquals("Java Updated", entity.getBody().name());
		assertEquals(categoryId, entity.getBody().categoryId());
		assertEquals(userId, entity.getBody().userId());
	}

	// ------------------------------- DELETE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_delete_a_course")
	public void shouldBeAbleToDeleteACourse() {
		// Arrange
		ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

		when(rt.exchange(
				contains("/courses/" + courseId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenReturn(responseEntity);

		// Act
		courseService.delete(courseId, token);

		// Assert
		HttpEntity<Void> entity = TestHeaderUtils.captureExchangeEntity(rt, "/courses/" + courseId, HttpMethod.DELETE);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);
	}

	@Test
	@DisplayName("should_throw_exception_when_course_not_found_on_delete")
	void shouldThrowExceptionWhenCourseNotFoundOnDelete() {
		// Arrange
		String errorBody = ApiErrorResponse.field("id", "course not found").toString();

		HttpClientErrorException e = HttpExceptionFactory.notFound(errorBody);

		when(rt.exchange(
				contains("/courses/" + courseId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> courseService.delete(courseId, token));

		HttpEntity<Void> entity = TestHeaderUtils.captureExchangeEntity(rt, "/courses/" + courseId, HttpMethod.DELETE);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);
	}

	@Test
	@DisplayName("should_throw_exception_when_course_in_use_on_delete")
	void shouldThrowExceptionWhenCourseInUseOnDelete() {
		// Arrange
		String errorBody = ApiErrorResponse.global("Course in use").toString();

		HttpClientErrorException e = HttpExceptionFactory.conflict(errorBody);

		when(rt.exchange(
				contains("/courses/" + courseId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> courseService.delete(courseId, token));

		HttpEntity<Void> entity = TestHeaderUtils.captureExchangeEntity(rt, "/courses/" + courseId, HttpMethod.DELETE);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);
	}


	// ------------------------------- TOGGLE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_toggle_a_course")
	void shouldBeAbleToToggleACourse() {
		// Arrange
		LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
		CourseResponseDTO response = new CourseResponseDTO(courseId, "Java", false, now, now, null, null);

		when(rt.exchange(
				contains("/courses/" + courseId + "/active"),
				eq(HttpMethod.PATCH),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

		// Act
		CourseResponseDTO result = courseService.toggle(courseId, token);

		// Assert
		assertNotNull(result);
		assertFalse(result.active());

		HttpEntity<Void> entity = TestHeaderUtils.captureExchangeEntity(rt, "/courses/" + courseId + "/active", HttpMethod.PATCH);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);
	}

	@Test
	@DisplayName("should_throw_exception_when_toggle_fails")
	void shouldThrowExceptionWhenToggleFails() {
		// Arrange
		String errorBody = ApiErrorResponse.global("Toggle error").toString();

		HttpClientErrorException e = HttpExceptionFactory.conflict(errorBody);

		when(rt.exchange(
				contains("/courses/" + courseId + "/active"),
				eq(HttpMethod.PATCH),
				any(HttpEntity.class),
				eq(CourseResponseDTO.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> courseService.toggle(courseId, token));

		HttpEntity<CourseResponseDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/courses/" + courseId + "/active", HttpMethod.PATCH);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);
	}

	// ------------------------------- Helpers -----------------------------------------

	private CourseRequestDTO createRequest(String name) {
		return createRequest(name, categoryId, userId);
	}

	private CourseRequestDTO createRequest(String name, UUID catId, UUID uId) {
		return new CourseRequestDTO(name, catId, uId);
	}

	private CourseResponseDTO createResponse(UUID id, String name) {
		LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
		return new CourseResponseDTO(id, name, false, now, now, null, null);
	}
}
