--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:CreateSiteTable
CREATE TABLE SM_SITE_REF
(
  SITE_NO    VARCHAR2(10 BYTE),
  SNAME      VARCHAR2(255 BYTE),
  LATITUDE   NUMBER,
  LONGITUDE  NUMBER,
  FL_BEG_YR  NUMBER,
  FL_END_YR  NUMBER,
  FL_YRS     NUMBER,
  GEOM_LL    MDSYS.SDO_GEOMETRY
);
--rollback DROP TABLE SM_SITE_REF;

--chageset ajmccart:PopulateSiteTable
INSERT INTO SM_SITE_REF
select distinct
  CASE WHEN "site_no" ='NA' THEN null
       ELSE  "site_no" 
  END as SITE_NO, 
  "sname" SNAME, to_number("lat") LATITUDE,to_number("long") LONGITUDE, 
  CASE WHEN "fl_beg_yr"='NA' THEN null
       ELSE "fl_beg_yr" 
  END as FL_BEG_YR, 
    CASE WHEN "fl_end_yr"='NA' THEN null
       ELSE "fl_end_yr" 
  END as FL_END_YR,
  CASE WHEN "fl_yrs"='NA' THEN null
       ELSE "fl_yrs" 
  END as FL_YRS,
null 
from SM_ALLSSCDATACOMBINED3;

update sm_site_ref set site_no = 
(select site_no from sm_site_fix 
where sm_site_fix.sname = sm_site_ref.sname)
where exists
(select site_no from sm_site_fix 
where sm_site_fix.sname = sm_site_ref.sname);

update sm_site_ref set GEOM_LL = mdsys.sdo_geometry(2001,8307,mdsys.sdo_point_type
(longitude,latitude,null),null,null);
--rollback TRUNCATE TABLE sm_site_ref;

--changeset ajmccart:CreateSitePK
ALTER TABLE SM_SITE_REF ADD CONSTRAINT SM_SITE_REF_PK
 PRIMARY KEY (SITE_NO);
--rollback ALTER TABLE SM_SITE_REF DROP PRIMARY KEY;

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
   



