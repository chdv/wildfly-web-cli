package com.dch.settings;


import com.dch.utils.FileUtils;
import com.dch.utils.ZipUtils;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by pixel on 31.08.2015.
 */
@Named
@SessionScoped
public class ModulesInstallBean implements Serializable{

    private Part uploadedFile;

    @EJB(beanInterface = WildflyConfigBean.class)
    private WildflyConfigBean wildflyConfigBean;

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String upload() throws IOException, InterruptedException {
        if (null != uploadedFile) {
            InputStream is = uploadedFile.getInputStream();
            String outputDir = wildflyConfigBean.getTempDir() + File.separator + "modules_" + System.currentTimeMillis();
            ZipUtils.unzip(is, outputDir);
            File outputDirFile = new File(outputDir);
            File cliFile = null;
            for(File f : outputDirFile.listFiles()) {
                if(f.getName().endsWith(".cli")) {
                    cliFile = f;
                    break;
                }
            }
            if(cliFile == null) {
                return FacesUtils.sendMessage("Ошибка: в архиве не найден cli-файл");
            }
            List<String> commands = FileUtils.getFileLines(cliFile);
            wildflyConfigBean.runBatches(commands, outputDirFile);
            Thread.sleep(3000); // pause for delete files
            FileUtils.delete(new File(outputDir));
            return FacesUtils.sendMessage(getExecLogString());
        } else {
            return FacesUtils.sendMessage("Ошибка: файл не выбран");
        }
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
