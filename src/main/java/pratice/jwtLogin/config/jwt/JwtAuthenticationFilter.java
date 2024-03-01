package pratice.jwtLogin.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pratice.jwtLogin.config.auth.PrincipalDetails;
import pratice.jwtLogin.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import static pratice.jwtLogin.config.jwt.JwtProperties.*;

// login 요청이 들어오면 해당 필터 동작
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;



    //Login 요청을 받으면 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter.attemptAuthentication 로그인 시도");

        // 권한 관리가 필요한 경우에는 PrincipalDetails를 세션에 담아야 한다.
        ObjectMapper om = new ObjectMapper();

        try {
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println("user = " + user);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            System.out.println("JwtAuthenticationFilter : 토큰생성완료" + authenticationToken.toString());


            // PrincipalDetailsService의 loadUserByUsername() 호출
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Object에 정보가 담겨옴
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            System.out.println("principalDetails = " + principalDetails.getUser().getUsername());

            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //return super.attemptAuthentication(request, response);
    }

    // attemptAuthentication를 통해 인증이 정상적으로 완료되면 successfulAuthentication가 실행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("JwtAuthenticationFilter.successfulAuthentication : 인증 완료");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = TOKEN_PREFIX + JWT.create()
                .withSubject(TOKEN_SUBJECT) // 토큰의 이름
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(SECRET));

        response.addHeader(HEADER_STRING, jwtToken);

        //super.successfulAuthentication(request, response, chain, authResult);
    }
}
