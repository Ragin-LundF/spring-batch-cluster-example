package com.example.batch.api;

import com.example.batch.jobs.JobRunner;
import com.example.batch.models.Aggregate;
import com.example.batch.models.Transactions;
import com.example.batch.repositories.AggregateRepository;
import com.example.batch.repositories.TransactionsRepository;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.randomizers.range.BigDecimalRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestAPI {
    private static final BigDecimalRangeRandomizer BD_RANDOM = new BigDecimalRangeRandomizer(1.0, 1000.0);
    private static final StringRandomizer STR_RANDOM = new StringRandomizer(20);
    private static final List<String> LABELS = Arrays.asList(
            "TEST", "MAIL", "SHOPPING", "INSURANCE", "CAR", "TV", "SALARY", "SECURITIES"
    );

    private final AggregateRepository aggregateRepository;
    private final TransactionsRepository trxRepository;
    private final JobRunner jobRunner;

    @GetMapping("/aggStatus")
    public ResponseEntity<String> aggregationStatus() {
        final var aggResult = aggregateRepository.findAll();
        return ResponseEntity.ok(String.valueOf(((List<Aggregate>) aggResult).size()));
    }

    /**
     * Use JobRnr instead of Spring Batch.
     *
     * @return ok
     */
    @GetMapping("/startJobrnr")
    public ResponseEntity<String> startJobRnr() {
        LABELS.forEach(
                jobRunner::startJobs
        );
        return ResponseEntity.ok("jobrnr started");
    }

    /**
     * Generate dummy data.
     *
     * @param numberOfTrx number of transactions should be generated.
     * @return ok
     */
    @GetMapping("/generate/{trx}")
    public ResponseEntity<String> generate(final @NonNull @PathVariable("trx") Integer numberOfTrx) {
        Transactions trx;
        for (int i = 0; i<numberOfTrx; i++) {
            trx = new Transactions();
            trx.setLabel(label());
            trx.setAmount(BD_RANDOM.getRandomValue());
            trx.setPurpose(STR_RANDOM.getRandomValue());

            trxRepository.save(trx);
        }
        return ResponseEntity.ok("ok");
    }

    private String label() {
        return LABELS.get(
                ThreadLocalRandom.current().nextInt(LABELS.size()) % LABELS.size()
        );
    }
}
