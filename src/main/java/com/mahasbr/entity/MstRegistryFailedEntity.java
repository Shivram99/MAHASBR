package com.mahasbr.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mst_registry_failed")
@Data
public class MstRegistryFailedEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_registry_failed_seq_gen")
    @SequenceGenerator(name = "mst_registry_failed_seq_gen", sequenceName = "mst_registry_failed_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "error_message" )
    private String errorMessage;  // Reason for failure

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "api_url")
    private String apiUrl;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "taluka_name")
    private String talukaName;

    @Column(name = "village_name")
    private String villageName;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** The single record as received (JSON, max 4000 chars), so it can be fixed and re-run. */
    @Column(name = "raw_data", length = 4000)
    private String rawData;

    /** Registry the record came from (mst_registry_master.id). */
    @Column(name = "reg_user_id")
    private Integer regUserId;
}
