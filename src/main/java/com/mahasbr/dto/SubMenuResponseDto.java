package com.mahasbr.dto;
//src/main/java/com/example/dto/SubMenuResponseDto.java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubMenuResponseDto {
 private Integer subMenuId;
 private Integer menuCode;
 private Integer subMenuCode;
 private Integer roleId;
 private String subMenuNameEnglish;
 private String controllerName;
 private String linkName;
 private String subMenuNameMarathi;
 private Character isActive;
 private String icon;
}
