package com.agri.agri.controller;

import com.agri.agri.entity.CropBatch;
import com.agri.agri.repository.CropBatchRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.security.Principal;

@Controller
public class CropController {

    private final CropBatchRepository cropBatchRepository;
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    public CropController(CropBatchRepository cropBatchRepository) {
        this.cropBatchRepository = cropBatchRepository;
    }

    @GetMapping("/crop_inventory")
    public String showCropInventory(Model model, Principal principal) {
        // Spring Security handles authentication and ensures only Farmers reach this page
        model.addAttribute("username", principal != null ? principal.getName() : "Farmer");
        model.addAttribute("crops", cropBatchRepository.findAll());
        return "crop_inventory";
    }

    @PostMapping("/crop/add")
    public String addCropBatch(
            @RequestParam("cropName") String cropName,
            @RequestParam("cropImage") MultipartFile cropImage,
            @RequestParam("stockLevel") Integer stockLevel,
            @RequestParam("pricePerUnit") BigDecimal pricePerUnit,
            @RequestParam("dateAdded") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateAdded) throws IOException {

        CropBatch batch = new CropBatch();
        batch.setCropName(cropName);
        batch.setStockLevel(stockLevel);
        batch.setPricePerUnit(pricePerUnit);
        batch.setDateAdded(dateAdded);

        if (!cropImage.isEmpty()) {
            File uploadDir = new File(UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String fileName = cropImage.getOriginalFilename();
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            Files.write(fileNameAndPath, cropImage.getBytes());

            batch.setImageFileName(fileName);
        }

        cropBatchRepository.save(batch);
        return "redirect:/crop_inventory";
    }

    @PostMapping("/crop/edit")
    public String editCropBatch(
            @RequestParam("id") Long id,
            @RequestParam("cropName") String cropName,
            @RequestParam(value = "cropImage", required = false) MultipartFile cropImage,
            @RequestParam("stockLevel") Integer stockLevel,
            @RequestParam("pricePerUnit") BigDecimal pricePerUnit,
            @RequestParam("dateAdded") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateAdded) throws IOException {

        CropBatch batch = cropBatchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid crop Id:" + id));

        batch.setCropName(cropName);
        batch.setStockLevel(stockLevel);
        batch.setPricePerUnit(pricePerUnit);
        batch.setDateAdded(dateAdded);

        if (cropImage != null && !cropImage.isEmpty()) {
            File uploadDir = new File(UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) uploadDir.mkdir();

            String fileName = cropImage.getOriginalFilename();
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            Files.write(fileNameAndPath, cropImage.getBytes());

            batch.setImageFileName(fileName);
        }

        cropBatchRepository.save(batch);
        return "redirect:/crop_inventory";
    }

    @PostMapping("/crop/delete")
    public String deleteCropBatch(@RequestParam("id") Long id) {
        cropBatchRepository.deleteById(id);
        return "redirect:/crop_inventory";
    }
}