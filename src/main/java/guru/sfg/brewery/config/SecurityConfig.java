package guru.sfg.brewery.config;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // securedEnabled = true => method level security, prePostEnabled = true => @PreAuthorize("hasRole('ADMIN')")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    // Needed for use with Spring Data JPA SPeL
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        //return new LdapShaPasswordEncoder();
//        return new StandardPasswordEncoder();
        //return new BCryptPasswordEncoder();
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // new on Spring 5
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder(); // new on Spring 5
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
        .csrf().ignoringAntMatchers("/h2-console/**", "/api/**");

        http
        .authorizeRequests(authorize -> {
            authorize.antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll();
                    //.antMatchers(HttpMethod.GET, "/api/v1/beer/**").hasAnyRole("ADMIN", "CUSTOMER", "USER")
                    //.mvcMatchers(HttpMethod.DELETE, "/api/v1/beer/**").hasRole("ADMIN")
                    //.mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").hasAnyRole("ADMIN", "CUSTOMER", "USER")
//                    .mvcMatchers("/brewery/breweries").hasAnyRole("ADMIN", "CUSTOMER")
//                    .mvcMatchers(HttpMethod.GET, "/brewery/api/v1/breweries").hasAnyRole("ADMIN", "CUSTOMER")
//                    .mvcMatchers("/beers/find", "/beers/{beerId}").hasAnyRole("ADMIN", "CUSTOMER", "USER");
        })
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin(loginConfigurer -> {
            loginConfigurer
                    .loginProcessingUrl("/login")
                    .loginPage("/").permitAll()
                    .successForwardUrl("/")
                    .defaultSuccessUrl("/")
                    .failureUrl("/?error");
        })
        .logout(logoutConfigurer -> {
            logoutConfigurer
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/?logout")
                    .permitAll();
        })
        .httpBasic()
                .and()//.rememberMe().key("sfg-key").userDetailsService(userDetailsService);
                .rememberMe().tokenRepository(persistentTokenRepository).userDetailsService(userDetailsService);

        // h2 console config
        http.headers().frameOptions().sameOrigin();
    }

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("spring")
//                //.password("{noop}guru")
//                //.password("guru")
//                .password("{bcrypt}$2a$10$2I78PQi/u1Yk88mvJB3ukOoKaFi.DjE4AtRz2bCE8cHiS39Lop45S") // with PasswordEncoderFactories.createDelegatingPasswordEncoder()
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                //.password("{noop}password")
//                //.password("password")
//                //.password("{SSHA}P2onGgiVITqwP1o1xzSaXmfxpN5qUHGXP9JI5w==")
//                //.password("aee54173b1610c4b26d193b2eea2913465e06a8e672284536ec6c12c2c02405c1ac4fa55cba6bb8f")
//                //.password("$2a$10$ypOdJYHo7o6aXYmJyaVQFuzSI6u3nXoxxeTYwgurn0OAceFKzTssq")
//                .password("{sha256}9d6289b9846e5fb663e4745ab88d7e1e1a91ac5b9ad850eed8260b47da33340731c969f84a59b80a") //// with PasswordEncoderFactories.createDelegatingPasswordEncoder()
//                .roles("USER");
//
//        auth.inMemoryAuthentication()
//                //.withUser("scott").password("{noop}tiger").roles("CUSTOMER");
//        .withUser("scott").password("{bcrypt10}$2a$10$HFNEzs06yqzQ8JhC7FKtmuFaAQV2wLgR.z54W9hb5Jy.YYIUaaVuq").roles("CUSTOMER");
//    }
}
