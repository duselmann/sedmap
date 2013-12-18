--liquibase formatted sql

--This is for the sedmap schema
 
  
--changeset duselman:CreateDaIndex
CREATE INDEX "SEDMAP"."SITE_REF_DA_IDX" ON "SEDMAP"."SITE_REF" ("NWISDA1");
--rollback Drop index SITE_REF_DA_IDX;

--changeset duselman:CreateStateIndex
CREATE INDEX "SEDMAP"."SITE_REF_STATE_IDX" ON "SEDMAP"."SITE_REF" ("STATE");
--rollback Drop index SITE_REF_STATE_IDX;
  
--changeset duselman:CreateHucIndex
CREATE INDEX "SEDMAP"."SITE_REF_HUC_IDX" ON "SEDMAP"."SITE_REF" ("HUC_8");
--rollback Drop index SITE_REF_HUC_IDX;
  
--changeset duselman:CreateEco3Index
CREATE INDEX "SEDMAP"."SITE_REF_ECO3_IDX" ON "SEDMAP"."SITE_REF" ("ECO_L3_CODE");
--rollback Drop index SITE_REF_ECO3_IDX;

--changeset duselman:CreateEco2Index
CREATE INDEX "SEDMAP"."SITE_REF_ECO2_IDX" ON "SEDMAP"."SITE_REF" ("ECO_L2_CODE");
--rollback Drop index SITE_REF_ECO2_IDX;

--changeset duselman:CreateUrbanIndex
CREATE INDEX "SEDMAP"."SITE_REF_URBAN_IDX" ON "SEDMAP"."SITE_REF" ("URBAN");
--rollback Drop index SITE_REF_URBAN_IDX;

--changeset duselman:CreateAgricIndex
CREATE INDEX "SEDMAP"."SITE_REF_AGRIC_IDX" ON "SEDMAP"."SITE_REF" ("AGRIC");
--rollback Drop index SITE_REF_AGRIC_IDX;

--changeset duselman:CreateForestIndex
CREATE INDEX "SEDMAP"."SITE_REF_FOREST_IDX" ON "SEDMAP"."SITE_REF" ("FOREST");
--rollback Drop index SITE_REF_FOREST_IDX;
  
--changeset duselman:CreateKFactIndex
CREATE INDEX "SEDMAP"."SITE_REF_KFACT_IDX" ON "SEDMAP"."SITE_REF" ("KFACT");
--rollback Drop index SITE_REF_KFACT_IDX;

--changeset duselman:CreateRFactIndex
CREATE INDEX "SEDMAP"."SITE_REF_RFACT_IDX" ON "SEDMAP"."SITE_REF" ("RFACT");
--rollback Drop index SITE_REF_RFACT_IDX;

--changeset duselman:CreateClayIndex
CREATE INDEX "SEDMAP"."SITE_REF_CLAY_IDX" ON "SEDMAP"."SITE_REF" ("CLAY");
--rollback Drop index SITE_REF_CLAY_IDX;
  
--changeset duselman:CreateSandIndex
CREATE INDEX "SEDMAP"."SITE_REF_Sand_IDX" ON "SEDMAP"."SITE_REF" ("SAND");
--rollback Drop index "SITE_REF_Sand_IDX";
  
--changeset duselman:CreateSiltIndex
CREATE INDEX "SEDMAP"."SITE_REF_Silt_IDX" ON "SEDMAP"."SITE_REF" ("SILT");
--rollback Drop index "SITE_REF_Silt_IDX";
  
--changeset duselman:CreateDiscreteYearIndex
CREATE INDEX "SEDMAP"."DISCRETE_SAMPLE_YEAR_IDX" ON "SEDMAP"."DISCRETE_SAMPLE_FACT" (EXTRACT(YEAR FROM "DATETIME"));
--rollback Drop index DISCRETE_SAMPLE_YEAR_IDX;
