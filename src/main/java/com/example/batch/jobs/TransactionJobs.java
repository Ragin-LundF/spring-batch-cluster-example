package com.example.batch.jobs;

import com.example.batch.models.Aggregate;
import com.example.batch.models.Transactions;
import com.example.batch.repositories.AggregateRepository;
import com.example.batch.repositories.TransactionsRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TransactionJobs {
    public static final String JOB_PARAM_LABEL = "JOB_LABEL";
    public static final String QUALIFIER_TRX_JOB = "TRX_QUALIFIER_JOB";
    private static final String QUALIFIER_TRX_STEP = "TRX_QUALIFIER_STEP";
    private static final String QUALIFIER_TRX_READER = "TRX_QUALIFIER_READER";
    private static final String QUALIFIER_TRX_WRITER = "TRX_QUALIFIER_WRITER";
    private static final String QUALIFIER_TRX_PROCESSOR = "TRX_QUALIFIER_PROCESSOR";
    private static final String JOB_TRX = "JOB_TRX";
    private static final String STEP_TRX = "STEP_TRX";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory trxSteps;

    @Value("${test.service:none}")
    private String serverId;

    @Bean
    @Qualifier(QUALIFIER_TRX_JOB)
    public Job trxJob(@Qualifier(QUALIFIER_TRX_STEP) final Step transactionJob) {
        return jobBuilderFactory
                .get(JOB_TRX)
                .start(transactionJob)
                .build();
    }

    @Bean
    @Qualifier(QUALIFIER_TRX_STEP)
    public Step trxStep(
            @Qualifier(QUALIFIER_TRX_READER) final RepositoryItemReader<Transactions> trxItemReader,
            @Qualifier(QUALIFIER_TRX_PROCESSOR) final ItemProcessor<Transactions, Aggregate> trxProcessor,
            @Qualifier(QUALIFIER_TRX_WRITER) final RepositoryItemWriter<Aggregate> trxWriter
    ) {
        return trxSteps.get(STEP_TRX)
                .<Transactions, Aggregate>chunk(5)
                .reader(trxItemReader)
                .processor(trxProcessor)
                .writer(trxWriter)
                .build();
    }

    @Bean
    @StepScope
    @Qualifier(QUALIFIER_TRX_READER)
    public RepositoryItemReader<Transactions> trxReader(final TransactionsRepository repository,
                                                        final @Value("#{jobParameters['JOB_LABEL']}") String label) {
        return new RepositoryItemReaderBuilder<Transactions>()
                .repository(repository)
                .methodName("findAllByLabel")
                .arguments(label)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .saveState(false)
                .build();
    }

    @Bean
    @Qualifier(QUALIFIER_TRX_WRITER)
    public RepositoryItemWriter<Aggregate> trxWriter(final AggregateRepository aggregateRepository) {
        final var writer = new RepositoryItemWriter<Aggregate>();
        writer.setRepository(aggregateRepository);
        writer.setMethodName("save");

        try {
            writer.afterPropertiesSet();
        } catch (Exception e) {
            log.error("Unable to write aggregates", e);
        }

        return writer;
    }

    @Bean
    @Qualifier(QUALIFIER_TRX_PROCESSOR)
    public ItemProcessor<Transactions, Aggregate> trxProcessor() {
        return trx -> {
            final var aggregate = new Aggregate();
            aggregate.setTrxId(trx.getId());
            aggregate.setServer(serverId);
            return aggregate;
        };
    }
}
