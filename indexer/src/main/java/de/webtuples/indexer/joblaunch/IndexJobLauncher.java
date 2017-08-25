

package de.webtuples.indexer.joblaunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class IndexJobLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(IndexJobLauncher.class);

    private static final String ORA_ERROR_SNAPSHOT_TOO_OLD = "ORA-01555";

    private final JobParametersRepository jobParametersRepository;
    private final JobLauncher jobLauncher;
    private final int startDateOffsetInSec;

    public IndexJobLauncher(JobParametersRepository jobParametersRepository, JobLauncher jobLauncher,
            @Value("${application.startdate.offset_in_sec}") int startDateOffsetInSec) {
        this.jobParametersRepository = jobParametersRepository;
        this.jobLauncher = jobLauncher;
        this.startDateOffsetInSec = startDateOffsetInSec;
    }

    public void run(Job job, Date initialStartDate) throws JobExecutionException {
        Date indexFromDate = jobParametersRepository.lastStartDate(job)
                .orElse(initialStartDate);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addDate(JobParameterKey.INDEX_FROM_DATE, indexFromDate)
                .addString(JobParameterKey.JOB_ID, UUID.randomUUID().toString());

        LOG.info("Launch job " + job.getName() + " with indexFromDate: " + indexFromDate);
        JobExecution jobExecution = jobLauncher.run(job, jobParametersBuilder.toJobParameters());

        if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            jobParametersRepository.saveStartDate(job, startDateWithGap);
        }


    }


}
