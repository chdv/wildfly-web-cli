package com.dch.settings;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by pixel on 03.09.2015.
 */
@WebFilter("*.xhtml")
public class LoginFilter implements Filter {

    public static final String AUTHORIZATION_ATTR = "authorization";

    private String loginPage = "/faces/login.xhtml";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        if(req.getRequestURI().endsWith(loginPage)) {
            chain.doFilter(request, response);
        } else if(req.getSession().getAttribute(AUTHORIZATION_ATTR)!=Boolean.TRUE) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendRedirect(req.getContextPath() + loginPage);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

}
