--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:PopulateFloodFreq
insert into sm_flood_freq
select
  "StaID" as USGS_STATION_ID,
  PK2,
  PK5,
  PK10,
  PK25,
  PK50,
  PK100,
  PK200,
  PK500
  from SM_SRC_STREAMSTATSSLIMMED
  where "StaID" in
  (select usgs_station_id from sm_site_ref);
 --rollback truncate table sm_flood_freq;
 
 --changeset ajmccart:PopulateFlowExceedance
 insert into sm_flow_exceedance
select
  "StaID" as USGS_STATION_ID,
  D1,
  D5,
  D10,
  D20,
  D25,
  D30,
  D40,
  D50,
  D60,
  D70,
  D75,
  D80,
  D90,
  D95,
  D99
  from SM_SRC_STREAMSTATSSLIMMED
  where "StaID" in
  (select usgs_station_id from sm_site_ref);
  --rollback truncate table sm_flow_exceedance