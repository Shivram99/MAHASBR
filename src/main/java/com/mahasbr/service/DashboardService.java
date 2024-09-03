package com.mahasbr.service;

import java.util.List;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.model.ActAndYearWiseModel;
import com.mahasbr.model.DistrictAndActWiseModel;
import com.mahasbr.model.DistrictAndIndustryModel;
import com.mahasbr.model.DistrictAndTimeWiseModel;
import com.mahasbr.model.IndustryAndTimeWiseModel;

public interface DashboardService {

List<DistrictAndTimeWiseModel> getDistrictAndTimeWise(String district, String quarter , String operation);
List<ActAndYearWiseModel> getActAndYearWise(String act, String year ,String operation) ;
List<DistrictAndActWiseModel> getDistrictAndActWise(String district, String act ,String operation);
List<DistrictAndIndustryModel> getDistrictAndEstateOwnerWise(String district, String estateOwner , String operation);
List<IndustryAndTimeWiseModel> getEstateOwnerAndQuarterWise(String estateOwner, String quarter, String operation);
}
