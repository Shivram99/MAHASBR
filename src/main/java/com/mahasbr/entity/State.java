package com.mahasbr.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "states")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class State {

	
    @Id
    @NotBlank
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Prevents circular reference
    private List<District> districts;

    private Boolean isActive;

}
