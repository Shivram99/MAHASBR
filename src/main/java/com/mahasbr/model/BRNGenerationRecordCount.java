package com.mahasbr.model;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BRNGenerationRecordCount {

	private Integer totalBRNGeneretion;
	private Integer concernData;
	private Integer duplicateGeneration;
	private Integer totalGeneration;
	private Set<String> missingHeaders = new HashSet<>();
}
