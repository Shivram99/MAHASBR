package com.mahasbr.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.mahasbr.dto.CircularResponseDTO;
import com.mahasbr.dto.DivisionDto;
import com.mahasbr.dto.RegistryMasterResponse;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.model.SearchBrnDto;
import com.mahasbr.service.CircularService;
import com.mahasbr.service.DistrictMasterService;
import com.mahasbr.service.DivisionService;
import com.mahasbr.service.MstRegistryDetailsPageService;
import com.mahasbr.service.RegistryMasterService;
import com.mahasbr.service.TalukaMasterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/citizenSearch")
@RequiredArgsConstructor
public class SearchBRNController {

    private final DistrictMasterService districtservice;

    private final MstRegistryDetailsPageService mstRegistryDetailsPageService;

    private final TalukaMasterService talukaMasterService;

    private final CircularService circularService;

    private final RegistryMasterService registryMasterService;

    private final DivisionService divisionService;

    @GetMapping("/districts")
    public ResponseEntity<List<DistrictMaster>> getAll() {
        return ResponseEntity.ok(districtservice.findByIsActiveTrue());
    }

    @PostMapping("/searchBRN")
    public ResponseEntity<Page<MstRegistryDetailsPageEntity>> searchBRN(
            @Valid @RequestBody SearchBrnDto searchBrnDto,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "siNo") String sortBy) {
        if (StringUtils.isAllBlank(searchBrnDto.getEstablishmentName(), searchBrnDto.getBrn())) {
            throw new IllegalArgumentException("At least one of the fields 'Name of Establishment' or 'BRN No' is required.");
        }

        String districtCode = formatDistrictCode(searchBrnDto.getDistrictId());
        DistrictMaster district = districtservice.findByCensusDistrictCode(districtCode).orElseThrow(
                () -> new ResourceNotFoundException("District", "CensusDistrictCode", districtCode));

        String districtName = StringUtils.upperCase(StringUtils.trimToNull(district.getDistrictName()));
        String talukaName = resolveTalukaName(districtCode, searchBrnDto.getTalukaId());
        String brn = StringUtils.upperCase(StringUtils.trimToNull(searchBrnDto.getBrn()));
        String establishmentName = StringUtils.upperCase(StringUtils.trimToNull(searchBrnDto.getEstablishmentName()));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<MstRegistryDetailsPageEntity> result = mstRegistryDetailsPageService
                .searchBrnRecords(pageable, districtName, talukaName, brn, establishmentName);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/districtTaluka")
    public ResponseEntity<List<TalukaMaster>> getDistrictTaluka(@RequestBody List<String> districtCode) {
        return ResponseEntity.ok(talukaMasterService.findByCensusDistrictCodeInAndIsActiveTrue(districtCode));
    }

    @GetMapping("/circulars")
    public ResponseEntity<List<CircularResponseDTO>> getAllCirculars() {
        return ResponseEntity.ok(circularService.getAllCirculars());
    }

    @GetMapping("/files/**")
    public ResponseEntity<Resource> getCircularFile(HttpServletRequest request) {
        try {
            String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            String pathWithinHandlerMapping = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            String relativePath = new AntPathMatcher().extractPathWithinPattern(pattern, pathWithinHandlerMapping);
            Resource resource = circularService.getCircularFile(relativePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("X-Content-Type-Options", "nosniff")
                    .header(HttpHeaders.CACHE_CONTROL, "no-store, max-age=0")
                    .header("Pragma", "no-cache")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/registry")
    public ResponseEntity<List<RegistryMasterResponse>> getRegistringAutherity() {
        return ResponseEntity.ok(registryMasterService.getAllRegistries());
    }

    @GetMapping("/division")
    public ResponseEntity<List<DivisionDto>> findDivisionService() {
        return ResponseEntity.ok(divisionService.getAllDivisions());
    }

    private String resolveTalukaName(String districtCode, Long talukaId) {
        if (talukaId == null) {
            return null;
        }

        String talukaCode = formatTalukaCode(talukaId);
        TalukaMaster taluka = talukaMasterService
                .findActiveByDistrictCodeAndTalukaCode(districtCode, talukaCode)
                .orElseThrow(() -> new ResourceNotFoundException("Taluka", "CensusTalukaCode", talukaCode));

        return StringUtils.upperCase(StringUtils.trimToNull(taluka.getTalukaName()));
    }

    private String formatDistrictCode(Long districtId) {
        return StringUtils.leftPad(String.valueOf(districtId), 3, '0');
    }

    private String formatTalukaCode(Long talukaId) {
        return StringUtils.leftPad(String.valueOf(talukaId), 5, '0');
    }
}
