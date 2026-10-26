package Medco.PremuimFinancing.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(securedEnabled = true)
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/sebna/users/signUp",
                                "/actuator/health",
                                "/api/sebna/users/signIn",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api/sebna/privilege/**",
                                "/api/sebna/role/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/sebna/users/password/sendResetCode/*",
                                "/api/sebna/users/resetPassword"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/sebna/users/password/checkResetCode"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/sebna/posts/landing",
                                "/api/sebna/posts/*",
                                "/api/sebna/post-comments/post/*",
                                "/api/sebna/post-comments/*",
                                "/api/sebna/comments/post/*",
                                "/api/sebna/likes/post/*",
                                "/api/sebna/likes/post/*/count",
                                "/api/sebna/likes/post/*/user-liked"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/sebna/likes/*/toggle",
                                "/api/sebna/comments/post/*"
                        ).permitAll()
                        .anyRequest().authenticated());
        http
                .headers(headers -> {
                    // Content Security Policy
                    headers.contentSecurityPolicy("default-src 'self'; script-src 'self'; style-src 'self';");

                    // Strict Transport Security (HSTS)
                    headers.httpStrictTransportSecurity(hsts -> hsts
                            .includeSubDomains(true)
                            .maxAgeInSeconds(31536000)
                    );



                    // X-Content-Type-Options
                    headers.contentTypeOptions();

                    // Frame options (clickjacking protection)
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
                });


        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
//        config.setAllowedOrigins(List.of("http://192.168.100.203:5173","http://localhost:5173","https://strap-venues-brings-requested.trycloudflare.com","http://strap-venues-brings-requested.trycloudflare.com"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);

    }

}

