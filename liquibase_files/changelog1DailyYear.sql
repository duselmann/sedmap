--liquibase formatted sql

--This is for the sedmap schema

--changeset duselman:CreateDailyYearTable
create table sm_daily_year (
  USGS_STATION_ID varchar2(15),
  SAMPLE_YEAR number(4)
);
alter table sm_daily_year add constraint 
sm_daily_year_pk primary key(USGS_STATION_ID,SAMPLE_YEAR);


--changeset duselman:PopulateDailyYearTableFromDailyYears
insert into sm_daily_year select USGS_STATION_ID,1900 from sm_daily_years yrs where yrs."1900"=1;
insert into sm_daily_year select USGS_STATION_ID,1901 from sm_daily_years yrs where yrs."1901"=1;
insert into sm_daily_year select USGS_STATION_ID,1902 from sm_daily_years yrs where yrs."1902"=1;
insert into sm_daily_year select USGS_STATION_ID,1903 from sm_daily_years yrs where yrs."1903"=1;
insert into sm_daily_year select USGS_STATION_ID,1904 from sm_daily_years yrs where yrs."1904"=1;
insert into sm_daily_year select USGS_STATION_ID,1905 from sm_daily_years yrs where yrs."1905"=1;
insert into sm_daily_year select USGS_STATION_ID,1906 from sm_daily_years yrs where yrs."1906"=1;
insert into sm_daily_year select USGS_STATION_ID,1907 from sm_daily_years yrs where yrs."1907"=1;
insert into sm_daily_year select USGS_STATION_ID,1908 from sm_daily_years yrs where yrs."1908"=1;
insert into sm_daily_year select USGS_STATION_ID,1909 from sm_daily_years yrs where yrs."1909"=1;
insert into sm_daily_year select USGS_STATION_ID,1910 from sm_daily_years yrs where yrs."1910"=1;
insert into sm_daily_year select USGS_STATION_ID,1911 from sm_daily_years yrs where yrs."1911"=1;
insert into sm_daily_year select USGS_STATION_ID,1912 from sm_daily_years yrs where yrs."1912"=1;
insert into sm_daily_year select USGS_STATION_ID,1913 from sm_daily_years yrs where yrs."1913"=1;
insert into sm_daily_year select USGS_STATION_ID,1914 from sm_daily_years yrs where yrs."1914"=1;
insert into sm_daily_year select USGS_STATION_ID,1915 from sm_daily_years yrs where yrs."1915"=1;
insert into sm_daily_year select USGS_STATION_ID,1916 from sm_daily_years yrs where yrs."1916"=1;
insert into sm_daily_year select USGS_STATION_ID,1917 from sm_daily_years yrs where yrs."1917"=1;
insert into sm_daily_year select USGS_STATION_ID,1918 from sm_daily_years yrs where yrs."1918"=1;
insert into sm_daily_year select USGS_STATION_ID,1919 from sm_daily_years yrs where yrs."1919"=1;
insert into sm_daily_year select USGS_STATION_ID,1920 from sm_daily_years yrs where yrs."1920"=1;
insert into sm_daily_year select USGS_STATION_ID,1921 from sm_daily_years yrs where yrs."1921"=1;
insert into sm_daily_year select USGS_STATION_ID,1922 from sm_daily_years yrs where yrs."1922"=1;
insert into sm_daily_year select USGS_STATION_ID,1923 from sm_daily_years yrs where yrs."1923"=1;
insert into sm_daily_year select USGS_STATION_ID,1924 from sm_daily_years yrs where yrs."1924"=1;
insert into sm_daily_year select USGS_STATION_ID,1925 from sm_daily_years yrs where yrs."1925"=1;
insert into sm_daily_year select USGS_STATION_ID,1926 from sm_daily_years yrs where yrs."1926"=1;
insert into sm_daily_year select USGS_STATION_ID,1927 from sm_daily_years yrs where yrs."1927"=1;
insert into sm_daily_year select USGS_STATION_ID,1928 from sm_daily_years yrs where yrs."1928"=1;
insert into sm_daily_year select USGS_STATION_ID,1929 from sm_daily_years yrs where yrs."1929"=1;
insert into sm_daily_year select USGS_STATION_ID,1930 from sm_daily_years yrs where yrs."1930"=1;
insert into sm_daily_year select USGS_STATION_ID,1931 from sm_daily_years yrs where yrs."1931"=1;
insert into sm_daily_year select USGS_STATION_ID,1932 from sm_daily_years yrs where yrs."1932"=1;
insert into sm_daily_year select USGS_STATION_ID,1933 from sm_daily_years yrs where yrs."1933"=1;
insert into sm_daily_year select USGS_STATION_ID,1934 from sm_daily_years yrs where yrs."1934"=1;
insert into sm_daily_year select USGS_STATION_ID,1935 from sm_daily_years yrs where yrs."1935"=1;
insert into sm_daily_year select USGS_STATION_ID,1936 from sm_daily_years yrs where yrs."1936"=1;
insert into sm_daily_year select USGS_STATION_ID,1937 from sm_daily_years yrs where yrs."1937"=1;
insert into sm_daily_year select USGS_STATION_ID,1938 from sm_daily_years yrs where yrs."1938"=1;
insert into sm_daily_year select USGS_STATION_ID,1939 from sm_daily_years yrs where yrs."1939"=1;
insert into sm_daily_year select USGS_STATION_ID,1940 from sm_daily_years yrs where yrs."1940"=1;
insert into sm_daily_year select USGS_STATION_ID,1941 from sm_daily_years yrs where yrs."1941"=1;
insert into sm_daily_year select USGS_STATION_ID,1942 from sm_daily_years yrs where yrs."1942"=1;
insert into sm_daily_year select USGS_STATION_ID,1943 from sm_daily_years yrs where yrs."1943"=1;
insert into sm_daily_year select USGS_STATION_ID,1944 from sm_daily_years yrs where yrs."1944"=1;
insert into sm_daily_year select USGS_STATION_ID,1945 from sm_daily_years yrs where yrs."1945"=1;
insert into sm_daily_year select USGS_STATION_ID,1946 from sm_daily_years yrs where yrs."1946"=1;
insert into sm_daily_year select USGS_STATION_ID,1947 from sm_daily_years yrs where yrs."1947"=1;
insert into sm_daily_year select USGS_STATION_ID,1948 from sm_daily_years yrs where yrs."1948"=1;
insert into sm_daily_year select USGS_STATION_ID,1949 from sm_daily_years yrs where yrs."1949"=1;
insert into sm_daily_year select USGS_STATION_ID,1950 from sm_daily_years yrs where yrs."1950"=1;
insert into sm_daily_year select USGS_STATION_ID,1951 from sm_daily_years yrs where yrs."1951"=1;
insert into sm_daily_year select USGS_STATION_ID,1952 from sm_daily_years yrs where yrs."1952"=1;
insert into sm_daily_year select USGS_STATION_ID,1953 from sm_daily_years yrs where yrs."1953"=1;
insert into sm_daily_year select USGS_STATION_ID,1954 from sm_daily_years yrs where yrs."1954"=1;
insert into sm_daily_year select USGS_STATION_ID,1955 from sm_daily_years yrs where yrs."1955"=1;
insert into sm_daily_year select USGS_STATION_ID,1956 from sm_daily_years yrs where yrs."1956"=1;
insert into sm_daily_year select USGS_STATION_ID,1957 from sm_daily_years yrs where yrs."1957"=1;
insert into sm_daily_year select USGS_STATION_ID,1958 from sm_daily_years yrs where yrs."1958"=1;
insert into sm_daily_year select USGS_STATION_ID,1959 from sm_daily_years yrs where yrs."1959"=1;
insert into sm_daily_year select USGS_STATION_ID,1960 from sm_daily_years yrs where yrs."1960"=1;
insert into sm_daily_year select USGS_STATION_ID,1961 from sm_daily_years yrs where yrs."1961"=1;
insert into sm_daily_year select USGS_STATION_ID,1962 from sm_daily_years yrs where yrs."1962"=1;
insert into sm_daily_year select USGS_STATION_ID,1963 from sm_daily_years yrs where yrs."1963"=1;
insert into sm_daily_year select USGS_STATION_ID,1964 from sm_daily_years yrs where yrs."1964"=1;
insert into sm_daily_year select USGS_STATION_ID,1965 from sm_daily_years yrs where yrs."1965"=1;
insert into sm_daily_year select USGS_STATION_ID,1966 from sm_daily_years yrs where yrs."1966"=1;
insert into sm_daily_year select USGS_STATION_ID,1967 from sm_daily_years yrs where yrs."1967"=1;
insert into sm_daily_year select USGS_STATION_ID,1968 from sm_daily_years yrs where yrs."1968"=1;
insert into sm_daily_year select USGS_STATION_ID,1969 from sm_daily_years yrs where yrs."1969"=1;
insert into sm_daily_year select USGS_STATION_ID,1970 from sm_daily_years yrs where yrs."1970"=1;
insert into sm_daily_year select USGS_STATION_ID,1971 from sm_daily_years yrs where yrs."1971"=1;
insert into sm_daily_year select USGS_STATION_ID,1972 from sm_daily_years yrs where yrs."1972"=1;
insert into sm_daily_year select USGS_STATION_ID,1973 from sm_daily_years yrs where yrs."1973"=1;
insert into sm_daily_year select USGS_STATION_ID,1974 from sm_daily_years yrs where yrs."1974"=1;
insert into sm_daily_year select USGS_STATION_ID,1975 from sm_daily_years yrs where yrs."1975"=1;
insert into sm_daily_year select USGS_STATION_ID,1976 from sm_daily_years yrs where yrs."1976"=1;
insert into sm_daily_year select USGS_STATION_ID,1977 from sm_daily_years yrs where yrs."1977"=1;
insert into sm_daily_year select USGS_STATION_ID,1978 from sm_daily_years yrs where yrs."1978"=1;
insert into sm_daily_year select USGS_STATION_ID,1979 from sm_daily_years yrs where yrs."1979"=1;
insert into sm_daily_year select USGS_STATION_ID,1980 from sm_daily_years yrs where yrs."1980"=1;
insert into sm_daily_year select USGS_STATION_ID,1981 from sm_daily_years yrs where yrs."1981"=1;
insert into sm_daily_year select USGS_STATION_ID,1982 from sm_daily_years yrs where yrs."1982"=1;
insert into sm_daily_year select USGS_STATION_ID,1983 from sm_daily_years yrs where yrs."1983"=1;
insert into sm_daily_year select USGS_STATION_ID,1984 from sm_daily_years yrs where yrs."1984"=1;
insert into sm_daily_year select USGS_STATION_ID,1985 from sm_daily_years yrs where yrs."1985"=1;
insert into sm_daily_year select USGS_STATION_ID,1986 from sm_daily_years yrs where yrs."1986"=1;
insert into sm_daily_year select USGS_STATION_ID,1987 from sm_daily_years yrs where yrs."1987"=1;
insert into sm_daily_year select USGS_STATION_ID,1988 from sm_daily_years yrs where yrs."1988"=1;
insert into sm_daily_year select USGS_STATION_ID,1989 from sm_daily_years yrs where yrs."1989"=1;
insert into sm_daily_year select USGS_STATION_ID,1990 from sm_daily_years yrs where yrs."1990"=1;
insert into sm_daily_year select USGS_STATION_ID,1991 from sm_daily_years yrs where yrs."1991"=1;
insert into sm_daily_year select USGS_STATION_ID,1992 from sm_daily_years yrs where yrs."1992"=1;
insert into sm_daily_year select USGS_STATION_ID,1993 from sm_daily_years yrs where yrs."1993"=1;
insert into sm_daily_year select USGS_STATION_ID,1994 from sm_daily_years yrs where yrs."1994"=1;
insert into sm_daily_year select USGS_STATION_ID,1995 from sm_daily_years yrs where yrs."1995"=1;
insert into sm_daily_year select USGS_STATION_ID,1996 from sm_daily_years yrs where yrs."1996"=1;
insert into sm_daily_year select USGS_STATION_ID,1997 from sm_daily_years yrs where yrs."1997"=1;
insert into sm_daily_year select USGS_STATION_ID,1998 from sm_daily_years yrs where yrs."1998"=1;
insert into sm_daily_year select USGS_STATION_ID,1999 from sm_daily_years yrs where yrs."1999"=1;
insert into sm_daily_year select USGS_STATION_ID,2000 from sm_daily_years yrs where yrs."2000"=1;
insert into sm_daily_year select USGS_STATION_ID,2001 from sm_daily_years yrs where yrs."2001"=1;
insert into sm_daily_year select USGS_STATION_ID,2002 from sm_daily_years yrs where yrs."2002"=1;
insert into sm_daily_year select USGS_STATION_ID,2003 from sm_daily_years yrs where yrs."2003"=1;
insert into sm_daily_year select USGS_STATION_ID,2004 from sm_daily_years yrs where yrs."2004"=1;
insert into sm_daily_year select USGS_STATION_ID,2005 from sm_daily_years yrs where yrs."2005"=1;
insert into sm_daily_year select USGS_STATION_ID,2006 from sm_daily_years yrs where yrs."2006"=1;
insert into sm_daily_year select USGS_STATION_ID,2007 from sm_daily_years yrs where yrs."2007"=1;
insert into sm_daily_year select USGS_STATION_ID,2008 from sm_daily_years yrs where yrs."2008"=1;
insert into sm_daily_year select USGS_STATION_ID,2009 from sm_daily_years yrs where yrs."2009"=1;
insert into sm_daily_year select USGS_STATION_ID,2010 from sm_daily_years yrs where yrs."2010"=1;
insert into sm_daily_year select USGS_STATION_ID,2011 from sm_daily_years yrs where yrs."2011"=1;
insert into sm_daily_year select USGS_STATION_ID,2012 from sm_daily_years yrs where yrs."2012"=1;
