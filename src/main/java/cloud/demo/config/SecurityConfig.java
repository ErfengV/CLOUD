package cloud.demo.config;

import cloud.demo.filter.ValidCodeFilter;
import cloud.demo.hander.MyAuthenticationFailureHandler;
import cloud.demo.service.impl.UserServiceImpl;
import cloud.demo.util.RedisUtil;
import com.ac.comehome.entity.Result;
import com.ac.comehome.enums.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserServiceImpl service;
    @Autowired
    ValidCodeFilter filter;
    @Autowired
    private ObjectMapper mapper;

    public final static String USERNAME = "username";
    public final static String VALIDATECODE = "validateCode";

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/verifyCodeServlet", "/index.html","/register.html","/user/register")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/index.html")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, exception) -> {
                    RedisUtil.set(request.getSession().getId(), request.getParameter("username"), 20000);
//                    request.getSession().setAttribute(USERNAME, request.getParameter("username"));
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    String jsonObj = mapper.writeValueAsString(Result.success("登录成功"));
                    out.print(jsonObj);
                    out.flush();
                })
                .failureHandler((request, response, exception) -> {
                    exception.printStackTrace();
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    String jsonObj = mapper.writeValueAsString(Result.failure(ResultCode.WRONG_ACCOUNT_OR_PWD));
                    out.print(jsonObj);
                    out.flush();
                })
                .loginProcessingUrl("/user/login")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/user/logout")
                .logoutSuccessHandler(((httpServletRequest, httpServletResponse, authentication) -> {
                    RedisUtil.del(httpServletRequest.getSession().getId());
//                    httpServletRequest.getSession().removeAttribute(USERNAME);
                    httpServletResponse.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = httpServletResponse.getWriter();
                    String jsonObj = mapper.writeValueAsString(Result.success("退出登录成功"));
                    out.print(jsonObj);
                    out.flush();
                }))
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }
}
