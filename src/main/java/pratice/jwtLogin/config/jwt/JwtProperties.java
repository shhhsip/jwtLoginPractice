package pratice.jwtLogin.config.jwt;

public interface JwtProperties {

    String TOKEN_SUBJECT = "com.test.shhhsip";
    String SECRET = "shhhsip";
    int EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
