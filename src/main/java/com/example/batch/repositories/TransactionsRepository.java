package com.example.batch.repositories;

import com.example.batch.models.Transactions;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Page<Transactions> findAllByLabel(String label, Pageable pageable);
    List<Transactions> findAllByLabel(String label);
}
