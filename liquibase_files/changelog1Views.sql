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

			
			
--changeset duselman:CreateSiteInfoView
create or replace view sedmap.SM_SITE_INFO as
select s.USGS_STATION_ID,STATION_NAME,LATITUDE,LONGITUDE,GEOM_LL,HUC_12,DA,"STATE",
    y.minyr as daily_min, y.maxyr as daily_max, y.minyr ||'-'|| y.maxyr as Daily_Period,
    f.minyr as discrete_min, f.maxyr as discrete_max, f.minyr ||'-'|| f.maxyr as Discrete_Period,
    NVL2(r.USGS_STATION_ID, '1','0') as REFERENCE_SITE,
      NVL( (select 1 from sedmap.sm_daily_sites d where s.usgs_station_id=d.usgs_station_id) ,0) as daily_site,
      NVL( (select 1 from sedmap.sm_inst_sites  d where s.usgs_station_id=d.usgs_station_id) ,0) as discrete_site,
      NVL(y.sample_years,0) as daily_years,
      NVL(f.discrete_samples,0) as discrete_samples
  from sedmap.sm_site_ref s 
  left outer join SM_REFERENCE_SITES r on s.USGS_STATION_ID = r.USGS_STATION_ID
  left outer join (
    select usgs_station_id, count(*) sample_years, min(sample_year) minyr, max(sample_year) maxyr 
      from sm_daily_year y 
     group by usgs_station_id) y 
  on (y.usgs_station_id = s.usgs_station_id)
  left outer join (
    select usgs_station_id, count(*) discrete_samples, EXTRACT(year FROM min(datetime)) minyr, EXTRACT(year FROM max(datetime)) maxyr 
      from sm_inst_sample_fact f 
     group by usgs_station_id) f 
  on (f.usgs_station_id = s.usgs_station_id)
;
			