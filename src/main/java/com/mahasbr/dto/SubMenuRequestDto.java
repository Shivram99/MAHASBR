package com.mahasbr.dto;
//src/main/java/com/example/dto/SubMenuRequestDto.java
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubMenuRequestDto {

 @NotNull(message = "Menu code is required")
 private Integer menuCode;

 @NotNull(message = "Sub-menu code is required")
 private Integer subMenuCode;

 private Integer roleId;

 @NotNull(message = "Sub-menu name (English) is required")
 @Size(max = 100)
 private String subMenuNameEnglish;

 private String controllerName;

 private String linkName;

 private String subMenuNameMarathi;

 private Character isActive;

 private String icon;
}
