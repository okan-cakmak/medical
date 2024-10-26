package org.example.medical.controller;

import lombok.RequiredArgsConstructor;
import org.example.medical.model.MedicalRecord;
import org.example.medical.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            // Would add logging here with requester's details
            medicalRecordService.uploadCSV(file);
            return ResponseEntity.ok("CSV file uploaded and processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalCodes() {
        // Would add logging here with requester's details
        List<MedicalRecord> codes = medicalRecordService.getAllMedicalCodes();
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getMedicalCodeByCode(@PathVariable String code) {
        // Would add logging here with requester's details
        return medicalRecordService.getMedicalCodeByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllMedicalCodes() {
        // Would add logging here with requester's details
        medicalRecordService.deleteAllMedicalCodes();
        return ResponseEntity.ok("All medical codes deleted successfully");
    }
}

