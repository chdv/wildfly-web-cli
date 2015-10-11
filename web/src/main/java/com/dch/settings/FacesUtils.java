package com.dch.settings;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Created by dcherdyntsev on 02.09.2015.
 */
public class FacesUtils {

    public static String sendMessage(String message) {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("result", message);
        return "result";
    }

    public static void put2Session(String key, Object value) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute(key, value);
    }

}
