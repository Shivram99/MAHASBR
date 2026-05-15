package com.mahasbr.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mahasbr.dto.RegisteredEstablishmentExportDto;

@Repository
public class RegisteredEstablishmentExportQueryRepository {

	private final JdbcTemplate jdbcTemplate;

	public RegisteredEstablishmentExportQueryRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public long countForExport(
			boolean applyRegistryFilter,
			Integer registryId,
			boolean applyDistrictFilter,
			List<String> districts,
			boolean applyTalukaFilter,
			List<String> talukas,
			String brn) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM mst_reg_details WHERE 1 = 1");
		List<Object> parameters = new ArrayList<>();

		appendFilters(sql, parameters, applyRegistryFilter, registryId, applyDistrictFilter, districts,
				applyTalukaFilter, talukas, brn);

		Long count = jdbcTemplate.queryForObject(sql.toString(), parameters.toArray(), Long.class);
		return count != null ? count : 0L;
	}

	public List<RegisteredEstablishmentExportDto> findBatchForExport(
			boolean applyRegistryFilter,
			Integer registryId,
			boolean applyDistrictFilter,
			List<String> districts,
			boolean applyTalukaFilter,
			List<String> talukas,
			String brn,
			long lastSeenSiNo,
			int batchSize) {
		StringBuilder sql = new StringBuilder("""
				SELECT
				    si_no,
				    establishment_or_owner_name,
				    house_no,
				    street_name,
				    locality,
				    pin_code,
				    telephone_mob_number,
				    email_address,
				    pan_number,
				    tan_number,
				    head_office_house_no,
				    head_office_street_name,
				    head_office_locality,
				    head_office_pin_code,
				    head_office_telephone_mob_number,
				    head_office_email_address,
				    head_office_pan_number,
				    head_office_tan_number,
				    major_activity_description,
				    nic2008_activity_code,
				    nic2008_activity_code_description,
				    operation_start_year,
				    ownership_code,
				    total_persons_working,
				    act_authority_reg_numbers,
				    remarks,
				    location_code,
				    registration_status,
				    brn_number,
				    town_village,
				    taluka,
				    district,
				    sector,
				    ward_number,
				    authority_name,
				    act_name,
				    TO_CHAR(registration_date, 'YYYY-MM-DD') AS registration_date,
				    TO_CHAR(deregistration_expiry_date, 'YYYY-MM-DD') AS deregistration_expiry_date,
				    gst_number,
				    hsn_code
				FROM mst_reg_details
				WHERE si_no > ?
				""");

		List<Object> parameters = new ArrayList<>();
		parameters.add(lastSeenSiNo);
		appendFilters(sql, parameters, applyRegistryFilter, registryId, applyDistrictFilter, districts,
				applyTalukaFilter, talukas, brn);
		sql.append(" ORDER BY si_no ASC FETCH FIRST ? ROWS ONLY");
		parameters.add(batchSize);

		return jdbcTemplate.query(connection -> prepareStatement(connection.prepareStatement(sql.toString()), parameters,
				batchSize), (resultSet, rowNum) -> new RegisteredEstablishmentExportDto(
						resultSet.getLong("si_no"),
						resultSet.getString("establishment_or_owner_name"),
						resultSet.getString("house_no"),
						resultSet.getString("street_name"),
						resultSet.getString("locality"),
						resultSet.getString("pin_code"),
						resultSet.getString("telephone_mob_number"),
						resultSet.getString("email_address"),
						resultSet.getString("pan_number"),
						resultSet.getString("tan_number"),
						resultSet.getString("head_office_house_no"),
						resultSet.getString("head_office_street_name"),
						resultSet.getString("head_office_locality"),
						resultSet.getString("head_office_pin_code"),
						resultSet.getString("head_office_telephone_mob_number"),
						resultSet.getString("head_office_email_address"),
						resultSet.getString("head_office_pan_number"),
						resultSet.getString("head_office_tan_number"),
						resultSet.getString("major_activity_description"),
						resultSet.getString("nic2008_activity_code"),
						resultSet.getString("nic2008_activity_code_description"),
						resultSet.getString("operation_start_year"),
						resultSet.getString("ownership_code"),
						resultSet.getString("total_persons_working"),
						resultSet.getString("act_authority_reg_numbers"),
						resultSet.getString("remarks"),
						resultSet.getString("location_code"),
						resultSet.getString("registration_status"),
						resultSet.getString("brn_number"),
						resultSet.getString("town_village"),
						resultSet.getString("taluka"),
						resultSet.getString("district"),
						resultSet.getString("sector"),
						resultSet.getString("ward_number"),
						resultSet.getString("authority_name"),
						resultSet.getString("act_name"),
						resultSet.getString("registration_date"),
						resultSet.getString("deregistration_expiry_date"),
						resultSet.getString("gst_number"),
						resultSet.getString("hsn_code")));
	}

	private PreparedStatement prepareStatement(PreparedStatement preparedStatement, List<Object> parameters,
			int batchSize) throws SQLException {
		preparedStatement.setFetchSize(batchSize);
		preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);

		for (int index = 0; index < parameters.size(); index++) {
			preparedStatement.setObject(index + 1, parameters.get(index));
		}

		return preparedStatement;
	}

	private void appendFilters(StringBuilder sql, List<Object> parameters, boolean applyRegistryFilter,
			Integer registryId, boolean applyDistrictFilter, List<String> districts, boolean applyTalukaFilter,
			List<String> talukas, String brn) {
		if (applyRegistryFilter) {
			sql.append(" AND reg_user_id = ?");
			parameters.add(registryId);
		}

		if (applyDistrictFilter) {
			appendInClause(sql, parameters, "district", districts);
		}

		if (applyTalukaFilter) {
			appendInClause(sql, parameters, "taluka", talukas);
		}

		if (brn != null) {
			sql.append(" AND brn_number = ?");
			parameters.add(brn);
		}
	}

	private void appendInClause(StringBuilder sql, List<Object> parameters, String columnName, List<String> values) {
		sql.append(" AND ").append(columnName).append(" IN (");
		for (int index = 0; index < values.size(); index++) {
			if (index > 0) {
				sql.append(", ");
			}
			sql.append('?');
			parameters.add(values.get(index));
		}
		sql.append(')');
	}
}
