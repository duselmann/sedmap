--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:PopulateSiteTable
INSERT INTO SM_SITE_REF
(USGS_STATION_ID,
  STATION_NAME,
  LATITUDE,
  LONGITUDE,
  FL_BEG_YR,
  FL_END_YR,
  FL_YRS)
select distinct
  a."site_no", 
  a."sname" SNAME, to_number(a."lat") LATITUDE,to_number(a."long") LONGITUDE, 
  CASE WHEN a."fl_beg_yr"='NA' THEN null
       ELSE a."fl_beg_yr" 
  END as FL_BEG_YR, 
    CASE WHEN a."fl_end_yr"='NA' THEN null
       ELSE a."fl_end_yr" 
  END as FL_END_YR,
  CASE WHEN a."fl_yrs"='NA' THEN null
       ELSE a."fl_yrs" 
  END as FL_YRS
from SM_SRC_ALLSSCDATACOMBINED7 a;

insert into sm_site_ref (USGS_STATION_ID, STATION_NAME, LATITUDE, LONGITUDE)
select 
a.usgs_station_id,
a.station_name,
CASE WHEN a."Latitude"='NA' THEN NULL
ELSE a."Latitude"
END,
CASE WHEN a."Longitude"='NA' THEN NULL
ELSE a."Longitude"
END
 from sm_src_daily_summ_sites a
where a.usgs_station_id not in
(select usgs_station_id from sm_site_ref);

update sm_site_ref set GEOM_LL = mdsys.sdo_geometry(2001,8307,mdsys.sdo_point_type
(longitude,latitude,null),null,null);

update sm_site_ref
set
DRAINAGE_AREA_MI_SQ  = (select replace(DRAINAGE_AREA_MI_SQ,'NA')  from sm_src_daily_summ_sites where sm_site_ref.usgs_station_id=sm_src_daily_summ_sites.usgs_station_id)
where exists (select 1 from sm_src_daily_summ_sites where sm_site_ref.USGS_STATION_ID = sm_src_daily_summ_sites.USGS_STATION_ID);

 update sm_site_ref
set 
FIPS_ST = (select "fips_st" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
FIPS_CNTY= (select "fips_cnty" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
HUC_12= (select "huc12" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
HUC_12_NAME= (select "huc12name" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
DA= (select replace(DA,'#N/A') from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
STATE= (select replace("state",'#N/A') from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
BASIN= (select replace("basin",'#N/A') from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
ECOREGION_NUM= (select "ecoregion_num" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
ECO3_NAME= (select "eco3name" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
ECO2_NAME= (select "eco2name" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno"),
ECO1_NAME= (select "eco1name" from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno")
where exists (select 1 from sm_src_state_eco_huc where sm_site_ref.usgs_station_id=sm_src_state_eco_huc."siteno");
--rollback truncate table sm_site_ref;

--changeset ajmccart:PopulateRefSites
insert into sm_reference_sites
select distinct staid from sm_src_reference_sites
where staid in
(select usgs_station_id from sm_site_ref);
--rollback truncate table sm_reference_sites;