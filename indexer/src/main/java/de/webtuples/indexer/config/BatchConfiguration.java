

package de.webtuples.indexer.config;

import de.webtuples.indexer.processor.CustomerProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

@Configuration
@EnableBatchProcessing
@Import(PersistenceConfiguration.class)
public class BatchConfiguration {

    private static final int TX_TIMEOUT_IN_SECONDS = 30 * 60;
    private static final int CHUNK_SIZE = 10000;

    @Autowired
    private PersistenceConfiguration persistenceConfiguration;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    /**
     * In Memory Job Repository.
     */
    @Bean
    public JobRepository jobRepository(PlatformTransactionManager transactionManager) {
        try {
            return new MapJobRepositoryFactoryBean(transactionManager).getObject();
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }



    @Bean
    public Job customerIndexDataJob(@Qualifier("customerIndexDataStep") Step leistungIndexGpvDataStep) {
        return jobBuilderFactory
                .get("dossierIndexGpvDataJob")
                .start(leistungIndexGpvDataStep)
                .build();
    }


    @Bean
    Step customerIndexDataStep(CustomerItemReader reader, CustomerProcessor processor, CustomerIndexItemWriter writer) {

        return stepBuilderFactory.get("customerIndexDataStep")
                .<Customer, Customer> chunk(CHUNK_SIZE)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionAttribute(createTransactionAttribute())
                .build();
    }

    private TransactionAttribute createTransactionAttribute() {
        DefaultTransactionAttribute defaultTransactionAttribute = new DefaultTransactionAttribute();
        defaultTransactionAttribute.setIsolationLevelName("ISOLATION_READ_COMMITTED");
        defaultTransactionAttribute.setPropagationBehaviorName("PROPAGATION_NOT_SUPPORTED");
        defaultTransactionAttribute.setTimeout(TX_TIMEOUT_IN_SECONDS);
        return defaultTransactionAttribute;
    }
}
