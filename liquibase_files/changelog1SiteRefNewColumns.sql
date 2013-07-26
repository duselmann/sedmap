--liquibase formatted sql

--This is for the sedmap schema
 

--changeset duselman:AddToSiteInfoBasinLandK
alter table sm_site_ref add (
 GAGE_BASIN_ID varchar2(15),
 PCT_URBAN  number(2,0),
 PCT_AG     number(2,0),
 PCT_FOREST number(2,0),
 SOIL_K     number(3,3)
-- more possible k values
-- SOIL_K_PCT number(6,3),
-- SOIL_K_UP  number(6,3),
-- SOIL_K_PCT number(3,3)
 );

-- populate with test data
--changeset duselman:AddToSiteInfoBasinLandKSampleData
update sm_site_ref set
GAGE_BASIN_ID = trunc(DBMS_RANDOM.value(low => 1, high => 9)*100000000000000),
PCT_URBAN  = trunc(DBMS_RANDOM.value(low => 0, high => 10)*10),
PCT_AG     = trunc(DBMS_RANDOM.value(low => 0, high => 10)*10),
PCT_FOREST = trunc(DBMS_RANDOM.value(low => 0, high => 10)*10),
SOIL_K     = trunc(DBMS_RANDOM.value(low => 0, high => 1)*1000)/1000;

