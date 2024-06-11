package backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import backend.utils.JwtTokenProvider;

@Configuration
public class JwtConfig {
@Bean
public JwtTokenProvider jwtTokenProvider() {
	return new JwtTokenProvider();
}
}
