package com.mahasbr.initializer;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.repository.RegistryApiAuthConfigRepository;
import com.mahasbr.repository.RegistryMasterRepository;

import lombok.RequiredArgsConstructor;

/**
 * Adds the registry APIs that were previously hard-coded (Charity, DISH, Labour, Co-op, KVIB,
 * MCGM, MSME, MCA, GSTN) to registry_api_auth_config, so they can be managed on the
 * Registry API Settings page. Runs at every start but only adds what is missing.
 *
 * <ul>
 * <li>Every entry starts <b>switched off</b>.</li>
 * <li>Logins are NOT stored in code. For each API they are read from the environment variables
 * {@code REGISTRY_API_<CODE>_USERNAME} and {@code REGISTRY_API_<CODE>_PASSWORD}
 * (e.g. {@code REGISTRY_API_LABOUR_USERNAME}; "CO-OP" becomes {@code CO_OP}). Anything not
 * set is left blank to be entered on the settings page.</li>
 * <li>Each entry is linked to the registry whose id matches the id used by the old code
 * (21 Charity ... 29 GSTN), if that registry exists; otherwise choose it on the page.</li>
 * </ul>
 * Existing entries are never changed.
 */
@Component
@RequiredArgsConstructor
public class RegistryApiSeeder implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(RegistryApiSeeder.class);

	private static final String LOGIN_ONLINE = "https://sbrlmsapi.mahaonline.gov.in/api/Login";
	private static final String LOGIN_ITGOV = "https://sbrlmsapi.mahaitgov.in/api/Login";

	record Source(long registryId, String code, String name, String loginUrl, String dataUrl, boolean basic) {
		Source(long registryId, String code, String name, String loginUrl, String dataUrl) {
			this(registryId, code, name, loginUrl, dataUrl, false);
		}
	}

	static final List<Source> SOURCES = List.of(
			new Source(21, "CHARITY", "Charity Commissioner", LOGIN_ITGOV,
					"https://sbrlmsapi.mahaitgov.in/api/SBR/GetSBRShopData"),
			new Source(22, "DISH", "DISH (Industrial Safety & Health)", LOGIN_ONLINE,
					"https://sbrlmsapi.mahaonline.gov.in/api/SBR/GetSBRShopData"),
			new Source(23, "LABOUR", "Labour", LOGIN_ONLINE,
					"https://sbrlmsapi.mahaonline.gov.in/api/SBR/getSBRFactoryData"),
			new Source(24, "CO-OP", "Co-Op Societies", LOGIN_ITGOV,
					"https://sbrlmsapi.mahaitgov.in/api/SBR/GetCoOperativeData"),
			new Source(25, "KVIB", "KVIB", LOGIN_ITGOV, "https://sbrlmsapi.mahaitgov.in/api/SBR/GetKVIBData"),
			// MCGM (Municipal Corporation of Greater Mumbai): Basic authentication, no login call,
			// dates sent as from_date / to_date in yyyyMMdd. See Source.basic().
			new Source(26, "MCGM", "Municipal Corporation of Greater Mumbai (MCGM)", null,
					"https://65.0.229.38:50001/RESTAdapter/Api/SharingShop", true),
			new Source(27, "MSME", "MSME", LOGIN_ITGOV, "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMSMEData"),
			new Source(28, "MCA", "MCA", LOGIN_ITGOV, "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMCAData"),
			new Source(29, "GSTN", "GSTN", LOGIN_ITGOV, "https://sbrlmsapi.mahaitgov.in/api/SBR/GetGSTNData"));

	private final RegistryApiAuthConfigRepository configRepository;
	private final RegistryMasterRepository registryRepository;

	@Override
	public void run(ApplicationArguments args) {
		seed(System::getenv);
	}

	/** @return number of entries added */
	int seed(UnaryOperator<String> env) {
		int added = 0;
		for (Source source : SOURCES) {
			if (configRepository.findByServiceNameCodeIgnoreCase(source.code()).isPresent()) {
				continue;
			}
			String prefix = "REGISTRY_API_" + source.code().replaceAll("[^A-Za-z0-9]", "_").toUpperCase(Locale.ROOT);

			RegistryApiAuthConfigEntity entity = new RegistryApiAuthConfigEntity();
			entity.setServiceName(source.name());
			entity.setServiceNameCode(source.code());
			entity.setAuthUrl(source.loginUrl());
			if (source.basic()) {
				entity.setAuthType("BASIC");
				entity.setAuthUrlMethod(null);
				entity.setDateFromField("from_date");
				entity.setDateToField("to_date");
				entity.setDateFormat("yyyyMMdd");
			} else {
				entity.setAuthUrlMethod("POST");
			}
			entity.setApiData(source.dataUrl());
			entity.setApiDataMethod("POST");
			entity.setUsername(blankToEmpty(env.apply(prefix + "_USERNAME")));
			entity.setPassword(blankToEmpty(env.apply(prefix + "_PASSWORD")));
			entity.setActive(false);
			entity.setSyncFromDate(LocalDate.of(2015, 1, 1));
			registryRepository.findById(source.registryId()).ifPresent(entity::setRegistry);

			configRepository.save(entity);
			added++;
			log.info("Added registry API '{}' (switched off, registry linked: {}, login set: {})", source.code(),
					entity.getRegistry() != null, !entity.getUsername().isEmpty() && !entity.getPassword().isEmpty());
		}
		return added;
	}

	private static String blankToEmpty(String value) {
		return value == null || value.isBlank() ? "" : value.trim();
	}
}
