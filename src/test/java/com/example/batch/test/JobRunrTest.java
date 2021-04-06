package com.example.batch.test;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import com.example.batch.repositories.AggregateRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class JobRunrTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AggregateRepository aggregateRepository;

    @Test
    public void testJobRunr() {
        generateData();
        startJobs();
        waitSomeSecondsForFinish();
        validateResultInDatabase();
        validateResultViaREST();
    }

    private void generateData() {
        final var urlGenerate = String.format("http://localhost:%d/generate/100", port);
        final var generateResult = restTemplate.getForObject(urlGenerate, String.class);
        Assertions.assertThat(generateResult).isEqualTo("ok");
    }

    private void startJobs() {
        final var urlJobRunr = String.format("http://localhost:%d/startJobrnr", port);
        final var jobResult = restTemplate.getForObject(urlJobRunr, String.class);
        Assertions.assertThat(jobResult).isEqualTo("jobrnr started");
    }

    private void waitSomeSecondsForFinish() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
        }
    }

    private void validateResultInDatabase() {
        final var aggregateResult = aggregateRepository.findAll();
        Assertions.assertThat(aggregateResult).isNotEmpty();
    }

    private void validateResultViaREST() {
        final var urlAggStatus = String.format("http://localhost:%d/aggStatus", port);
        final var aggStatus = restTemplate.getForObject(urlAggStatus, String.class);
        final var aggStatusLong = Long.valueOf(aggStatus);
        Assertions.assertThat(aggStatusLong).isGreaterThan(1);
    }
}
