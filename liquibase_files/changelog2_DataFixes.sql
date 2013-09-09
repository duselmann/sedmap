--liquibase formatted sql

--This is for the sedmap schema

--changeset duselman:trimEcoRegion1
update site_ref set ECO_L1_NAME=trim(ECO_L1_NAME)
where ECO_L1_NAME<>trim(ECO_L1_NAME);

--changeset duselman:trimEcoRegion2
update site_ref set ECO_L2_NAME=trim(ECO_L2_NAME)
where ECO_L2_NAME<>trim(ECO_L2_NAME);

--changeset duselman:trimEcoRegion3
update site_ref set ECO_L3_NAME=trim(ECO_L3_NAME)
where ECO_L3_NAME<>trim(ECO_L3_NAME);

--changeset duselman:prefixHuc8With0
update site_ref set HUC_8='0'||huc_8
where length(huc_8)=7;
-- we could write a rollback that removes the leading 0

--changeset duselman:convertDateTimeFromTextToDateTime_1of6
alter table discrete_sample_fact rename column datetime to datetimetxt;
--changeset duselman:convertDateTimeFromTextToDateTime_2of6
alter table discrete_sample_fact add DATETIME date;
--changeset duselman:convertDateTimeFromTextToDateTime_3of6
update discrete_sample_fact set DATETIME=TO_DATE(datetimetxt,  'YYYY-MM-DD HH24:MI:SS');
--changeset duselman:convertDateTimeFromTextToDateTime_4of6
alter table discrete_sample_fact drop constraint DISCRETE_SAMPLE_FACT_U01;
--changeset duselman:convertDateTimeFromTextToDateTime_5of6
alter table discrete_sample_fact drop column datetimetxt;
--changeset duselman:convertDateTimeFromTextToDateTime_6of6
CREATE INDEX DISCRETE_SAMPLE_FACT_IDX  ON discrete_sample_fact (site_no,datetime);
-- we could write a rollback that reverses this process

