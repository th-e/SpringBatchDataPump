package de.webtuples.indexer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


import de.webtuples.indexer.joblaunch.IndexJobLauncher;

@SpringBootApplication
@EnableScheduling

public class Application implements SchedulingConfigurer {

    private static final int THREAD_POOL_SIZE = 1;

    @Autowired
    private IndexJobLauncher jobLauncher;


    @Autowired
    @Qualifier("customerIndexDataJob")
    private Job customerIndexDataJob;

    private static ConfigurableApplicationContext context;
    private static String[] args;

    private static ScheduledExecutorService reusedExecutor;

    /**
     */
    private final Date startDateInitialLoadCustomer = Date.from(LocalDateTime.now().minusYears(2).atZone(ZoneId.systemDefault()).toInstant());

    public static void main(String[] args) {

        Application.args = args;
        Application.context = createContext(Application.args);
    }

    private static ConfigurableApplicationContext createContext(String[] args){
        SpringApplication springApp = new SpringApplication(Application.class);
        return springApp.run(args);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        if (reusedExecutor == null) {
            reusedExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        }
        return reusedExecutor;
    }


    @Scheduled(fixedDelayString = "${application.scheduled.delay_in_ms}", initialDelayString = "${application.scheduled.initial_delay_in_ms}")
    protected void runJob() throws JobExecutionException {

        if (!Application.context.isActive()){
            Application.context = createContext(Application.args);
        }
        jobLauncher.run(customerIndexDataJob, startDateInitialLoadCustomer);

        // Hack
        Application.resetApplicationContext();
    }

    public static void resetApplicationContext(){
        Application.context.close();
        Application.context = createContext(Application.args);
    }

}
