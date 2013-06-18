--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:DistinctDAtable
create table sm_src_all_da_distinct as
select distinct site_no,da from sm_src_allsedsitesdrainage;
--rollback delete table sm_src_all_da_distinct;

--changeset ajmccart:UpdateDA
update sm_site_ref set da=null;

update sm_site_ref set da=
(select da from sm_src_all_da_distinct
where sm_site_ref.usgs_station_id = sm_src_all_da_distinct.site_no)
where exists
(select da from sm_src_all_da_distinct
where sm_site_ref.usgs_station_id = sm_src_all_da_distinct.site_no);
--rollback update sm_site_ref set da=null;