package org.example.medical.service;

import lombok.RequiredArgsConstructor;
import org.example.medical.model.MedicalRecord;
import org.example.medical.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository repository;

    public void uploadCSV(MultipartFile file) {
        try {
            List<MedicalRecord> medicalRecords = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

            // Read and validate headers
            String headerLine = reader.readLine();
            validateHeaders(headerLine);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                MedicalRecord record = new MedicalRecord();

                record.setSource(removeQuotes(data[0]));
                record.setCodeListCode(removeQuotes(data[1]));
                record.setCode(removeQuotes(data[2]));
                record.setDisplayValue(removeQuotes(data[3]));
                record.setLongDescription(removeQuotes(data[4]));

                // Parse dates
                String fromDateStr = removeQuotes(data[5]);
                if (!fromDateStr.isEmpty()) {
                    record.setFromDate(LocalDate.parse(fromDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                }

                String toDateStr = removeQuotes(data[6]);
                if (!toDateStr.isEmpty()) {
                    record.setToDate(LocalDate.parse(toDateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                }

                // Parse sorting priority
                String priorityStr = removeQuotes(data[7]);
                if (!priorityStr.isEmpty()) {
                    record.setSortingPriority(Integer.parseInt(priorityStr));
                }

                medicalRecords.add(record);
            }

            repository.saveAll(medicalRecords);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }
    }

    private void validateHeaders(String headerLine) {
        String[] expectedHeaders = {
                "source", "codeListCode", "code", "displayValue",
                "longDescription", "fromDate", "toDate", "sortingPriority"
        };

        String[] actualHeaders = headerLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (int i = 0; i < expectedHeaders.length; i++) {
            String actualHeader = removeQuotes(actualHeaders[i]);
            if (!expectedHeaders[i].equals(actualHeader)) {
                throw new RuntimeException("Invalid CSV header format. Expected: " +
                        String.join(",", expectedHeaders));
            }
        }
    }

    public List<MedicalRecord> getAllMedicalCodes() {
        return repository.findAll();
    }

    public Optional<MedicalRecord> getMedicalCodeByCode(String code) {
        return repository.findByCode(code);
    }

    public void deleteAllMedicalCodes() {
        repository.deleteAll();
    }

    private String removeQuotes(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.replaceAll("^\"|\"$", "").trim();
    }
}
