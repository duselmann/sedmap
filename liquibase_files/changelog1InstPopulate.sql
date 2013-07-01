--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:PopulateInstSites
INSERT INTO SM_INST_SITES
select distinct
  a."site_no" 
from SM_SRC_ALLSSCDATACOMBINED7 a;
--rollback truncate sm_inst_sites;

--changeset ajmccart:CreateDataSeq
CREATE SEQUENCE SEDMAP.SM_DATA_SEQ
  START WITH 1
  MAXVALUE 9999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  NOCACHE
  NOORDER;
--rollback drop sequence sedmap.sm_data_seq;

--changeset ajmccart:PopulateInstSample
insert into sm_inst_sample_fact
SELECT 
   sm_data_seq.nextval,
   replace(S."Agencycode",'NA') AGENCY_CODE, 
   replace(S."Dailyflow",'NA') DAILY_FLOW, 
   to_date(s."Datetime",'YYYY-MM-DD HH24:MI:SS'),
   S."site_no",
   replace(S."dcomment",'NA') DCOMMENT, 
   replace(S.GH,'NA'), 
   replace(S."icomment",'NA') ICOMMENT, 
   replace(S."Instflow",'NA') INSTFLOW, 
   replace(S.LOI,'NA'), 
   replace(S."Numbersamppts",'NA') NUMBERSAMPPTS, 
   replace(S.P125,'NA'), 
   replace(S.P16,'NA'), 
   replace(S."P1milli",'NA') P1MILLI, 
   replace(S.P2,'NA'), 
   replace(S.P250,'NA'), 
   replace(S."P2milli",'NA') P2MILLI, 
   replace(S.P31,'NA'), 
   replace(S.P4,'NA'), 
   replace(S.P500,'NA'), 
   replace(S.P63,'NA'), 
   replace(S.P8,'NA'), 
   replace(S."pH",'NA') PH, 
   replace(S."pHfield",'NA') PH_FIELD, 
   replace(S."Samplecondition",'NA') SAMPLE_CONDITION, 
   replace(S."Samplepurpose",'NA') SAMPLE_PURPOSE, 
   replace(S."Samplesource",'NA') SAMPLE_SOURCE, 
   replace(S."Sampmethod",'NA') SAMP_METHOD, 
   replace(S."Sampsplitter",'NA') SAMP_SPLITTER, 
   replace(S."Samptype",'NA') SAMP_TYPE, 
   replace(S.SC,'NA'), 
   replace(S."Sclab",'NA') SC_LAB, 
   replace(S.SS,'NA'),  
   replace(S.SSC,'NA'), 
   replace(S."TempairC",'NA') TEMP_AIR_C, 
   replace(S."TempC",'NA') TEMP_C, 
   replace(S.TSS,'NA'), 
   replace(S.TSSC,'NA'), 
   replace(S."Turb1350",'NA') TURB1350, 
   replace(S."Turb61028",'NA') TURB1028, 
   replace(S."Turb63675",'NA') TURB3675, 
   replace(S."Turb63676",'NA') TURB63676, 
   replace(S."Turb63680",'NA') TURB63680, 
   replace(S."Turb70",'NA') TURB70, 
   replace(S."Turb75",'NA') TURB75, 
   replace(S."Turb76",'NA') TURB76, 
   replace(S."Turb99872",'NA') TURB88872, 
   replace(S."Velocity",'NA') VELOCITY, 
   replace(S."Verticals",'NA') VERTICALS, 
   replace(S."VisitPurpose",'NA') VISIT_PURPOSE, 
   replace(S."Width",'NA') WIDTH
FROM SM_SRC_ALLSSCDATACOMBINED7 S;
--rollback truncate table sm_inst_sample_fact;