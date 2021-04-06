package com.example.batch.test;

import com.example.batch.repositories.AggregateRepository;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

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
        validateResult();
    }

    private void generateData() {
        System.out.println("Start generating data...");
        final var urlGenerate = String.format("http://localhost:%d/generate/100", port);
        final var generateResult = restTemplate.getForObject(urlGenerate, String.class);
        Assertions.assertThat(generateResult).isEqualTo("ok");
        System.out.println("Start generating data...finished");
    }

    private void startJobs() {
        System.out.println("Start generating jobs...");
        final var urlJobRunr = String.format("http://localhost:%d/startJobrnr", port);
        final var jobResult = restTemplate.getForObject(urlJobRunr, String.class);
        Assertions.assertThat(jobResult).isEqualTo("jobrnr started");
        System.out.println("Start generating jobs...finished");
    }

    private void waitSomeSecondsForFinish() {
        System.out.println("Start waiting...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
        }
        System.out.println("Start waiting...finished");
    }

    private void validateResult() {
        System.out.println("Start validating database...");
        final var aggregateResult = aggregateRepository.findAll();
        Assertions.assertThat(aggregateResult).isNotEmpty();
        System.out.println("Start validating database...finished");
    }
}
