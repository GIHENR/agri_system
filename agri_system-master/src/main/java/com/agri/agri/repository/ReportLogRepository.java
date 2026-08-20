package com.agri.agri.repository;

import com.agri.agri.entity.ReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportLogRepository extends JpaRepository<ReportLog, Long> {
    // Fetches the most recent reports first
    List<ReportLog> findTop20ByOrderByGeneratedAtDesc();
}