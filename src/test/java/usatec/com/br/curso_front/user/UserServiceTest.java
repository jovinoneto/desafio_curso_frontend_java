package usatec.com.br.curso_front.user;

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
import usatec.com.br.curso_front.modules.user.dto.UserRequestDTO;
import usatec.com.br.curso_front.modules.user.dto.UserResponseDTO;
import usatec.com.br.curso_front.modules.user.dto.UserUpdateMeRequestDTO;
import usatec.com.br.curso_front.modules.user.dto.UserUpdateRequestDTO;
import usatec.com.br.curso_front.modules.user.service.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private RestTemplate rt;

	@InjectMocks
	private UserService userService;

	private String baseUrl;
	private UUID userId;
	private String token;

	@BeforeEach
	public void setUp() {
		baseUrl = "http://localhost:8080";
		userId = UUID.randomUUID();
		token = "test-token";

		ReflectionTestUtils.setField(userService, "baseUrl", baseUrl);
	}

	// ------------------------------- CREATE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_create_user")
	void should_be_able_to_create_user() {
		// Arrange
		UserRequestDTO request = createRequest("New user", "user@example.com");

		when(rt.postForObject(
				contains("/users"),
				any(HttpEntity.class),
				eq(UserRequestDTO.class)
		)).thenReturn(request);

		// Act
		userService.create(request);

		// Assert
		verify(rt).postForObject(
				contains("/users"),
				any(HttpEntity.class),
				eq(UserRequestDTO.class)
		);
	}

	@Test
	@DisplayName("should_throw_exception_when_email_already_exists")
	void shouldThrowExceptionWhenEmailAlreadyExists() {
		// Arrange
		UserRequestDTO request = createRequest("New user", "user@example.com");
		String errorBody = ApiErrorResponse.field("name", "Email already exists").toString();

		HttpClientErrorException e = HttpExceptionFactory.badRequest(errorBody);

		when(rt.postForObject(
				contains("/users"),
				any(HttpEntity.class),
				eq(UserRequestDTO.class)
		)).thenThrow(e);

		// Act & Assert
		assertThrows(BackIntegrationException.class, () -> userService.create(request));

		verify(rt).postForObject(
				contains("/users"),
				any(HttpEntity.class),
				eq(UserRequestDTO.class)
		);
	}

	// ------------------------------- UPDATE ME -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_update_current_user")
	public void shouldBeAbleToUpdateCurrentUser() {
		// Arrange
		UserUpdateMeRequestDTO request = new UserUpdateMeRequestDTO("Jane Doe", "jane@example.com");
		UserResponseDTO response = createResponse(userId, "Jane Doe", "jane@example.com", "STUDENT");

		ResponseEntity<UserResponseDTO> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

		when(rt.exchange(
				contains("/users/me"),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

		// Act
		userService.updateMe(request, token);

		// Assert
		HttpEntity<UserUpdateMeRequestDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/users/me", HttpMethod.PUT);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		verify(rt).exchange(
				contains("/users/me"),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_throw_exception_when_updating_current_user_with_invalid_token")
	public void shouldThrowExceptionWhenUpdatingCurrentUserWithInvalidToken() {
		// Arrange
		UserUpdateMeRequestDTO request = new UserUpdateMeRequestDTO("Jane Doe", "jane@example.com");
		String errorBody = ApiErrorResponse.global("Unauthorized").toString();

		HttpClientErrorException e = HttpExceptionFactory.unauthorized(errorBody);

		when(rt.exchange(
				contains("/users/me"),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> userService.updateMe(request, token));
	}

	// ------------------------------- UPDATE -----------------------------------------
	@Test
	@DisplayName("should_be_able_to_update_a_user")
	public void shouldBeAbleToUpdateAUser() {
		// Arrange
		UserUpdateRequestDTO request = new UserUpdateRequestDTO("Update User", "user@example.com", 5);
		UserResponseDTO response = createResponse(userId, "Jane Doe", "jane@example.com", "TEACHER");

		ResponseEntity<UserResponseDTO> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);


		when(rt.exchange(
				contains("/users/" + userId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		)).thenReturn(responseEntity);

		// Act
		userService.update(userId, request, token);

		// Assert
		HttpEntity<UserUpdateRequestDTO> entity = TestHeaderUtils.captureExchangeEntity(rt, "/users/" + userId, HttpMethod.PUT);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		verify(rt).exchange(
				contains("/users/" + userId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_throw_exception_when_user_not_found_on_update")
	public void shouldThrowExceptionWhenUserNotFoundOnUpdate() {
		// Arrange
		UserUpdateRequestDTO request = new UserUpdateRequestDTO("Update User", "user@example.com", 5);
		String errorBody = ApiErrorResponse.field("id", "user not found").toString();

		HttpClientErrorException e = HttpExceptionFactory.notFound(errorBody);

		when(rt.exchange(
				contains("/users/" + userId),
				eq(HttpMethod.PUT),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> userService.update(userId, request, token));
	}

	// --------------------------------- DELETE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_delete_a_user")
	public void shouldBeAbleToDeleteAUser() {
		// Arrange
		when(rt.exchange(
				contains("/users/" + userId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

		// Act
		userService.delete(userId, token);

		// Assert
		HttpEntity<Void> entity = TestHeaderUtils.captureExchangeEntity(rt, "/users/" + userId, HttpMethod.DELETE);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		verify(rt).exchange(
				contains("/users/" + userId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_throw_exception_when_user_not_found_on_delete")
	public void shouldThrowExceptionWhenUserNotFoundOnDelete() {
		// Arrange
		String errorBody = ApiErrorResponse.field("id", "user not found").toString();

		HttpClientErrorException e = HttpExceptionFactory.notFound(errorBody);

		when(rt.exchange(
				contains("/users/" + userId),
				eq(HttpMethod.DELETE),
				any(HttpEntity.class),
				eq(Void.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> userService.delete(userId, token));
	}

	// --------------------------------- PROFILE -----------------------------------------

	@Test
	@DisplayName("should_be_able_to_get_current_user_profile")
	public void shouldBeAbleToGetCurrentUserProfile() {
		// Arrange
		UserResponseDTO response = createResponse(userId, "User Name", "user@example.com", "STUDENT");

		when(rt.exchange(
				contains("/users/me"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

		// Act
		UserResponseDTO result = userService.profile(token);

		// Assert
		assertNotNull(result);
		assertEquals(userId, result.id());
		assertEquals("User Name", result.name());
		assertEquals("user@example.com", result.email());

		HttpEntity<String> entity = TestHeaderUtils.captureExchangeEntity(rt, "/users/me", HttpMethod.GET);
		TestHeaderUtils.assertAuthorizationHeader(entity, token);

		verify(rt).exchange(
				contains("/users/me"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		);
		verifyNoMoreInteractions(rt);
	}

	@Test
	@DisplayName("should_throw_exception_when_getting_profile_with_invalid_token")
	public void shouldThrowExceptionWhenGettingProfileWithInvalidToken() {
		// Arrange
		String errorBody = ApiErrorResponse.global("Unauthorized").toString();

		HttpClientErrorException e = HttpExceptionFactory.unauthorized(errorBody);

		when(rt.exchange(
				contains("/users/me"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(UserResponseDTO.class)
		)).thenThrow(e);

		// Act / Assert
		assertThrows(BackIntegrationException.class, () -> userService.profile(token));
	}

	// --------------------------------- HELPERS -----------------------------------------

	private UserRequestDTO createRequest(String name, String email) {
		return new UserRequestDTO(name, email, "password123", "password123");
	}

	private UserResponseDTO createResponse(UUID id, String name, String email, String role) {
		LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
		return new UserResponseDTO(id, name, email, role, now, now);
	}

}
