package nextstep;

import auth.*;
import nextstep.member.Member;
import nextstep.member.MemberDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final MemberDao memberDao;

    public WebMvcConfiguration(MemberDao memberDao){
        this.memberDao = memberDao;
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(){
        return new JwtTokenProvider();
    }

    @Bean
    public LoginService loginService(){
        return new LoginService(
                (username, password)->{
                    Member member = memberDao.findByUsername(username);
                    if(member.checkWrongPassword(password)){
                        return null;
                    }
                    return new TokenMember(
                            member.getId(),
                            member.getRole()
                    );
                },
                (id)->{
                    Member member = memberDao.findById(id);
                    return new TokenMember(
                            member.getId(),
                            member.getRole()
                    );
                },
                jwtTokenProvider()
        );
    }

    @Bean
    public LoginController loginController(){
        return new LoginController(loginService());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor(jwtTokenProvider())).addPathPatterns("/admin/**");
    }

    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(new LoginMemberArgumentResolver(loginService()));
    }
}
