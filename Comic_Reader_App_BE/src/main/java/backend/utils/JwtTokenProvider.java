package backend.utils;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import backend.model.Role;
import backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtTokenProvider {

	private static final long TOKEN_EXPIRATION = 3600000;
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("Id", user.getId());
		claims.put("role", user.getRole().getName());

		return Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
				.signWith(SECRET_KEY).compact();
	}

	public Claims verifyToken(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JWT token");
		}
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).deserializeJsonWith(new JacksonDeserializer<>())
				.build().parseClaimsJws(token).getBody();
		return claims.get("Id", Long.class);
	}

	public String getUserRoleFromToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).deserializeJsonWith(new JacksonDeserializer<>())
				.build().parseClaimsJws(token).getBody();
		return claims.get("role", String.class);
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(String.valueOf(getUserIdFromToken(token)), "",
				getAuthorities(getUserRoleFromToken(token)));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Collection<? extends GrantedAuthority> getAuthorities(String role) {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
	}

}
