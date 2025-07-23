package com.mahasbr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brn_state_sequence")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrnStateSequence {
    @Id
    @Column(name = "state_code", length = 2)
    private String stateCode;

    @Column(name = "current_value", nullable = false)
    private long currentValue = 0;

}

