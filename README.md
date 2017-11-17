sedmap
======


Dev URL:  http://cida-eros-sedmapdev.er.usgs.gov:8080/sediment/
Updating tables, views, indexes for new datasets using Liquibase:
So far, for data refreshes we have received a whole new dataset - rather than a set of data to add to existing data.  I delete all tables, views, indexes, and changelog tables (scripts in the notes below) and then re-run all of the liquibase scripts.  I found this useful for these reasons:
*The tables need to be truncated and reloaded anyway.
*After the site table is loaded the records need to be updated with the GEOM_LL data anyway (seechangelog1SiteRef.sql).
*All of the site_ref updates need to be rerun anyway (see changelog2_DataFixes.sql).
*Even indirect tables need to be truncated and reloaded.  For example, the daily_year table is created by pivoting a column in daily_years (see changelog2_DailyYear.sql)

If you decide to take this approach,
1) First bring in the new datasets as SRC_ (source) tables and name them appropriately (in the DEV schema). I use Toad 'Create Table' and 'Import Table Data'
2) If needed, update the source files to replace 'NA' with null.  Or, if the tables are already loaded, you can use and adjust current files created (SRC_ALLSSC_DATACOMBINED_replaceNA.txt, SRC_DAILY_SSLINVENTORY_replaceNA.txt, SRC_SSC_SITE_INFO_replaceNA.txt).  --In these files, you will need to use REPLACE to replace the old SRC_ table name with the new one.  Also, you may have to add lines for new columns - ie, in SRC_DAILY_SSLINVENTORY_replaceNA.txt I had to add "2014" when the time came.
3) Alter the liquibase files (repo: https://github.com/USGS-CIDA/sedmap/tree/liquibase) to
(a) replace the old SRC table names with your new SRC table names and
(b) make any other necessary adjustments, for example:
    b1) Do you need to add a line for new daily years (see changelog2_DaylyYear.sql)?  Remove a line/year?
    b2) Do you need to update additional sites in site_ref when State is null for GU, PR, or VI sites (see changelog2_DataFixes.sql)?
4) Delete all of the necessary tables, views, indexes and changelog tables from the schema from the DEV schema (see below: "Deleting from schema")
5) Run the liquibase scripts against the DEV schema (using Jenkins job Liquibase_AnnualDataRefresh on http://cida-eros-sedmapdev.er.usgs.gov:8080/jenkins/) and fix any problems you may run into
6) Copy the SRC files from the DEV schema onto the QA and PROD schemas
7) Run the delete scripts against the QA schema, then run the liquibase scripts against the QA schema.

Deleting from schema:
To delete all of the tables, views, indexes and chagelog tables run these scripts:
-------------------------------------------------------------------------------
Drop index SITE_REF_DA_IDX;

Drop index SITE_REF_STATE_IDX;

Drop index SITE_REF_HUC_IDX;

Drop index SITE_REF_ECO3_IDX;

Drop index SITE_REF_ECO2_IDX;
Drop index SITE_REF_URBAN_IDX;
Drop index SITE_REF_AGRIC_IDX;
Drop index SITE_REF_FOREST_IDX;
Drop index SITE_REF_KFACT_IDX;
Drop index SITE_REF_RFACT_IDX;
Drop index SITE_REF_CLAY_IDX;
Drop index SITE_REF_SAND_IDX;
Drop index SITE_REF_SILT_IDX;
Drop index DISCRETE_SAMPLE_YEAR_IDX;

drop view SITE_REF_BASIN;
drop view DISCRETE_STATIONS;
drop view daily_stations;
drop view SITE_INFO;
drop view ECO1NAMES;
drop view ECO2NAMES;
drop view ECO3NAMES;
drop view DISCRETE_STATIONS_DL;
drop view DAILY_STATIONS_DL;

drop table site_basin;
drop table daily_year;
Drop table benchmark_sites;    
Drop table flow_exceedance;
Drop table discrete_sample_fact;
Drop table discrete_sites;
Drop table daily_years;  
Drop table daily_sites;
DROP INDEX SM_SITE_GEOM_LL_SP_IDX;
delete from mdsys.user_sdo_geom_metadata where table_name='SITE_REF';
Drop table SITE_REF;
drop table DATABASECHANGELOG;
drop table DATABASECHANGELOGLOCK;
---------------------------------------------------------------------------------------

Updating the new basin layer shapefile:
We usually get shapefiles from Casey Lee (cjlee@usgs.gov) in a zip file that averages 180MB+.  Regardless of the names (they differ year by year while this year they looked like "Basins.2015.updatedesc"), we are only interested in 5 files:
*.dbf
*.prj
*.shp
*.shp.xml
*.shx
These 5 files must be renamed to the correct layer name used in Geoserver.  The end result must be:
Allbasinsupdate.desc.dbf
Allbasinsupdate.desc.prj
Allbasinsupdate.desc.shp
Allbasinsupdate.desc.shp.xml
Allbasinsupdate.desc.shx
Once renamed they need to be copied to the sedmap servers:
cida-eros-sedmapdev.er.usgs.gov
cida-eros-sedmapqa.er.usgs.gov
cida-eros-sedmapprod.er.usgs.gov
Place the files in /opt/tomcat/geoserver/data/Allbasinsupdate.desc/ overwriting any files in the directory.
Since we are replacing files of the same name we need to remove a Geoserver generated file called Allbasinsupdate.desc.qix located in that same directory (/opt/tomcat/geoserver/data/Allbasinsupdate.desc/).  Once Geoserver is restarted, this file is regenerated for the new shapefiles.
Restart Geoserver via restarting tomcat:
> tomcat restart default

Checking Data Dot Size in UI:
As mentioned in JIRA ticket NSM-260 (https://internal.cida.usgs.gov/jira/browse/NSM-260) the Data Dot Sizes need to me manually manipulated based on the amount of data in the database.  There is no algorithmic way to do this with Geoserver so the SQL for the _discrete layer needs to be updated based on the visual representation of the Dot Size.
To change the SQL for this layer:
Log into Geoserver as admin
http://cida-eros-sedmapdev.er.usgs.gov:8080/geoserver
http://cida-eros-sedmapqa.er.usgs.gov:8080/geoserver
http://cida-eros-sedmapprod.er.usgs.gov:8080/geoserver
On the left side, click "Layers".
In the "Layer Name" column, click the "_discrete" link.
Scroll to the bottom of this page and under the "Property" fields click the "Edit sql view" link
Modify the "SQL Statement" to reflect the changes
Simple tests after the upload to make sure the update worked:
Visual inspection:
Are the discrete and daily points layers loading?
Are the point sizes the same as production?
Are all spatial layers loading?
Is the NLCD legend loading?
Data filtering
Apply a state filter, are point filtering by that state?
Apply a Basin boundary files (06130500, 07263620). Are sites filtered to just those in the basin?
Apply a year range filter
Apply a couple site characteristic filters, make sure they work.
Pick a daily data site, click on it and record the number of years of daily data available. Use that value as the min years of daily data filter. Make sure this site is on the map when the filter is applied. 
Data download
Apply a few filters, download the discrete data only. Compare to what is in production. They may not be exact due to data refresh, but are they close?
Apply a few filters, download daily data only. Compare to what is in production. They should be the same or very close.
Apply a few filters, download site attribute info only. Compare to production. They should be the same or very close. 
Try to download data in both .tsv and .cvs format.
Apply filers and select the option to include daily flow with the discrete data. Do you get the daily flow data? How does it compare to production?

