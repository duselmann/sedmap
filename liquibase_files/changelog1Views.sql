--liquibase formatted sql

--This is for the sedmap schema
 

--changeset duselman:CreateDiscreteSitesView
CREATE OR REPLACE FORCE VIEW "SEDMAP"."SM_INST_STATIONS" AS 
select s."USGS_STATION_ID","STATION_NAME","LATITUDE","LONGITUDE","GEOM_LL",
		"FL_BEG_YR","FL_END_YR","FL_YRS","DRAINAGE_AREA_MI_SQ","FIPS_ST",
		"FIPS_CNTY","HUC_12","HUC_12_NAME","DA","STATE","BASIN",
		"ECOREGION_NUM","ECO3_NAME","ECO2_NAME","ECO1_NAME", 
		NVL2(r.USGS_STATION_ID, '1','0') as REFERENCE_SITE
from sedmap.sm_site_ref s 
left outer join SM_REFERENCE_SITES r 
			on s.USGS_STATION_ID = r.USGS_STATION_ID
where exists (select 1 
			from sedmap.sm_inst_sites i 
			where s.usgs_station_id=i.usgs_station_id);



--changeset duselman:CreateDailySitesView
create or replace view sedmap.sm_daily_stations as
select s.USGS_STATION_ID,STATION_NAME,LATITUDE,LONGITUDE,GEOM_LL,
		FL_BEG_YR,FL_END_YR,FL_YRS,DRAINAGE_AREA_MI_SQ,FIPS_ST,
		FIPS_CNTY,HUC_12,HUC_12_NAME,DA,"STATE",BASIN,
        ECOREGION_NUM,ECO3_NAME,ECO2_NAME,ECO1_NAME,
        NVL2(r.USGS_STATION_ID, '1','0') as REFERENCE_SITE
from sedmap.sm_site_ref s 
left join (select usgs_station_id, count(*) sample_years 
			from sm_daily_year y 
			group by usgs_station_id) y 
			on (y.usgs_station_id = s.usgs_station_id)
left outer join SM_REFERENCE_SITES r 
			on s.USGS_STATION_ID = r.USGS_STATION_ID
where exists (select 1 
			from sedmap.sm_daily_sites i 
			where s.usgs_station_id=i.usgs_station_id)
