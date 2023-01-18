package com.babydocs.security;

import com.babydocs.constants.RoleBuilder;
import com.babydocs.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static java.util.Arrays.asList;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    private static final String ROLE_ADMIN = RoleBuilder.getRoleAdmin().getRole();
    private static final String ROLE_REGISTERED_USER = RoleBuilder.getSiteUserR().getRole();
    private static final String ROLE_SUPER_ADMIN = RoleBuilder.getSuperAdmin().getRole();
    private static final String ROLE_ROLE_USER = RoleBuilder.getSiteUser().getRole();

    private final UserPrincipalDetailService userPrincipalDetailService;
    private final UserRepository userRepository;

    public SecurityConfig(UserPrincipalDetailService userPrincipalDetailService,
        UserRepository userRepository)
    {
        this.userPrincipalDetailService = userPrincipalDetailService;
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
    {
        auth.authenticationProvider(authenticationProvider());

    }

    @Override
    public void configure(HttpSecurity http) throws Exception
    {

        http
            .csrf()
            .disable()
            .cors()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtAuthenticationFilter(authenticationManager()))
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), this.userRepository))
            .authorizeRequests()
            // configure access role
            .antMatchers("/swagger-ui/**","/login").permitAll()
            .antMatchers("/api/v1/public/**").permitAll()
            .antMatchers("/api/v1/secured/**").hasAnyRole(ROLE_REGISTERED_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN)
            .antMatchers("/api/admin/**").hasAnyRole(ROLE_ADMIN, ROLE_SUPER_ADMIN)
            .antMatchers("/api/superadmin/**").hasRole(ROLE_SUPER_ADMIN)
            .and()
            .httpBasic();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.userPrincipalDetailService);
        return daoAuthenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception
    {
        return super.authenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(asList("*"));
        configuration.setAllowedHeaders(asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
                                               "Access-Control-Request-Method", "Access-Control-Request-Headers",
                                               "Origin", "Cache-Control", "Content-Type", "Authorization", "FullName"));
        configuration.setExposedHeaders(asList("Access-Control-Allow-Headers",
                                               "Authorization", "FullName",
                                               "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With",
                                               "Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"));

        configuration.setAllowedMethods(asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
