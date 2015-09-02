--liquibase formatted sql

--This is for the sedmap schema

--changeset duselman:trimEcoRegion1
update site_ref set ECO_L1_NAME=trim(ECO_L1_NAME)
where ECO_L1_NAME<>trim(ECO_L1_NAME);
--rollback update site_ref set set ECO_L1_NAME=trim(ECO_L1_NAME) where ECO_L1_NAME<>trim(ECO_L1_NAME);

--changeset duselman:trimEcoRegion2
update site_ref set ECO_L2_NAME=trim(ECO_L2_NAME)
where ECO_L2_NAME<>trim(ECO_L2_NAME);
--rollback update site_ref set ECO_L2_NAME=trim(ECO_L2_NAME) where ECO_L2_NAME<>trim(ECO_L2_NAME);

--changeset duselman:trimEcoRegion3
update site_ref set ECO_L3_NAME=trim(ECO_L3_NAME)
where ECO_L3_NAME<>trim(ECO_L3_NAME);
--rollback update site_ref set ECO_L3_NAME=trim(ECO_L3_NAME) where ECO_L3_NAME<>trim(ECO_L3_NAME);

--changeset duselman:prefixHuc8With0
update site_ref set HUC_8='0'||huc_8
where length(huc_8)=7;
--rollback update site_ref set HUC_8='0'||huc_8 where length(huc_8)=7;

--changeset duselman:prefixHuc6With0
update site_ref set HUC_6='0'||huc_6
where length(huc_6)=5;
--rollback update site_ref set HUC_6='0'||huc_6 where length(huc_6)=5;

--changeset duselman:prefixHuc4With0
update site_ref set HUC_4='0'||huc_4
where length(huc_4)=3;
--rollback update site_ref set HUC_4='0'||huc_4 where length(huc_4)=3;

--changeset duselman:prefixHuc2With0
update site_ref set HUC_2='0'||huc_2
where length(huc_2)=1;
--rollback update site_ref set HUC_2='0'||huc_2 where length(huc_2)=1;


--changeset duselman:upperCaseSiteName
update site_ref set sname = upper(sname);
--rollback update site_ref set sname = upper(sname);

--changeset duselman:updateMissingStates-GU-PR-VI
Update site_ref Set State = 'GU' where site_no = '16809600';
Update site_ref Set State = 'GU' where site_no = '16854500'; 
Update site_ref Set State = 'GU' where site_no = '16858000';
Update site_ref Set State = 'GU' where site_no = '16865000';
Update site_ref Set State = 'PR' where huc_basin_name = 'Puerto Rico';
Update site_ref Set State = 'VI' where site_no = '50292600';
Update site_ref Set State = 'VI' where site_no = '50294000';
--rollback Update site_ref Set State = 'VI' where site_no = '50294000';
