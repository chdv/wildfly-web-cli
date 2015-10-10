package com.dch.settings;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.IOException;

/**
 * Created by pixel on 31.08.2015.
 */
@Named
@RequestScoped
public class ReloadBean {

    @EJB(beanInterface = WildflyConfigBean.class)
    private WildflyConfigBean wildflyConfigBean;

    @Resource
    private ManagedExecutorService executorService;

    public String reload() throws IOException, InterruptedException {
        executorService.submit(() -> {
            wildflyConfigBean.reload();
            return null;
        });
        return FacesUtils.sendMessage("Операция запущена, ожидайте завершения...");
    }

}
