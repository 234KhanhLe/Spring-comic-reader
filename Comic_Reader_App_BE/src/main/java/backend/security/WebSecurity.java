package backend.security;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import backend.utils.JwtTokenFilter;
import backend.utils.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	JwtTokenFilter jwtTokenFilter;

	private final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

	private final static String[] publicEndPoints = { "/api/v1/search/**" , "api/v1/chapter/**"};

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET","PUT","POST","DELETE","OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		http.csrf(csrf -> csrf.disable())
				.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(publicEndPoints).permitAll()
						.requestMatchers("/api/v1/auth/**").permitAll()
						.requestMatchers("api/v1/staff/**").hasAnyAuthority("ROLE_ROLE_STAFF")
						.requestMatchers("api/v1/detail/**").permitAll()
						.requestMatchers("api/v1/comic/createcomic").permitAll()
						.requestMatchers("api/v1/tag/add").hasAnyAuthority("ROLE_ROLE_STAFF", "ROLE_ROLE_PUBLISHER")
						.requestMatchers("api/v1/comic/tag/**").permitAll()
						.requestMatchers("api/v1/chapter/update").hasAnyAuthority("ROLE_ROLE_PUBLISHER")
						.requestMatchers("api/v1/chapter/delete").hasAnyAuthority("ROLE_ROLE_PUBLISHER", "ROLE_ROLE_STAFF")
						.requestMatchers("api/v1/comic/delete").hasAnyAuthority("ROLE_ROLE_PUBLISHER", "ROLE_ROLE_STAFF")
						.requestMatchers("api/v1/comic/update").hasAnyAuthority("ROLE_ROLE_PUBLISHER", "ROLE_ROLE_STAFF")
						.requestMatchers("api/v1/comic/addTag").hasAnyAuthority("ROLE_ROLE_PUBLISHER", "ROLE_ROLE_STAFF")
						.requestMatchers("api/v1/user/detail/{userId}").authenticated()
//						.requestMatchers("api/v1/user/profile").permitAll()
						.requestMatchers("api/v1/attachment/upload").hasAnyAuthority("ROLE_ROLE_PUBLISHER")
						.requestMatchers("api/v1/chapter/addchapter").hasAnyAuthority("ROLE_ROLE_PUBLISHER")
						.requestMatchers("api/v1/chapter/{comicId}-{id}").permitAll()
						.requestMatchers("api/v1/comic/delete").hasAnyAuthority("ROLE_ROLE_PUBLISHER", "ROLE_ROLE_STAFF")
						.requestMatchers("api/v1/comic/all").permitAll()
						.anyRequest().authenticated())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.logout(logout -> logout.logoutUrl("api/v1/auth/logout").logoutSuccessHandler(new LogoutSuccessHandler() {
			
			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
					throws IOException, ServletException {
				response.setStatus(HttpStatus.OK.value());
			}
		}).permitAll());
		http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
