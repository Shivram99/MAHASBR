package com.mahasbr.service;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mahasbr.dto.RegistryApiAuthConfigRequest;
import com.mahasbr.dto.RegistryApiAuthConfigResponse;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.entity.RegistryMasterEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.mapper.RegistryApiAuthConfigMapper;
import com.mahasbr.repository.RegistryApiAuthConfigRepository;
import com.mahasbr.repository.RegistryMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistryApiAuthConfigServiceImpl implements RegistryApiAuthConfigService {

    static final LocalDate DEFAULT_SYNC_FROM = LocalDate.of(2015, 1, 1);
    private static final Set<String> HTTP_METHODS = Set.of("GET", "POST");

    private final RegistryApiAuthConfigRepository repository;
    private final RegistryMasterRepository registryRepository;
    private final RegistryApiAuthConfigMapper mapper;
    private final RegistrySyncService syncService;

    @Override
    public RegistryApiAuthConfigResponse create(RegistryApiAuthConfigRequest request) {
        RegistryApiAuthConfigEntity entity = new RegistryApiAuthConfigEntity();
        applyRequest(entity, request, true);
        // New APIs start switched off unless explicitly switched on.
        entity.setActive(Boolean.TRUE.equals(request.getActive()));
        return toResponse(repository.save(entity));
    }

    @Override
    public RegistryApiAuthConfigResponse update(Long id, RegistryApiAuthConfigRequest request) {
        RegistryApiAuthConfigEntity entity = find(id);
        applyRequest(entity, request, false);
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        return toResponse(repository.save(entity));
    }

    @Override
    public RegistryApiAuthConfigResponse setActive(Long id, boolean active) {
        RegistryApiAuthConfigEntity entity = find(id);
        if (active && entity.getRegistry() == null) {
            throw new IllegalArgumentException("Link this API to a registry before switching it on.");
        }
        if (active && (!StringUtils.hasText(entity.getUsername()) || !StringUtils.hasText(entity.getPassword()))) {
            throw new IllegalArgumentException("Enter the login (username and password) before switching it on.");
        }
        repository.updateActive(id, active);
        entity.setActive(active);
        return toResponse(entity);
    }

    @Override
    public RegistryApiAuthConfigResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    public void delete(Long id) {
        RegistryApiAuthConfigEntity entity = find(id);
        if (syncService.isRunning(id)) {
            throw new IllegalArgumentException("This API is syncing right now. Try again when it has finished.");
        }
        repository.delete(entity);
    }

    @Override
    public List<RegistryApiAuthConfigResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public RegistryApiAuthConfigEntity getByServiceNameCode(String serviceNameCode) {
        return repository.findByServiceNameCode(serviceNameCode)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + serviceNameCode));
    }

    private RegistryApiAuthConfigEntity find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registry API not found with ID: " + id));
    }

    private RegistryApiAuthConfigResponse toResponse(RegistryApiAuthConfigEntity entity) {
        return mapper.toResponse(entity, syncService.isRunning(entity.getId()));
    }

    /** Validates the request and copies it onto the entity. */
    private void applyRequest(RegistryApiAuthConfigEntity entity, RegistryApiAuthConfigRequest request,
            boolean creating) {
        String serviceName = required(request.getServiceName(), "Service name");
        String code = required(request.getServiceNameCode(), "Service code").toUpperCase(Locale.ROOT);
        repository.findByServiceNameCodeIgnoreCase(code)
                .filter(other -> !Objects.equals(other.getId(), entity.getId()))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Service code " + code + " is already used.");
                });

        RegistryMasterEntity registry = registryRepository.findById(
                requiredId(request.getRegistryId()))
                .orElseThrow(() -> new IllegalArgumentException("Selected registry does not exist."));
        boolean registryChanged = entity.getRegistry() != null
                && !Objects.equals(entity.getRegistry().getId(), registry.getId());
        if (registryChanged && entity.getLastSyncedTo() != null) {
            throw new IllegalArgumentException(
                    "The registry can't be changed after data has been synced. Create a new API entry instead.");
        }

        LocalDate syncFrom = request.getSyncFromDate() != null ? request.getSyncFromDate() : DEFAULT_SYNC_FROM;
        if (syncFrom.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Sync start date can't be in the future.");
        }
        if (!creating && !Objects.equals(syncFrom, entity.getSyncFromDate())) {
            // Start date changed: fetch again from the new date. Existing rows are updated, not duplicated.
            entity.setLastSyncedTo(null);
        }

        entity.setServiceName(serviceName);
        entity.setServiceNameCode(code);
        String authType = StringUtils.hasText(request.getAuthType())
                ? request.getAuthType().trim().toUpperCase(Locale.ROOT) : "TOKEN";
        if (!authType.equals("TOKEN") && !authType.equals("BASIC")) {
            throw new IllegalArgumentException("Login type must be TOKEN or BASIC.");
        }
        entity.setAuthType(authType);
        if (authType.equals("TOKEN")) {
            entity.setAuthUrl(url(request.getAuthUrl(), "Login URL"));
            entity.setAuthUrlMethod(method(request.getAuthUrlMethod()));
        } else {   // BASIC: no separate login call
            entity.setAuthUrl(StringUtils.hasText(request.getAuthUrl()) ? url(request.getAuthUrl(), "Login URL") : null);
            entity.setAuthUrlMethod(null);
        }
        entity.setDateFromField(fieldName(request.getDateFromField(), "FromDate", "\"From\" date field name"));
        entity.setDateToField(fieldName(request.getDateToField(), "ToDate", "\"To\" date field name"));
        String dateFormat = StringUtils.hasText(request.getDateFormat()) ? request.getDateFormat().trim() : "yyyy-MM-dd";
        try {
            java.time.format.DateTimeFormatter.ofPattern(dateFormat).format(LocalDate.now());
        } catch (IllegalArgumentException | java.time.DateTimeException e) {
            throw new IllegalArgumentException("Date format is not valid (examples: yyyy-MM-dd, yyyyMMdd).");
        }
        entity.setDateFormat(dateFormat);
        entity.setSkipSslVerify(Boolean.TRUE.equals(request.getSkipSslVerify()));
        entity.setUsername(required(request.getUsername(), "Username"));
        entity.setApiData(url(request.getApiData(), "Data URL"));
        entity.setApiDataMethod(method(request.getApiDataMethod()));
        entity.setRegistry(registry);
        entity.setSyncFromDate(syncFrom);

        if (StringUtils.hasText(request.getPassword())) {
            entity.setPassword(request.getPassword());
        } else if (creating) {
            throw new IllegalArgumentException("Password is required.");
        }
        // Update with an empty password: keep the stored one.
    }

    private static String required(String value, String label) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }

    private static Long requiredId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Registry is required.");
        }
        return id;
    }

    private static String url(String value, String label) {
        String url = required(value, label);
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if ((scheme.equals("https") || scheme.equals("http")) && uri.getHost() != null) {
                return url;
            }
        } catch (IllegalArgumentException ignored) {
            // fall through to the error below
        }
        throw new IllegalArgumentException(label + " must be a full http(s):// address.");
    }

    /**
     * The name of a JSON field in the request body, e.g. from_date. It must start with a letter:
     * a name like 20260101 (a date value typed into the wrong box) is rejected by gateways that
     * convert the JSON to XML.
     */
    private static String fieldName(String value, String fallback, String label) {
        if (!StringUtils.hasText(value)) {
            return fallback;
        }
        String name = value.trim();
        if (!name.matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
            throw new IllegalArgumentException(label + " must be a field name such as from_date "
                    + "(letters, digits and _, starting with a letter), not a date value.");
        }
        return name;
    }

    private static String method(String value) {
        String method = StringUtils.hasText(value) ? value.trim().toUpperCase(Locale.ROOT) : "POST";
        if (!HTTP_METHODS.contains(method)) {
            throw new IllegalArgumentException("HTTP method must be GET or POST.");
        }
        return method;
    }
}
