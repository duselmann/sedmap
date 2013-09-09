--liquibase formatted sql

--This is for the sedmap schema
 

--changeset duselman:CreateDiscreteStationsView
CREATE OR REPLACE FORCE VIEW "SEDMAP"."DISCRETE_STATIONS" AS 
select s.*, NVL2(b.SITE_NO, '1','0') as BENCHMARK_SITE
from sedmap.site_ref s 
left outer join sedmap.BENCHMARK_SITES b 
            on s.SITE_NO = b.SITE_NO
where exists (select 1 
            from sedmap.discrete_sites i 
            where s.SITE_NO=i.SITE_NO);
grant select on DISCRETE_SITES to seduser;
--  drop view DISCRETE_SITES;


--changeset duselman:CreateDailyStationsView
create or replace view sedmap.daily_stations as
select s.*, NVL2(b.SITE_NO, '1','0') as BENCHMARK_SITE
from sedmap.site_ref s 
left join (select SITE_NO, count(*) sample_years 
            from sedmap.daily_year y 
            group by SITE_NO) y 
            on (y.SITE_NO = s.SITE_NO)
left outer join sedmap.BENCHMARK_SITES b 
            on s.SITE_NO = b.SITE_NO
where exists (select 1 
            from sedmap.daily_sites i 
            where s.SITE_NO=i.SITE_NO);
grant select on daily_sites to seduser;
--  drop view daily_sites;

            
            
--changeset duselman:CreateSiteInfoView
create or replace view sedmap.SITE_INFO as
select s.SITE_NO,SNAME,LATITUDE,LONGITUDE,GEOM_LL,HUC_8,NWISDA1,"STATE",
    y.minyr as daily_min, y.maxyr as daily_max, y.minyr ||'-'|| y.maxyr as Daily_Period,
    f.minyr as discrete_min, f.maxyr as discrete_max, f.minyr ||'-'|| f.maxyr as Discrete_Period,
    NVL2(b.SITE_NO, '1','0') as BENCHMARK_SITE,
      NVL( (select 1 from sedmap.daily_sites d where s.SITE_NO=d.SITE_NO) ,0) as daily_site,
      NVL( (select 1 from sedmap.discrete_sites  d where s.SITE_NO=d.SITE_NO) ,0) as discrete_site,
      NVL(y.sample_years,0) as daily_years,
      NVL(f.discrete_samples,0) as discrete_samples
  from sedmap.site_ref s 
  left outer join sedmap.BENCHMARK_SITES b on s.SITE_NO = b.SITE_NO
  left outer join (
    select SITE_NO, count(*) sample_years, min(sample_year) minyr, max(sample_year) maxyr 
      from sedmap.daily_year y 
     group by SITE_NO) y 
  on (y.SITE_NO = s.SITE_NO)
  left outer join (
    select SITE_NO, count(*) discrete_samples, EXTRACT(year FROM min(datetime)) minyr, EXTRACT(year FROM max(datetime)) maxyr 
      from sedmap.discrete_sample_fact f 
     group by SITE_NO) f 
  on (f.SITE_NO = s.SITE_NO);
grant select on SITE_INFO to seduser;
--  drop view SITE_INFO;


--changeset duselman:ecoRegion1View
create or replace view sedmap.ECO1NAMES as
select distinct ECO_L1_NAME ECO_NAME from sedmap.site_ref 
where ECO_L1_NAME is not null order by 1;
grant select on ECO1NAMES to seduser;
--  drop view ECO1NAMES;

--changeset duselman:ecoRegion2View
create or replace view sedmap.ECO2NAMES as
select distinct ECO_L2_NAME ECO_NAME from sedmap.site_ref 
where ECO_L2_NAME is not null order by 1;
grant select on ECO2NAMES to seduser;
--  drop view ECO2NAMES;

--changeset duselman:ecoRegion3View
create or replace view sedmap.ECO3NAMES as
select distinct ECO_L3_NAME ECO_NAME from sedmap.site_ref 
where ECO_L3_NAME is not null order by 1;
grant select on ECO3NAMES to seduser;
--  drop view ECO3NAMES;

