package com.mahasbr.dto;

public interface CitizenDashboardDataRegDeRegNewReg {
	String getRegistryName();
    String getDistrict();
    String getDivision();
    Integer getYear();
    String getQuarter();

    Long getTotalRegistrations();
    Long getTotalPersonsWorking();
    Long getTotalDeregistrations();
    Long getNewRegistrationsThisYear();
}
