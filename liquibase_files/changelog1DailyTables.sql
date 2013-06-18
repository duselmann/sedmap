--liquibase formatted sql

--This is for the sedmap schema
 
--changeset ajmccart:CreateDailySites
CREATE TABLE SM_DAILY_SITES
(
  USGS_STATION_ID  VARCHAR2(15 BYTE)
);

ALTER TABLE SM_DAILY_SITES ADD 
  CONSTRAINT SM_DAILY_SITE_FACT_PK
 PRIMARY KEY
 (USGS_STATION_ID);
 
 ALTER TABLE SM_DAILY_SITES ADD (
  CONSTRAINT SM_DAILY_SITE_FACT_R01 
 FOREIGN KEY (USGS_STATION_ID) 
 REFERENCES SM_SITE_REF (USGS_STATION_ID));
 
 GRANT SELECT ON SM_DAILY_SITES TO SEDUSER;
 --rollback drop table sm_daily_sites;
 
 --changeset ajmccart:CreateDailyYears
 CREATE TABLE SM_DAILY_YEARS
(
  USGS_STATION_ID         VARCHAR2(15 BYTE),
  NUM_UNIQUE_YEARS        NUMBER,
  YEAR_1_DS_STATION       NUMBER,
  YEAR_RECENT_DS_STATION  NUMBER,
  "1900"                  NUMBER,
  "1901"                  NUMBER,
  "1902"                  NUMBER,
  "1903"                  NUMBER,
  "1904"                  NUMBER,
  "1905"                  NUMBER,
  "1906"                  NUMBER,
  "1907"                  NUMBER,
  "1908"                  NUMBER,
  "1909"                  NUMBER,
  "1910"                  NUMBER,
  "1911"                  NUMBER,
  "1912"                  NUMBER,
  "1913"                  NUMBER,
  "1914"                  NUMBER,
  "1915"                  NUMBER,
  "1916"                  NUMBER,
  "1917"                  NUMBER,
  "1918"                  NUMBER,
  "1919"                  NUMBER,
  "1920"                  NUMBER,
  "1921"                  NUMBER,
  "1922"                  NUMBER,
  "1923"                  NUMBER,
  "1924"                  NUMBER,
  "1925"                  NUMBER,
  "1926"                  NUMBER,
  "1927"                  NUMBER,
  "1928"                  NUMBER,
  "1929"                  NUMBER,
  "1930"                  NUMBER,
  "1931"                  NUMBER,
  "1932"                  NUMBER,
  "1933"                  NUMBER,
  "1934"                  NUMBER,
  "1935"                  NUMBER,
  "1936"                  NUMBER,
  "1937"                  NUMBER,
  "1938"                  NUMBER,
  "1939"                  NUMBER,
  "1940"                  NUMBER,
  "1941"                  NUMBER,
  "1942"                  NUMBER,
  "1943"                  NUMBER,
  "1944"                  NUMBER,
  "1945"                  NUMBER,
  "1946"                  NUMBER,
  "1947"                  NUMBER,
  "1948"                  NUMBER,
  "1949"                  NUMBER,
  "1950"                  NUMBER,
  "1951"                  NUMBER,
  "1952"                  NUMBER,
  "1953"                  NUMBER,
  "1954"                  NUMBER,
  "1955"                  NUMBER,
  "1956"                  NUMBER,
  "1957"                  NUMBER,
  "1958"                  NUMBER,
  "1959"                  NUMBER,
  "1960"                  NUMBER,
  "1961"                  NUMBER,
  "1962"                  NUMBER,
  "1963"                  NUMBER,
  "1964"                  NUMBER,
  "1965"                  NUMBER,
  "1966"                  NUMBER,
  "1967"                  NUMBER,
  "1968"                  NUMBER,
  "1969"                  NUMBER,
  "1970"                  NUMBER,
  "1971"                  NUMBER,
  "1972"                  NUMBER,
  "1973"                  NUMBER,
  "1974"                  NUMBER,
  "1975"                  NUMBER,
  "1976"                  NUMBER,
  "1977"                  NUMBER,
  "1978"                  NUMBER,
  "1979"                  NUMBER,
  "1980"                  NUMBER,
  "1981"                  NUMBER,
  "1982"                  NUMBER,
  "1983"                  NUMBER,
  "1984"                  NUMBER,
  "1985"                  NUMBER,
  "1986"                  NUMBER,
  "1987"                  NUMBER,
  "1988"                  NUMBER,
  "1989"                  NUMBER,
  "1990"                  NUMBER,
  "1991"                  NUMBER,
  "1992"                  NUMBER,
  "1993"                  NUMBER,
  "1994"                  NUMBER,
  "1995"                  NUMBER,
  "1996"                  NUMBER,
  "1997"                  NUMBER,
  "1998"                  NUMBER,
  "1999"                  NUMBER,
  "2000"                  NUMBER,
  "2001"                  NUMBER,
  "2002"                  NUMBER,
  "2003"                  NUMBER,
  "2004"                  NUMBER,
  "2005"                  NUMBER,
  "2006"                  NUMBER,
  "2007"                  NUMBER,
  "2008"                  NUMBER,
  "2009"                  NUMBER,
  "2010"                  NUMBER,
  "2011"                  NUMBER,
  "2012"                  NUMBER
);

ALTER TABLE SM_DAILY_YEARS ADD 
  CONSTRAINT SM_DAILY_YRS_FACT_PK
 PRIMARY KEY
 (USGS_STATION_ID);
 
 ALTER TABLE SM_DAILY_YEARS ADD (
  CONSTRAINT SM_DAILY_YRS_FACT_R01 
 FOREIGN KEY (USGS_STATION_ID) 
 REFERENCES SM_DAILY_SITES (USGS_STATION_ID));

GRANT SELECT ON SM_DAILY_YEARS TO SEDUSER;
--rollback drop table sm_daily_years;