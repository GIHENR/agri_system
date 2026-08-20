package com.agri.agri.repository;

import com.agri.agri.entity.CropBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CropBatchRepository extends JpaRepository<CropBatch, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.stockLevel), 0) FROM CropBatch c")
    Integer getTotalStock();

    Optional<CropBatch> findByCropName(String cropName);
}