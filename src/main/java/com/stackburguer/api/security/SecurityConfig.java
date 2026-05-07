package com.stackburguer.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Value("${url.front.end}")
    private String urlFrontEnd;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens permitidas (Seu Front-end)
        configuration.setAllowedOrigins(Arrays.asList(urlFrontEnd));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/orders/webhook"))
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        //TUDO QUE É TOTALMENTE LIBERADO (Qualquer método)
                        .requestMatchers(
                                "/auth/**", "/sessions/**", "/user", "/orders/webhook",
                                "/product-file/**", "/category-file/**", "/files/**",
                                "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()

                        //LEITURA LIBERADA (Apenas GET)
                        //Aqui liberamos a visualização para clientes, mas o POST/PUT continua bloqueado
                        .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()

                        //CASOS ESPECÍFICOS
                        .requestMatchers(HttpMethod.POST, "/create-payment-intent").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/orders/*/status").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        //TUDO QUE SOBROU NESSAS ROTAS É SÓ PARA ADMIN
                        //Como o GET já foi liberado acima, o que sobrar aqui (POST, PUT, DELETE) vira ADMIN
                        .requestMatchers("/products/**", "/categories/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/orders/**").hasRole("ADMIN")

                        .anyRequest().authenticated()  //O Resto precisa estar logado
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }



}
