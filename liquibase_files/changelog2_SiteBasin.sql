--liquibase formatted sql

--This is for the sedmap schema

--changeset duselman:CreateSiteBasinTable
create table site_basin (
  SITE_NO varchar2(15),
  BASIN_IDS varchar2(1000)
);
alter table site_basin add constraint 
site_basin_pk primary key(SITE_NO);
grant select on site_basin to seduser;
-- rollback drop table site_basin;


--changeset duselman:PopulateSiteBasinTableFromSiteBasinsRef
insert into site_basin --  turns out the empty cols are not null, but if they where...
select site_no,
NVL(v2||',','') ||
NVL(v3||',','') ||
NVL(v4||',','') ||
NVL(v5||',','') ||
NVL(v6||',','') ||
NVL(v7||',','') ||
NVL(v8||',','') ||
NVL(v9||',','') ||
NVL(v10||',','') ||
NVL(v11||',','') ||
NVL(v12||',','') ||
NVL(v13||',','') ||
NVL(v14||',','') ||
NVL(v15||',','') ||
NVL(v16||',','') ||
NVL(v17||',','') ||
NVL(v18||',','') ||
NVL(v19||',','') ||
NVL(v20||',','') ||
NVL(v21||',','') ||
NVL(v22||',','') ||
NVL(v23||',','') ||
NVL(v24||',','') ||
NVL(v25||',','') ||
NVL(v26||',','') ||
NVL(v27||',','')
from site_Basins_ref;
-- rollback truncate table site_basin;
