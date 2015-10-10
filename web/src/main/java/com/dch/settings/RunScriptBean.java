package com.dch.settings;


import com.dch.utils.TextUtils;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;

/**
 * Created by pixel on 31.08.2015.
 */
@Named
@RequestScoped
public class RunScriptBean {

    @EJB(beanInterface = WildflyConfigBean.class)
    private WildflyConfigBean wildflyConfigBean;

    private String script;

    public String process() throws IOException, InterruptedException {
        if(!TextUtils.isEmpty(script)) {
            wildflyConfigBean.runBatch(script);
            return FacesUtils.sendMessage(getExecLogString());
        } else {
            return FacesUtils.sendMessage("Ошибка: текст скрипта пустой");
        }
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<String> getExecLog() {
        return wildflyConfigBean.getExecLog();
    }

    public String getExecLogString() {
        StringBuilder result = new StringBuilder();
        for(String s : getExecLog()) {
            result.append(s);
            result.append("<br/>");
        }
        return result.toString();
    }
}
