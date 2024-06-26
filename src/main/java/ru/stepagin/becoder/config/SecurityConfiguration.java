package ru.stepagin.becoder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.stepagin.becoder.service.MyUserDetailsService;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    private final MyUserDetailsService userDetailService;
    @Value(value = "${api.endpoints.base-url}")
    private String baseUrl;

    public SecurityConfiguration(MyUserDetailsService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(baseUrl + "/api-docs").permitAll();
                    registry.requestMatchers(baseUrl + "/swagger-ui.html").permitAll();
                    registry.requestMatchers(baseUrl + "/swagger-ui/index.html").permitAll();
                    registry.requestMatchers(baseUrl + "/h2-console/**").permitAll();
                    registry.requestMatchers(baseUrl + "/auth/register").permitAll();
                    registry.requestMatchers("/auth/register").permitAll();
                    registry.requestMatchers("/auth/start").permitAll();
                    registry.requestMatchers("/auth/login").permitAll();
                    registry.requestMatchers("/favicon.ico").permitAll();

                    registry.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                .formLogin(formLogin ->
                        formLogin.loginPage("/auth/login").
                                loginProcessingUrl("/perform-login")
                                .usernameParameter("user")
                                .passwordParameter("pass")
                                .defaultSuccessUrl("/accounts").permitAll()

                )
                .build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
