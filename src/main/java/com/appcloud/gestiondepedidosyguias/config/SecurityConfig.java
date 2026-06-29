package com.appcloud.gestiondepedidosyguias.config;

import java.util.Collection;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/api/guias/*/download").hasRole("LECTOR")
						.requestMatchers("/api/guias/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

		return http.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(this::extractRolesFromClaims);
		return converter;
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return token -> {
			try {
				var parsedJwt = JWTParser.parse(token);
				JWTClaimsSet claimsSet = parsedJwt.getJWTClaimsSet();
				Map<String, Object> headers = parsedJwt.getHeader().toJSONObject();
				Map<String, Object> claims = claimsSet.getClaims();

				Instant issuedAt = claimsSet.getIssueTime() != null ? claimsSet.getIssueTime().toInstant() : Instant.now();
				Instant expiresAt = claimsSet.getExpirationTime() != null
						? claimsSet.getExpirationTime().toInstant()
						: issuedAt.plusSeconds(3600);

				return Jwt.withTokenValue(token)
						.headers(headerMap -> headerMap.putAll(headers))
						.claims(claimMap -> claimMap.putAll(claims))
						.subject(claimsSet.getSubject() != null ? claimsSet.getSubject() : "local-user")
						.issuedAt(issuedAt)
						.expiresAt(expiresAt)
						.build();
			} catch (Exception ex) {
				throw new JwtException("No se pudo decodificar el token JWT", ex);
			}
		};
	}

	private Collection<GrantedAuthority> extractRolesFromClaims(Jwt jwt) {
		Object rolesClaim = jwt.getClaims().get("roles");
		if (rolesClaim instanceof Collection<?> collection) {
			return collection.stream()
					.filter(String.class::isInstance)
					.map(String.class::cast)
					.map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		}
		if (rolesClaim instanceof String role) {
			return List.of(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role));
		}

		Object groupsClaim = jwt.getClaims().get("groups");
		if (groupsClaim instanceof Collection<?> collection) {
			return collection.stream()
					.filter(String.class::isInstance)
					.map(String.class::cast)
					.map(group -> group.startsWith("ROLE_") ? group : "ROLE_" + group)
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		}
		if (groupsClaim instanceof String group) {
			return List.of(new SimpleGrantedAuthority(group.startsWith("ROLE_") ? group : "ROLE_" + group));
		}

		return List.of();
	}
}
