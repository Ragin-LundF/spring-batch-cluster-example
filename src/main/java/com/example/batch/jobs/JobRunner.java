package com.example.batch.jobs;

import com.example.batch.models.Aggregate;
import com.example.batch.models.Transactions;
import com.example.batch.repositories.AggregateRepository;
import com.example.batch.repositories.TransactionsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobRunner {
    private final AggregateRepository aggregateRepository;
    private final TransactionsRepository trxRepository;

    @Value("${test.service:none}")
    private String serverId;

    public void startJobs(@NonNull final String label) {
        BackgroundJob.enqueue(() -> {
            transformData(label);
        });
    }

    public void transformData(@NonNull final String label) {
        final var trxs = trxRepository.findAllByLabel(label);
        log.info(String.format("Found %d transactions for label %s", trxs.size(), label));
        storeAggregateOfTrx(trxs);
    }

    public void storeAggregateOfTrx(@NonNull final List<Transactions> trxs) {
        trxs.forEach(trx -> {
            final var aggregate = new Aggregate();
            aggregate.setServer(serverId);
            aggregate.setTrxId(trx.getId());
            aggregateRepository.save(aggregate);
            log.info(String.format("Processed transaction with ID %d", trx.getId()));
            try {
                Thread.sleep(200);
            } catch(InterruptedException ie) {
            }
        });
    }
}
