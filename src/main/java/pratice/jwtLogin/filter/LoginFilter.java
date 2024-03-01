package pratice.jwtLogin.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        // 정상 로그인이 들어오면 토근을 만들어 응답해줌
        // 요청마다 header에 Authrozation에 value값으로 토큰을 가지고온 후에, 서버에서 만든 토큰이 맞는지 검증
        if (req.getMethod().equalsIgnoreCase("post")) {
            String headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth = " + headerAuth);

            if (headerAuth.equals("test")) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                outError(res);
            }
        }
    }

    static void outError(HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        out.println("인증 실패");
    }

}
