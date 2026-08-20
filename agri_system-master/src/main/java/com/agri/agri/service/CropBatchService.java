package com.agri.agri.service;

import com.agri.agri.dto.CropBatchRequest;
import com.agri.agri.entity.CropBatch;
import com.agri.agri.repository.CropBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CropBatchService {

    private final CropBatchRepository cropBatchRepository;

    public CropBatchService(CropBatchRepository cropBatchRepository) {
        this.cropBatchRepository = cropBatchRepository;
    }

    @Transactional(readOnly = true)
    public List<CropBatch> getAllCrops() {
        return cropBatchRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public CropBatch addCrop(CropBatchRequest request) {
        CropBatch crop = new CropBatch();
        crop.setCropName(request.getCropName());
        crop.setPricePerUnit(request.getPricePerUnit());
        crop.setStockLevel(request.getStockLevel());
        crop.setImageFileName(request.getImageFileName() != null ? request.getImageFileName() : "default.jpg");
        crop.setDateAdded(LocalDate.now());

        return cropBatchRepository.save(crop);
    }
}