package usatec.com.br.curso_front.securityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import usatec.com.br.curso_front.exceptions.ApiAuthenticationException;
import usatec.com.br.curso_front.exceptions.ApiErrorResponse;
import usatec.com.br.curso_front.modules.auth.service.ExternalAuthenticationProvider;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final ExternalAuthenticationProvider authProvider;

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.authenticationProvider(authProvider)
				.build();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						// recursos estáticos - liberados
						.requestMatchers("/css/**", "/js/**", "/components/**", "/nav/**", "/images/**").permitAll()

						// rotas públicas de autenticação e cadastro
						.requestMatchers("/home").permitAll()
						.requestMatchers("/auth/**", "/login").permitAll()
						.requestMatchers("/user/create", "/user/create/**", "/user/success").permitAll()
						.requestMatchers("/course/list", "/course/list/**").permitAll()
						.requestMatchers("/category/list", "/category/list/**").permitAll()
						.requestMatchers("/course/active").permitAll()
						// GARANTA QUE A ROTA DE USER EXIJA AUTENTICAÇÃO
						.requestMatchers("/user/**").authenticated()

						// Qualquer outra requisição precisa estar logado
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/auth/login") // página de login
						.loginProcessingUrl("/auth/signIn")
						.defaultSuccessUrl("/home", false)
						.usernameParameter("email")
						.failureHandler((request, response, exception) -> {
							if (exception instanceof ApiAuthenticationException apiEx) {
								request.getSession().setAttribute("apiError", apiEx.getApiError());
							} else {
								request.getSession().setAttribute(
										"apiError",
										new ApiErrorResponse(
												"Authentication failed",
												Collections.emptyMap()
										)
								);
							}
							request.getSession().setAttribute(
									"loginEmail",
									request.getParameter("email")
							);
							response.sendRedirect("/auth/login");
						})
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/auth/logout") // URL para logout
						.logoutSuccessUrl("/auth/login?logout") // Redireciona para a página de login após logout
						.invalidateHttpSession(true) // Invalida a sessão
						.deleteCookies("JSESSIONID") // Remove o cookie de sessão
						.clearAuthentication(true)
						.permitAll()
				);

		return http.build();
	}
}
