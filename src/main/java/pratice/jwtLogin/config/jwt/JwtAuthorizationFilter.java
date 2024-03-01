package pratice.jwtLogin.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pratice.jwtLogin.config.auth.PrincipalDetails;
import pratice.jwtLogin.model.User;
import pratice.jwtLogin.repository.UserRepository;

import java.io.IOException;

import static org.springframework.util.StringUtils.*;
import static pratice.jwtLogin.config.jwt.JwtProperties.*;

// 시큐리티가 가지고있는 BasicAuthenticationFilter는 권한이나 인증이 필요한 특정 주소를 요청 받았을 때, 해당 필터를 무조건 타게 되어있다.
// 물론 권한이나 인증이 필요한 주소가 아니라면 해당 필터를 타지않는다.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private UserRepository userRepository;
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증 or 권한이 필요한 요청");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader = " + jwtHeader);

        // header 확인
        if (!startsWithIgnoreCase(jwtHeader, TOKEN_PREFIX)){
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰 검증 -> 사용자 확인
        String token = delete(jwtHeader, TOKEN_PREFIX);

        String username = getStringFromPayload(token, "username");

        if(hasText(username)){
            User userEntity = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            //Jwt토큰의 서명이 정상이면 Authentication객체를 만들어주는 것
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails,
                    null, // 비밀번호가 null인 이유는 토큰으로 인증을 했기때문이다.
                    principalDetails.getAuthorities());

            // 시큐리티 세션에 접근하여 Authentication 객체를 강제로 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private static String getStringFromPayload(String token, String dataName) {
        return JWT.require(Algorithm.HMAC512(SECRET))
                .build().verify(token).getClaim(dataName).asString();
    }
}
