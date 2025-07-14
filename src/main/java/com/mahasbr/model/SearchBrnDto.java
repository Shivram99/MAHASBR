package com.mahasbr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchBrnDto {
 
	private String district;
	
	private String nameOfEstablishmentOrOwner;
	
	private String brnNo;
	
}
