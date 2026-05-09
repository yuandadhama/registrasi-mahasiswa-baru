package com.example.registrasi.nim0420230007.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Konfigurasi Spring Security untuk autentikasi admin.
 * - Halaman publik (/, /daftar, /sukses, /captcha/**) dapat diakses tanpa
 * login.
 * - Halaman /admin/** hanya dapat diakses oleh user dengan role ADMIN.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						// Izinkan akses publik ke halaman pendaftaran dan static resources
						.requestMatchers("/", "/daftar", "/sukses", "/captcha/**").permitAll()
						.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
						// Halaman admin hanya untuk role ADMIN
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin(form -> form
						// Konfigurasi halaman login custom
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/admin/dashboard", true)
						.failureUrl("/login?error=true")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.permitAll())
				// Nonaktifkan CSRF untuk kemudahan pengembangan (aktifkan di production)
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/captcha/**"));

		return http.build();
	}

	/**
	 * Konfigurasi user admin in-memory.
	 * Untuk production, gunakan database-based authentication.
	 *
	 * Default credentials: admin / admin123
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails admin = User.builder()
				.username("admin")
				.password(passwordEncoder().encode("admin123"))
				.roles("ADMIN")
				.build();

		return new InMemoryUserDetailsManager(admin);
	}

	/**
	 * Password encoder menggunakan BCrypt.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
