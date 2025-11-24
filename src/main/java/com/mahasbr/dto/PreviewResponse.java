package com.mahasbr.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PreviewResponse {
  private String fileId;
  private String fileName;
  private List<RegistryRowDTO> rows;
}
