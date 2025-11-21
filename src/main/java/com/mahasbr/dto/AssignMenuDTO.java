package com.mahasbr.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignMenuDTO {
	@NotEmpty
	private List<Long> menuIds; 
    private Long roleId; // optional if assigning to role
    private Long userId; // optional if assigning to user
}
