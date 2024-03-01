package pratice.jwtLogin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import pratice.jwtLogin.config.jwt.JwtAuthenticationFilter;
import pratice.jwtLogin.config.jwt.JwtAuthorizationFilter;
import pratice.jwtLogin.repository.UserRepository;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터를 스프링 필터 체인에등록
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;

    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;



    @Bean

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        sharedObject.userDetailsService(this.userDetailsService);
        AuthenticationManager authenticationManager = sharedObject.build();

        http.authenticationManager(authenticationManager);

        http.csrf(CsrfConfigurer::disable);

        //		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.sessionManagement((sessionManagement) ->
                sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //http.formLogin().disable();
        http.formLogin(AbstractHttpConfigurer::disable);

        //http.httpBasic().disable();
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.addFilter(corsConfig.corsFilter()); // @CrossOrigin -> 인증 없을때 사용, 인증이 있을 땐 필터를 만들어 추가

        // formLogin을 위에서 사용하지 않게 지정했기 때문에, 직접 필터 추가
        http.addFilter(new JwtAuthenticationFilter(authenticationManager))
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository)); // AuthenticationManager 필요


        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll());

        return http.build();
    }
}
