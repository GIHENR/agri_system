package com.agri.agri.controller;

import com.agri.agri.dto.CropBatchRequest;
import com.agri.agri.entity.CropBatch;
import com.agri.agri.service.CropBatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class CropBatchApiController {

    private final CropBatchService cropBatchService;

    public CropBatchApiController(CropBatchService cropBatchService) {
        this.cropBatchService = cropBatchService;
    }


    @GetMapping
    public ResponseEntity<List<CropBatch>> getAllInventory() {
        return ResponseEntity.ok(cropBatchService.getAllCrops());
    }


    @PostMapping
    public ResponseEntity<?> addInventoryCrop(@Valid @RequestBody CropBatchRequest request) {
        try {
            CropBatch savedCrop = cropBatchService.addCrop(request);
            return ResponseEntity.ok(savedCrop);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to add inventory item.");
        }
    }
}