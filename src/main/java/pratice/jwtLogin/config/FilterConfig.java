package pratice.jwtLogin.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pratice.jwtLogin.filter.LoginFilter;

//@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LoginFilter> filterRegistrationBean() {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>(new LoginFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(0); // 순서는 오름차순
        return bean;
    }
}
