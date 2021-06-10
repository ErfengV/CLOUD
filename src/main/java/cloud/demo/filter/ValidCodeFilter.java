package cloud.demo.filter;


import cloud.demo.exception.ValidateCodeException;
import cloud.demo.hander.MyAuthenticationFailureHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * program:clouddisk
 * description:ValidCodeFilter
 * author:lsj
 * create:2021-06-05 14:52
 */
@Component
public class ValidCodeFilter extends OncePerRequestFilter {

    @Autowired
    private MyAuthenticationFailureHandler handler;

    public final static String VALIDATECODE = "validateCode";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if ("/user/login".equals(httpServletRequest.getRequestURI()) && "post".equalsIgnoreCase(httpServletRequest.getMethod())) {
            try {
                validate(httpServletRequest);
            } catch (ValidateCodeException e) {
                handler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validate(HttpServletRequest httpServletRequest) {
        String validateCode = (String) httpServletRequest.getSession().getAttribute(VALIDATECODE);
        String imageCode = httpServletRequest.getParameter(VALIDATECODE);
        if (imageCode == null || "".equals(imageCode)) {
            throw new ValidateCodeException("验证码不能为空");
        }
        if (!imageCode.equals(validateCode)) {
            throw new ValidateCodeException("验证码不匹配");
        }
    }
}
