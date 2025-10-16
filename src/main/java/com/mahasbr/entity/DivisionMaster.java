package com.mahasbr.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Division_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DivisionMaster extends Auditable {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "division_seq_gen")
    @SequenceGenerator(name = "division_seq_gen", sequenceName = "division_seq", allocationSize = 1)
    private Long divisionId;

    @Column(nullable = false)
    private String divisionName;

    @Column(nullable = false, unique = true)
    private String divisionCode;

    @Column(nullable = false)
    private Boolean isActive = true;
}
