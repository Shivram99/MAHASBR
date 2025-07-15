package com.mahasbr.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Component
public class BRNGenartionRemark {

	private String locationCode;
	
	private String Remark;
	
}
