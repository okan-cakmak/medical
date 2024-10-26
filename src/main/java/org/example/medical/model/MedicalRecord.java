package org.example.medical.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String codeListCode;

    @Column(unique = true)  // Make code field unique
    private String code;

    private String displayValue;
    private String longDescription;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer sortingPriority;
}