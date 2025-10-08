package ExpenseTracking.ExpenseTracking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ExpenseTracking.ExpenseTracking.entity.customUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class securityConfigClass {

  private final customUserDetailsService userDetailsService;
  private final RoleBasedAuthSuccessHandler successHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/","/register", "/login", "/css/**", "/js/**", "/register.html","/index.html").permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        // .loginPage("/login") // keep default login for simplicity
        .successHandler(successHandler) // redirects based on role
        .permitAll()
      )
      .logout(Customizer.withDefaults())
      .authenticationProvider(daoAuthProvider());
    return http.build();
  }
}
