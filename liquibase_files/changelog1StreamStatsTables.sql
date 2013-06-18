--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:CreateFloodFreq
CREATE TABLE SM_FLOOD_FREQ
(
  USGS_STATION_ID  VARCHAR2(255 BYTE),
  PK2              NUMBER,
  PK5              NUMBER,
  PK10             NUMBER,
  PK25             NUMBER,
  PK50             NUMBER,
  PK100            NUMBER,
  PK200            NUMBER,
  PK500            NUMBER
);

ALTER TABLE SM_FLOOD_FREQ ADD 
  CONSTRAINT SM_FLOOD_FREQ_FACT_PK
 PRIMARY KEY
 (USGS_STATION_ID);
 
 GRANT SELECT ON SM_FLOOD_FREQ TO SEDUSER;
 --rollback drop table sm_flood_freq;
 
--changeset ajmccart:CreateFlowExceedance
 CREATE TABLE SM_FLOW_EXCEEDANCE
(
  USGS_STATION_ID  VARCHAR2(255 BYTE),
  D1               NUMBER,
  D5               NUMBER,
  D10              NUMBER,
  D20              NUMBER,
  D25              NUMBER,
  D30              NUMBER,
  D40              NUMBER,
  D50              NUMBER,
  D60              NUMBER,
  D70              NUMBER,
  D75              NUMBER,
  D80              NUMBER,
  D90              NUMBER,
  D95              NUMBER,
  D99              NUMBER
);

ALTER TABLE SM_FLOW_EXCEEDANCE ADD 
  CONSTRAINT SM_FLOW_EXCEEDANCE_FACT_PK
 PRIMARY KEY
 (USGS_STATION_ID);
 
 GRANT SELECT ON SM_FLOW_EXCEEDANCE TO SEDUSER;
 --rollback drop table sm_flow_exceedance;
