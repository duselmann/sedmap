--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:CreateSiteTable
CREATE TABLE SM_SITE_REF
(
  USGS_STATION_ID      VARCHAR2(15 BYTE),
  STATION_NAME         VARCHAR2(255 BYTE),
  LATITUDE             NUMBER,
  LONGITUDE            NUMBER,
  GEOM_LL              MDSYS.SDO_GEOMETRY,
  FL_BEG_YR            NUMBER,
  FL_END_YR            NUMBER,
  FL_YRS               NUMBER,
  DRAINAGE_AREA_MI_SQ  NUMBER,
  FIPS_ST              NUMBER,
  FIPS_CNTY            NUMBER,
  HUC_12               VARCHAR2(255 BYTE),
  HUC_12_NAME          VARCHAR2(255 BYTE),
  DA                   NUMBER,
  STATE                VARCHAR2(2 BYTE),
  BASIN                VARCHAR2(255 BYTE),
  ECOREGION_NUM        VARCHAR2(255 BYTE),
  ECO3_NAME            VARCHAR2(255 BYTE),
  ECO2_NAME            VARCHAR2(255 BYTE),
  ECO1_NAME            VARCHAR2(255 BYTE)
);

ALTER TABLE SM_SITE_REF ADD 
  CONSTRAINT SM_SITE_REF_PK
 PRIMARY KEY
 (USGS_STATION_ID);
 
 GRANT SELECT ON SM_SITE_REF TO SEDUSER;
 --rollback Drop table SM_SITE_REF;
 
--changeset ajmccart:AddtoMetadataView
INSERT INTO mdsys.user_sdo_geom_metadata
    (TABLE_NAME,
     COLUMN_NAME,
     DIMINFO,
     SRID)
  VALUES ('SM_SITE_REF',
          'GEOM_LL',
          mdsys.SDO_DIM_ARRAY(
          mdsys.SDO_DIM_ELEMENT('X', -180, 180, 0.005), 
          mdsys.SDO_DIM_ELEMENT('Y', -90, 90, 0.005)
          ),
          8307);
--rollback delete from mdsys.user_sdo_geom_metadata where table_name='SM_SITE_REF';

--changeset ajmccart:spatialIndex
CREATE INDEX SM_SITE_GEOM_LL_SP_IDX
   ON "SM_SITE_REF"("GEOM_LL") INDEXTYPE IS
   MDSYS.SPATIAL_INDEX PARAMETERS (' SDO_INDX_DIMS=2 LAYER_GTYPE="POINT"');
--rollback DROP INDEX SM_SITE_GEOM_LL_SP_IDX;

--changeset ajmccart:CreateRefSitesTable
CREATE TABLE SM_REFERENCE_SITES
(
  USGS_STATION_ID  VARCHAR2(15 BYTE)
);

ALTER TABLE SM_REFERENCE_SITES ADD 
  CONSTRAINT SM_REFERENCE_SITE_FACT_PK
 PRIMARY KEY
 (USGS_STATION_ID);
 
 ALTER TABLE SM_REFERENCE_SITES ADD (
  CONSTRAINT SM_REFERENCE_SITE_FACT_R01 
 FOREIGN KEY (USGS_STATION_ID) 
 REFERENCES SM_SITE_REF (USGS_STATION_ID));

GRANT SELECT ON SM_REFERENCE_SITES TO SEDUSER;
--rollback drop table sm_reference_sites;
