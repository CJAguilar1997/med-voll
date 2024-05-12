package med.voll.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)//<- Esta anotacion me permite hacer uso de la anotación @Secured en mis controllers o metodos de los mismos, -> 
public class SecurityConfiguration {        //-> si esta anotacion no se coloca en esta clase, la anotación no se podra usar ya que viene deshabilitada por defecto. Para hacer uso de los roles, -> 
                                            //-> se podría aplicar una nueva tabla en la base de datos llamado "rol" en los usuarios, los cuales determinaría el nivel de autorización con la que cuentan.

    @Autowired
    private SecurityFilter securityFilter;

    @Bean //Esta es otra forma para colocar el SecurityFilterChain
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()
                .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // @Bean //Esta es otra forma para colocar el SecurityFilterChain
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     return http.csrf().disable()
    //             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    //             .and().authorizeHttpRequests()
    //             .requestMatchers(HttpMethod.POST, "/login").permitAll()
    //             .requestMatchers(HttpMethod.DELETE, "/medico").hasRole("ADMIN")
    //             .requestMatchers(HttpMethod.DELETE, "/pacientes").hasRole("ADMIN")
    //             .anyRequest().authenticated()
    //             .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
    //             .build();
    // }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder PasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
