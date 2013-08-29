--liquibase formatted sql

--This is for the seduser schema
 
--changeset ajmccart:CreateSiteRefSyn
CREATE OR REPLACE SYNONYM SEDUSER.SITE_REF FOR SEDMAP.SITE_REF;
--rollback Drop synonym SEDUSER.SITE_REF;  

--changeset ajmccart:CreateDailySitesSyn
CREATE OR REPLACE SYNONYM SEDUSER.DAILY_SITES FOR SEDMAP.DAILY_SITES;
--rollback Drop synonym SEDUSER.DAILY_SITES;

--changeset ajmccart:CreateDailyYearsSyn
CREATE OR REPLACE SYNONYM SEDUSER.DAILY_YEARS FOR SEDMAP.DAILY_YEARS;
--rollback Drop synonym SEDUSER.DAILY_YEARS;

--changeset ajmccart:CreateDiscreteSampleSyn
CREATE OR REPLACE SYNONYM SEDUSER.DISCRETE_SAMPLE_FACT FOR SEDMAP.DISCRETE_SAMPLE_FACT;
--rollback Drop synonym SEDUSER.DISCRETE_SAMPLE_FACT;

--changeset ajmccart:CreateDiscreteSitesSyn
CREATE OR REPLACE SYNONYM SEDUSER.DISCRETE_SITES FOR SEDMAP.DISCRETE_SITES;
--rollback Drop synonym SEDUSER.DISCRETE_SITES;

--changeset ajmccart:CreateFlowExceedanceSyn
CREATE OR REPLACE SYNONYM SEDUSER.FLOW_EXCEEDANCE FOR SEDMAP.FLOW_EXCEEDANCE;
--rollback Drop synonym SEDUSER.FLOW_EXCEEDANCE;

--changeset ajmccart:CreateSiteBasinSyn
CREATE OR REPLACE SYNONYM SEDUSER.SITE_BASINS_REF FOR SEDMAP.SITE_BASINS_REF;
--rollback Drop synonym SEDUSER.SITE_BASINS_REF;