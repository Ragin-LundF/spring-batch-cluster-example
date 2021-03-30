package com.example.batch.repositories;

import com.example.batch.models.Aggregate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregateRepository extends PagingAndSortingRepository<Aggregate, Long> {
}
