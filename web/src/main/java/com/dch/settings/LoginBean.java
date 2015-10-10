package com.dch.settings;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.IOException;

/**
 * Created by pixel on 31.08.2015.
 */
@Named
@RequestScoped
public class LoginBean {

    @EJB(beanInterface = WildflyConfigBean.class)
    private WildflyConfigBean wildflyConfigBean;

    private String user;

    private String passwd;

    private String message;

    public String login() throws IOException, InterruptedException {
        if(wildflyConfigBean.login(user, passwd)) {
            FacesUtils.put2Session(LoginFilter.AUTHORIZATION_ATTR, Boolean.TRUE);
            return "index";
        } else {
            message = "Вход невозможен";
            return "login";
        }
    }

    public String exit() {
        FacesUtils.put2Session(LoginFilter.AUTHORIZATION_ATTR, Boolean.FALSE);
        return "login";
    }

    public String getUser() {
        return user;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getMessage() {
        return message;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
