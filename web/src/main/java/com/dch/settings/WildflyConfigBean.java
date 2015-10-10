package com.dch.settings;

import com.dch.utils.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by pixel on 31.08.2015.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class WildflyConfigBean {

    private Logger logger = LoggerFactory.getLogger(WildflyConfigBean.class);

    private String cliExecutable;

    private ReentrantLock lock = new ReentrantLock();

    private Condition waitCondition = lock.newCondition();

    private String user;

    private String passwd;

    private DateFormat format = new SimpleDateFormat("HH:mm:ss");

    @Resource
    private ManagedExecutorService executorService;

    private List<String> execLog = new CopyOnWriteArrayList<>();

    private String quote;

    public boolean login(String user, String passwd) throws IOException, InterruptedException {
        this.user = user;
        this.passwd = passwd;

        String response = runBatch("fake command");
        return (response != null && !response.startsWith("Failed"));
    }

    @PostConstruct
    private void init() {
        cliExecutable = getCliExecutable();
    }

    private String getCliExecutable() {
        String exec = "bin" + File.separator + "jboss-cli.";

        if(SystemUtils.IS_OS_UNIX) {
            exec += "sh";
            quote = "'";
        } else if(SystemUtils.IS_OS_WINDOWS) {
            exec += "bat";
            quote = "\"";
        }

        return System.getProperty("jboss.home.dir") + File.separator + exec;
    }

    public List<String> getExecLog() {
        return execLog;
    }

    public String getTempDir() {
        return System.getProperty("jboss.server.temp.dir");
    }

    @Lock(LockType.WRITE)
    private String runBatch(String batchCommands, File execDir) throws IOException, InterruptedException {
        String scriptFile = getTempDir() + File.separator + "commands.cli";
        File file = new File(scriptFile);
        FileUtils.write2File(batchCommands, file);
        addExecLog("run command: " + batchCommands);
        String result = runCommand(getFileInvoke(scriptFile), execDir);
        addExecLog(result);
        Thread.sleep(1000);
        file.delete();
        return result;
    }

    @Lock(LockType.WRITE)
    public void runBatches(List<String> commands, File execDir) throws IOException, InterruptedException {
        execLog = new ArrayList<>();
        for(String command : commands) {
            runBatch(command, execDir);
        }
    }

    @Lock(LockType.WRITE)
    public String runBatch(String batchCommands) throws IOException, InterruptedException {
        execLog = new ArrayList<>();
        return runBatch(batchCommands, null);
    }

    private String getJbossIp() {
        String result = System.getProperty("jboss.bind.address");
        if(result == null)
            return "127.0.0.1";
        return result;
    }

    private String getFileInvoke(String fileName) {
        return cliExecutable + " --connect controller=" + getJbossIp() + " --user=" + user + " --password=" + passwd + " --file=" + fileName;
    }

    private String quotes(String str) {
        return quote + str + quote;
    }

    @Lock(LockType.WRITE)
    public void reload() throws IOException, InterruptedException {
        runBatch("reload");
    }

    @Lock(LockType.WRITE)
    public void restart() throws IOException, InterruptedException {
        runBatch(":shutdown(restart=true)");
    }

    private void addExecLog(String message) {
        execLog.add(format.format(new Date()) + " " + message);
    }

    @Lock(LockType.WRITE)
    private String runCommand(String command) throws IOException, InterruptedException  {
        return runCommand(command, null);
    }

    @Lock(LockType.WRITE)
    private String runCommand(String command, File execDir) throws IOException, InterruptedException  {
        Process p = null;

        try {
            p = Runtime.getRuntime().exec(
                    command,
                    null,
                    execDir);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "Ошибка: " + e.getMessage();
        }

        StringWriter writer = new StringWriter();

        inheritIO(p.getInputStream(), writer);
        inheritIO(p.getErrorStream(), writer);

        lock.lock();
        try {
            waitCondition.await(5, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
        p.destroy();

        return writer.toString();
    }

    private void inheritIO(final InputStream src, final Writer dest) {
        executorService.submit(() -> {
            Scanner sc = new Scanner(src);
            if (sc.hasNextLine()) {
                dest.write(sc.nextLine());
                lock.lock();
                try {
                    waitCondition.signalAll();
                } finally {
                    lock.unlock();
                }
            }
            return null;
        });
    }

}
