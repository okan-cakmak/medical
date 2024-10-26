package org.example.medical;

import org.example.medical.model.MedicalRecord;
import org.example.medical.repository.MedicalRecordRepository;
import org.example.medical.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository repository;

    @InjectMocks
    private MedicalRecordService service;

    private MedicalRecord sampleRecord;
    private String validCsvContent;

    @BeforeEach
    void setUp() {
        // Setup sample record
        sampleRecord = new MedicalRecord();
        sampleRecord.setCode("CODE123");
        sampleRecord.setSource("TEST");
        sampleRecord.setDisplayValue("Test Record");
        sampleRecord.setFromDate(LocalDate.now());

        // Setup valid CSV content
        validCsvContent = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "\"TEST\",\"LIST1\",\"CODE123\",\"Test Record\",\"Description\",\"01-01-2024\",\"\",\"1\"";

    }

    @Test
    void uploadCSV_ValidFile_Success() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", validCsvContent.getBytes()
        );
        // Act & Assert
        assertDoesNotThrow(() -> service.uploadCSV(file));
    }

    @Test
    void uploadCSV_InvalidHeaders_ThrowsException() {
        // Arrange
        String invalidContent = "invalid,headers\nTEST,LIST1";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", invalidContent.getBytes()
        );

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> service.uploadCSV(file));
        assertTrue(exception.getMessage().contains("Invalid CSV header"));
    }

    @Test
    void getMedicalCodeByCode_ExistingCode_ReturnsRecord() {
        // Arrange
        when(repository.findByCode("CODE123")).thenReturn(Optional.of(sampleRecord));

        // Act
        Optional<MedicalRecord> result = service.getMedicalCodeByCode("CODE123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CODE123", result.get().getCode());
    }

    @Test
    void getAllMedicalCodes_ReturnsAllRecords() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(sampleRecord));

        // Act
        var result = service.getAllMedicalCodes();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(sampleRecord, result.get(0));
    }

    @Test
    void deleteAllMedicalCodes_Success() {
        // Act
        service.deleteAllMedicalCodes();

        // Assert
        verify(repository).deleteAll();
        verifyNoMoreInteractions(repository);
    }
}