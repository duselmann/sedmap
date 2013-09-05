--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:CreateSiteTable
CREATE TABLE SEDMAP.SITE_REF
(
  SITE_NO             VARCHAR2(255 BYTE),
  SNAME               VARCHAR2(255 BYTE),
  LATITUDE            VARCHAR2(255 BYTE),
  LONGITUDE           VARCHAR2(255 BYTE),
  GEOM_LL             MDSYS.SDO_GEOMETRY,
  NWISDA1             VARCHAR2(255 BYTE),
  STATE              VARCHAR2(255 BYTE),
  COUNTY_NAME         VARCHAR2(255 BYTE),
  ECO_L3_CODE         VARCHAR2(255 BYTE),
  ECO_L3_NAME         VARCHAR2(255 BYTE),
  ECO_L2_CODE         VARCHAR2(255 BYTE),
  ECO_L2_NAME         VARCHAR2(255 BYTE),
  ECO_L1_NAME         VARCHAR2(255 BYTE),
  ECO_L1_CODE         VARCHAR2(255 BYTE),
  HUC_REGION_NAME     VARCHAR2(255 BYTE),
  HUC_SUBREGION_NAME  VARCHAR2(255 BYTE),
  HUC_BASIN_NAME      VARCHAR2(255 BYTE),
  HUC_SUBBASIN_NAME   VARCHAR2(255 BYTE),
  HUC_2                VARCHAR2(255 BYTE),
  HUC_4                VARCHAR2(255 BYTE),
  HUC_6                VARCHAR2(255 BYTE),
  HUC_8                VARCHAR2(255 BYTE),
  PERM                VARCHAR2(255 BYTE),
  BFI                 VARCHAR2(255 BYTE),
  KFACT               VARCHAR2(255 BYTE),
  RFACT               VARCHAR2(255 BYTE),
  PPT30               VARCHAR2(255 BYTE),
  URBAN               VARCHAR2(255 BYTE),
  FOREST              VARCHAR2(255 BYTE),
  AGRIC               VARCHAR2(255 BYTE),
  MAJ_DAMS            VARCHAR2(255 BYTE),
  NID_STOR            VARCHAR2(255 BYTE),
  CLAY                VARCHAR2(255 BYTE),
  SAND                VARCHAR2(255 BYTE),
  SILT                VARCHAR2(255 BYTE)
);

ALTER TABLE SEDMAP.SITE_REF ADD (
  CONSTRAINT SITE_REF_PK
  PRIMARY KEY
  (SITE_NO));
  
GRANT SELECT ON SEDMAP.SITE_REF TO SEDUSER;

 --rollback Drop table SITE_REF;
 
 --changeset ajmccart:AddtoMetadataView
INSERT INTO mdsys.user_sdo_geom_metadata
    (TABLE_NAME,
     COLUMN_NAME,
     DIMINFO,
     SRID)
  VALUES ('SITE_REF',
          'GEOM_LL',
          mdsys.SDO_DIM_ARRAY(
          mdsys.SDO_DIM_ELEMENT('X', -180, 180, 0.005), 
          mdsys.SDO_DIM_ELEMENT('Y', -90, 90, 0.005)
          ),
          8307);
--rollback delete from mdsys.user_sdo_geom_metadata where table_name='SITE_REF';

--changeset ajmccart:spatialIndex
CREATE INDEX SM_SITE_GEOM_LL_SP_IDX
   ON "SITE_REF"("GEOM_LL") INDEXTYPE IS
   MDSYS.SPATIAL_INDEX PARAMETERS (' SDO_INDX_DIMS=2 LAYER_GTYPE="POINT"');
--rollback DROP INDEX SM_SITE_GEOM_LL_SP_IDX;


--changeset ajmccart:PopulateSiteTable
 insert into site_ref 
(SITE_NO,
  SNAME,
  LATITUDE,
  LONGITUDE,
  GEOM_LL,
  NWISDA1,
  STATE,
  COUNTY_NAME,
  ECO_L3_CODE,
  ECO_L3_NAME,
  ECO_L2_CODE,
  ECO_L2_NAME,
  ECO_L1_NAME,
  ECO_L1_CODE,
  HUC_REGION_NAME,
  HUC_SUBREGION_NAME,
  HUC_BASIN_NAME,
  HUC_SUBBASIN_NAME ,
  HUC_2,
  HUC_4,
  HUC_6,
  HUC_8,
  PERM,
  BFI,
  KFACT,
  RFACT ,
  PPT30 ,
  URBAN ,
  FOREST,
  AGRIC ,
  MAJ_DAMS,
  NID_STOR ,
  CLAY  ,
  SAND ,
  SILT)  
select "site_no",
  "sname" ,
  "lat"   ,
  "long"  ,
  NULL,
  "nwisda1" ,
  "state_name" ,
  "county_name" ,
  ECO_L3_CODE,
  ECO_L3_NAME,
  ECO_L2_CODE,
  ECO_L2_NAME,
  ECO_L1_NAME,
  ECO_L1_CODE,
  HUC_REGION_NAME,
  HUC_SUBREGION_NAME,
  HUC_BASIN_NAME ,
  HUC_SUBBASIN_NAME ,
  HUC2,
  HUC4,
  HUC6,
  HUC8,
  PERM ,
  BFI  ,
  KFACT,
  RFACT,
  PPT30,
  URBAN,
  FOREST,
  AGRIC,
  MAJ_DAMS,
  NID_STOR,
  CLAY,
  SAND,
  SILT    
   from SRC_SSC_SITE_INFO_8_27_13;

update site_ref set GEOM_LL = mdsys.sdo_geometry(2001,8307,mdsys.sdo_point_type
(longitude,latitude,null),null,null); 
--rollback truncate table site_ref;




