--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:CreateSiteBasins
CREATE TABLE SEDMAP.SITE_BASINS_REF
(
  SITE_NO  VARCHAR2(255 BYTE),
  V2       VARCHAR2(255 BYTE),
  V3       VARCHAR2(255 BYTE),
  V4       VARCHAR2(255 BYTE),
  V5       VARCHAR2(255 BYTE),
  V6       VARCHAR2(255 BYTE),
  V7       VARCHAR2(255 BYTE),
  V8       VARCHAR2(255 BYTE),
  V9       VARCHAR2(255 BYTE),
  V10      VARCHAR2(255 BYTE),
  V11      VARCHAR2(255 BYTE),
  V12      VARCHAR2(255 BYTE),
  V13      VARCHAR2(255 BYTE),
  V14      VARCHAR2(255 BYTE),
  V15      VARCHAR2(255 BYTE),
  V16      VARCHAR2(255 BYTE),
  V17      VARCHAR2(255 BYTE),
  V18      VARCHAR2(255 BYTE),
  V19      VARCHAR2(255 BYTE),
  V20      VARCHAR2(255 BYTE),
  V21      VARCHAR2(255 BYTE),
  V22      VARCHAR2(255 BYTE),
  V23      VARCHAR2(255 BYTE),
  V24      VARCHAR2(255 BYTE),
  V25      VARCHAR2(255 BYTE),
  V26      VARCHAR2(255 BYTE),
  V27      VARCHAR2(255 BYTE)
);

ALTER TABLE SEDMAP.SITE_BASINS_REF ADD (
  CONSTRAINT SITE_BASINS_REF_U01
  UNIQUE (SITE_NO));
  
ALTER TABLE SEDMAP.SITE_BASINS_REF ADD (
  CONSTRAINT SITE_BASINS_REF_R01 
  FOREIGN KEY (SITE_NO) 
  REFERENCES SEDMAP.SITE_REF (SITE_NO));

GRANT SELECT ON SEDMAP.SITE_BASINS_REF TO SEDUSER;
--rollback Drop table site_basins_ref;

--changeset ajmccart:PopulateSiteBasins
insert into site_basins_ref
select 
"site_no",
  V2  ,
  V3  ,
  V4  ,
  V5  ,
  V6  ,
  V7  ,
  V8  ,
  V9  ,
  V10 ,
  V11 ,
  V12 ,
  V13 ,
  V14 ,
  V15 ,
  V16 ,
  V17 ,
  V18 ,
  V19 ,
  V20 ,
  V21 ,
  V22 ,
  V23 ,
  V24 ,
  V25 ,
  V26 ,
  V27 from SRC_SEDSITES_BASINS;
--rollback truncate table site_basins_ref;  