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

--changeset duselman:prefixHuc6With0
update site_ref set HUC_6='0'||huc_6
where length(huc_6)=5;
-- we could write a rollback that removes the leading 0

--changeset duselman:prefixHuc4With0
update site_ref set HUC_4='0'||huc_4
where length(huc_4)=3;
-- we could write a rollback that removes the leading 0

--changeset duselman:prefixHuc2With0
update site_ref set HUC_2='0'||huc_2
where length(huc_2)=1;
-- we could write a rollback that removes the leading 0


--changeset duselman:upperCaseSiteName
update site_ref set sname = upper(sname);


--changeset duselman:updateMissingStates-GU-PR-VI
Update site_ref Set State = 'GU' where site_no = '16809600';
Update site_ref Set State = 'GU' where site_no = '16854500'; 
Update site_ref Set State = 'GU' where site_no = '16858000';
Update site_ref Set State = 'PR' where huc_basin_name = 'Puerto Rico';
Update site_ref Set State = 'VI' where site_no = '50292600';
Update site_ref Set State = 'VI' where site_no = '50294000';


--changeset duselman:updateMissingSNAME
Update site_ref Set sname ='YANTIC RIVER AT YANTIC, CT.' where site_no ='01127500';
Update site_ref Set sname ='SCANTIC R AT BROAD BROOK, CT.' where site_no ='01184500';
Update site_ref Set sname ='Schuylkill River at Pottsville, PA' where site_no ='01467500';
Update site_ref Set sname ='West Branch Schuylkill River at Cressona, PA' where site_no ='01467950';
Update site_ref Set sname ='Schuylkill River at Landingville, PA' where site_no ='01468500';
Update site_ref Set sname ='Schuylkill River at Auburn, PA' where site_no ='01469000';
Update site_ref Set sname ='Little Schuylkill River at South Tamaqua, PA' where site_no ='01469700';
Update site_ref Set sname ='Little Schuylkill River at Drehersville, PA' where site_no ='01470000';
Update site_ref Set sname ='RED CLAY CREEK AT WOODDALE, DE' where site_no ='01480000';
Update site_ref Set sname ='Rolling Stone Run near Kylertown, PA' where site_no ='01541880';
Update site_ref Set sname ='Bald Eagle Creek bl Spring Creek at Milesburg, PA' where site_no ='01547200';
Update site_ref Set sname ='Bald Eagle Creek at Blanchard, PA' where site_no ='01547500';
Update site_ref Set sname ='Marsh Creek at Blanchard, PA' where site_no ='01547700';
Update site_ref Set sname ='Basswood Run above Hunter Drift near Antrim, PA' where site_no ='01548417';
Update site_ref Set sname ='Rattler Run near Morris, PA' where site_no ='01548422';
Update site_ref Set sname ='Blockhouse Creek near English Center, PA' where site_no ='01549500';
Update site_ref Set sname ='WB Susquehanna River at Watsontown, PA' where site_no ='01553115';
Update site_ref Set sname ='CARTER RUN NEAR MARSHALL, VA' where site_no ='01661900';
Update site_ref Set sname ='HAZEL RIVER AT RIXEYVILLE, VA' where site_no ='01663500';
Update site_ref Set sname ='JAMES RIVER AT SCOTTSVILLE, VA' where site_no ='02029000';
Update site_ref Set sname ='ROANOKE RIVER AT ALTAVISTA, VA' where site_no ='02060500';
Update site_ref Set sname ='THIRD CREEK NR STONY POINT, NC' where site_no ='02119400';
Update site_ref Set sname ='WATEREE R. BL EASTOVER, SC' where site_no ='02148315';
Update site_ref Set sname ='LAKE MOULTRIE NEAR PINOPOLIS, SC (TAILRACE)' where site_no ='02172001';
Update site_ref Set sname ='TOMS CREEK TRIB (NFBR SWS NO. 14) NR AVALON, GA' where site_no ='02190200';
Update site_ref Set sname ='SALEM F SUBWATERSHED #11A VARNER H NR SALEM, WV' where site_no ='03060000';
Update site_ref Set sname ='Indian Creek near Jones Mills, PA' where site_no ='03082020';
Update site_ref Set sname ='Champion Creek at Melcroft, PA' where site_no ='03082120';
Update site_ref Set sname ='Consol Run near Bloomingdale OH' where site_no ='03110983';
Update site_ref Set sname ='Wheeling Creek below Blaine OH' where site_no ='03111548';
Update site_ref Set sname ='Unnamed tributary to Bend Fork near Belmont OH' where site_no ='03113950';
Update site_ref Set sname ='Huff Run at Lindentree OH' where site_no ='03121800';
Update site_ref Set sname ='Mud Run at Tuscarawas OH' where site_no ='03128690';
Update site_ref Set sname ='Little Mill Creek near Coshocton OH-USGS 03139940' where site_no ='03139940';
Update site_ref Set sname ='Sugartree Fork near Birmingham OH' where site_no ='03142240';
Update site_ref Set sname ='Sand Fork near Wakatomika OH' where site_no ='03144289';
Update site_ref Set sname ='Moxahala Creek near Crooksville OH' where site_no ='03148150';
Update site_ref Set sname ='Snow Fork Monday Creek at Buchtel OH' where site_no ='03158195';
Update site_ref Set sname ='West Branch Shade River near Burlingham OH' where site_no ='03159534';
Update site_ref Set sname ='East Branch Shade River near Tuppers Plains OH' where site_no ='03159555';
Update site_ref Set sname ='ELK RIVER AT SUTTON, WV' where site_no ='03195500';
Update site_ref Set sname ='ELK RIVER AT BLUE CREEK,WV' where site_no ='03197680';
Update site_ref Set sname ='Raccoon Creek near New Plymouth OH' where site_no ='03201555';
Update site_ref Set sname ='Big Four Hollow Creek near Lake Hope OH' where site_no ='03201700';
Update site_ref Set sname ='Raccoon Creek near Bolins Mills OH' where site_no ='03201902';
Update site_ref Set sname ='Little Raccoon Creek near Ewington OH' where site_no ='03201980';
Update site_ref Set sname ='Little Raccoon Creek near VintonOh' where site_no ='03201988';
Update site_ref Set sname ='LEVISA FORK NEAR GRUNDY, VA' where site_no ='03207500';
Update site_ref Set sname ='RACCOON CREEK NEAR ZEBULON, KY' where site_no ='03210040';
Update site_ref Set sname ='Scioto River near Prospect OH' where site_no ='03219500';
Update site_ref Set sname ='Rush Run at Worthington OH' where site_no ='03226865';
Update site_ref Set sname ='Linworth Road Creek at Columbus OH' where site_no ='03226870';
Update site_ref Set sname ='Bethel Road Creek at Columbus OH' where site_no ='03226875';
Update site_ref Set sname ='Olentangy River at Henderson Rd at Columbus OH' where site_no ='03226885';
Update site_ref Set sname ='Big Walnut Creek at Central College OH' where site_no ='03228500';
Update site_ref Set sname ='Little Darby Creek at West Jefferson OH' where site_no ='03230310';
Update site_ref Set sname ='Great Miami River at Dayton OH-USGS 03270400' where site_no ='03270400';
Update site_ref Set sname ='Great Miami River at Dayton OH' where site_no ='03270500';
Update site_ref Set sname ='NOLIN RIVER AT WAX, KY' where site_no ='03310500';
Update site_ref Set sname ='GREEN RIVER AT PARADISE, KY' where site_no ='03316500';
Update site_ref Set sname ='NOLICHUCKY RIVER NEAR MORRISTOWN, TENN.' where site_no ='03467500';
Update site_ref Set sname ='FRENCH BROAD RIVER BELOW DOUGLAS DAM, TN' where site_no ='03469000';
Update site_ref Set sname ='S F HOLSTON R AT BLUFF CITY, TENN.' where site_no ='03477000';
Update site_ref Set sname ='WATAUGA RIVER AT ELIZABETHTON, TENNESSEE' where site_no ='03486000';
Update site_ref Set sname ='SOUTH FORK HOLSTON RIVER AT KINGSPORT, TENN' where site_no ='03487500';
Update site_ref Set sname ='HOLSTON RIVER NEAR JEFFERSON CITY, TENNESSEE' where site_no ='03494000';
Update site_ref Set sname ='TENNESSEE RIVER AT KNOXVILLE, TENNESSEE' where site_no ='03497000';
Update site_ref Set sname ='LITTLE TENNESSEE RIVER AT MCGHEE, TENN' where site_no ='03519500';
Update site_ref Set sname ='TENNESSEE RIVER AT LOUDON, TENN.' where site_no ='03520000';
Update site_ref Set sname ='TURTLETOWN CREEK AT TURTLETOWN, TN' where site_no ='03556000';
Update site_ref Set sname ='NORTH POTATO CREEK NEAR DUCKTOWN, TENN.' where site_no ='03561000';
Update site_ref Set sname ='BRUSH C NR DUCKTOWN TENN' where site_no ='03562000';
Update site_ref Set sname ='HIWASSEE RIVER AT CHARLESTON, TN' where site_no ='03566000';
Update site_ref Set sname ='ELK RIVER NEAR PROSPECT, TN' where site_no ='03584500';
Update site_ref Set sname ='TENN-TOM WATERWAY AT CROSS ROADS, MS' where site_no ='03592824';
Update site_ref Set sname ='TENNESSEE RIVER AT SAVANNAH, TN' where site_no ='03593500';
Update site_ref Set sname ='BUFFALO RIVER NEAR LOBELVILLE, TN' where site_no ='03604500';
Update site_ref Set sname ='BIG SANDY R AT BIG SANDY TENN' where site_no ='03607000';
Update site_ref Set sname ='TENNESSEE RIVER NEAR PADUCAH, KY' where site_no ='03609500';
Update site_ref Set sname ='GREEN CREEK AT COUNTY HWY 565 NEAR PALMER, MI' where site_no ='04058120';
Update site_ref Set sname ='EAST BRANCH ESCANABA RIVER AT GWINN, MI' where site_no ='04058500';
Update site_ref Set sname ='MICHIGAMME RIVER NEAR WITCH LAKE, MI' where site_no ='04062400';
Update site_ref Set sname ='BOWER CREEK AT SUNNYVIEW ROAD NEAR DE PERE, WI' where site_no ='04085118';
Update site_ref Set sname ='MILWAUKEE R ABOVE NORTH AVE DAM AT MILWAUKEE, WI' where site_no ='04087010';
Update site_ref Set sname ='MENOMONEE RIVER AT MILWAUKEE, WI' where site_no ='04087138';
Update site_ref Set sname ='FISH CREEK NEAR ARTIC, IN' where site_no ='04177810';
Update site_ref Set sname ='Honey Creek at Melmore OH' where site_no ='04197100';
Update site_ref Set sname ='Vermilion River near Fitchville OH' where site_no ='04199287';
Update site_ref Set sname ='East Branch Black River near Lagrange OH' where site_no ='04199750';
Update site_ref Set sname ='West Branch Black River near Oberlin OH' where site_no ='04200050';
Update site_ref Set sname ='West Branch Black River above Lake St at Elyria OH' where site_no ='04200430';
Update site_ref Set sname ='GENESEE RIVER AT PORTAGEVILLE NY' where site_no ='04223000';
Update site_ref Set sname ='WEST BROOK AT LAKE GEORGE NY' where site_no ='04276895';
Update site_ref Set sname ='ENGLISH BROOK AT LAKE GEORGE NY' where site_no ='04276920';
Update site_ref Set sname ='CROW WING RIVER AT NIMROD, MN' where site_no ='05244000';
Update site_ref Set sname ='BRUCE VALLEY CREEK NEAR PLEASANTVILLE, WI' where site_no ='05379288';
Update site_ref Set sname ='KICKAPOO RIVER AT STATE HIGHWAY 33 AT ONTARIO, WI' where site_no ='05407470';
Update site_ref Set sname ='KICKAPOO RIVER NEAR LA FARGE, WI' where site_no ='05407950';
Update site_ref Set sname ='Big Spring near Elkader, IA' where site_no ='05411950';
Update site_ref Set sname ='MISSISSIPPI R AT LOCK and DAM 12 AT BELLEVUE, IA' where site_no ='05416100';
Update site_ref Set sname ='APPLE RIVER NEAR HANOVER, IL' where site_no ='05419000';
Update site_ref Set sname ='PLUM RIVER AT SAVANNA, IL' where site_no ='05420100';
Update site_ref Set sname ='SF PHEASANT BR.-DETENTION OUTLET-AT MIDDLETON, WI' where site_no ='054279449';
Update site_ref Set sname ='North Fork Salt River near Shelbina, MO' where site_no ='05502500';
Update site_ref Set sname ='South Fork Salt River at Santa Fe, MO' where site_no ='05505000';
Update site_ref Set sname ='Youngs Creek near Mexico, MO' where site_no ='05506000';
Update site_ref Set sname ='MIDDLE FORK SALT RIVER AT DUNCANS BRIDGE MO.' where site_no ='05506190';
Update site_ref Set sname ='Elk Fork Salt River near Paris, MO' where site_no ='05507000';
Update site_ref Set sname ='GRASS LAKE OUTLET AT LOTUS WOODS, IL' where site_no ='05547350';
Update site_ref Set sname ='NIPPERSINK CREEK BELOW WONDER LAKE, IL' where site_no ='05548110';
Update site_ref Set sname ='FOX RIVER AT JOHNSBURG, IL' where site_no ='05548500';
Update site_ref Set sname ='ILLINOIS RIVER AT PEKIN, IL' where site_no ='05563800';
Update site_ref Set sname ='Big Hole River near Melrose MT' where site_no ='06025500';
Update site_ref Set sname ='DUDLEY WASTEWAY NR PAVILLION WYO' where site_no ='06247500';
Update site_ref Set sname ='DEWEY DRAIN NR PAVILLION WYO' where site_no ='06248500';
Update site_ref Set sname ='FIVEMILE 76 DRAIN NR RIVERTON WYO' where site_no ='06249000';
Update site_ref Set sname ='SAND GULCH DRAIN AND WASTEWAY NR RIVERTON WYO' where site_no ='06249500';
Update site_ref Set sname ='LOST WELLS BUTTE DRAIN NR RIVERTON WYO' where site_no ='06250500';
Update site_ref Set sname ='EAGLE DRAIN NR SHOSHONI WYO' where site_no ='06252000';
Update site_ref Set sname ='LATERAL P-34.9 WASTEWAY NR SHOSHONI WYO' where site_no ='06252500';
Update site_ref Set sname ='LATERAL P-36.8 WASTEWAY NR SHOSHONI WYO' where site_no ='06253500';
Update site_ref Set sname ='WOLF CREEK AT WOLF, WY' where site_no ='06299500';
Update site_ref Set sname ='CRAZY WOMAN CREEK NEAR ARVADA, WY' where site_no ='06316500';
Update site_ref Set sname ='CEDAR CREEK NR PRETTY ROCK, ND' where site_no ='06352500';
Update site_ref Set sname ='MOREAU R NEAR FAITH,SD' where site_no ='06359500';
Update site_ref Set sname ='KEYA PAHA R NEAR KEYAPAHA,SD' where site_no ='06464100';
Update site_ref Set sname ='BEAVER CR NR YANKTON SD' where site_no ='06478514';
Update site_ref Set sname ='West Fork Ditch at Hornick, IA' where site_no ='06602020';
Update site_ref Set sname ='Monona-Harrison Ditch near Turin, IA' where site_no ='06602400';
Update site_ref Set sname ='Soldier River at Pisgah, IA' where site_no ='06608500';
Update site_ref Set sname ='Boyer River at Deloit, IA' where site_no ='06609280';
Update site_ref Set sname ='East Boyer River at Denison, IA' where site_no ='06609350';
Update site_ref Set sname ='SLATE CREEK NEAR ATLANTIC CITY, WY' where site_no ='06637900';
Update site_ref Set sname ='NORTH PLATTE RIVER NEAR DOUGLAS, WY' where site_no ='06650000';
Update site_ref Set sname ='NORTH PLATTE RIVER NR CASSA WYO' where site_no ='06654000';
Update site_ref Set sname ='LARAMIE RIVER NEAR UVA, WY' where site_no ='06670000';
Update site_ref Set sname ='CHERRY CREEK NEAR FRANKTOWN, CO.' where site_no ='06712000';
Update site_ref Set sname ='CHERRY CREEK NEAR MELVIN, CO' where site_no ='06712500';
Update site_ref Set sname ='SOUTH PLATTE RIVER AT SUBLETTE, CO.' where site_no ='06757000';
Update site_ref Set sname ='SOUTH PLATTE RIVER AT FORT MORGAN, CO' where site_no ='06759500';
Update site_ref Set sname ='SOUTH PLATTE RIVER AT BALZAC, CO.' where site_no ='06760000';
Update site_ref Set sname ='M LOUP R AT DUNNING, NEBR. (TOTAL LOAD SECTION)' where site_no ='06775501';
Update site_ref Set sname ='Nodaway River near Burlington Junction, MO' where site_no ='06817500';
Update site_ref Set sname ='Little Platte River at Smithville, MO' where site_no ='06821150';
Update site_ref Set sname ='SF REPUBLICAN R NR CO-KS ST LINE, KS' where site_no ='06827000';
Update site_ref Set sname ='ENDERS RESERVOIR NEAR ENDERS, NEBR.' where site_no ='06832000';
Update site_ref Set sname ='SAPPA C NR OBERLIN, KS' where site_no ='06845000';
Update site_ref Set sname ='REPUBLICAN R AT SCANDIA, KS' where site_no ='06854500';
Update site_ref Set sname ='BUFFALO C NR JAMESTOWN, KS' where site_no ='06855800';
Update site_ref Set sname ='REPUBLICAN R AT MILFORD, KS' where site_no ='06857000';
Update site_ref Set sname ='SMOKY HILL R NR ELLIS, KS' where site_no ='06862500';
Update site_ref Set sname ='BIG C NR OGALLAH, KS' where site_no ='06863300';
Update site_ref Set sname ='SMOKY HILL R AT LINDSBORG, KS' where site_no ='06866000';
Update site_ref Set sname ='PARADISE C NR PARADISE, KS' where site_no ='06867500';
Update site_ref Set sname ='SALINE R AT WILSON DAM, KS' where site_no ='06868200';
Update site_ref Set sname ='WOLF C NR SYLVAN GROVE, KS' where site_no ='06868500';
Update site_ref Set sname ='NF SOLOMON R AT KIRWIN, KS' where site_no ='06871800';
Update site_ref Set sname ='SF SOLOMON R AT ALTON, KS' where site_no ='06873500';
Update site_ref Set sname ='SOLOMON R AT BELOIT, KS' where site_no ='06876000';
Update site_ref Set sname ='TURKEY C NR ABILENE, KS' where site_no ='06877500';
Update site_ref Set sname ='LYON C NR WOODBINE, KS' where site_no ='06878500';
Update site_ref Set sname ='BIG BLUE R AT RANDOLPH, KS' where site_no ='06886000';
Update site_ref Set sname ='DELAWARE R AT VALLEY FALLS, KS' where site_no ='06890500';
Update site_ref Set sname ='LITTLE BLUE R. AT LONGVIEW ROAD IN KANS. CITY, M' where site_no ='06893790';
Update site_ref Set sname ='Grand River near Gallatin, MO' where site_no ='06897500';
Update site_ref Set sname ='Thompson River at Mount Moriah, MO' where site_no ='06898100';
Update site_ref Set sname ='Weldon River at Mill Grove, MO' where site_no ='06899000';
Update site_ref Set sname ='Thompson River at Trenton, MO' where site_no ='06899500';
Update site_ref Set sname ='Shoal Creek near Braymer, MO' where site_no ='06899700';
Update site_ref Set sname ='Medicine Creek near Galt, MO' where site_no ='06900000';
Update site_ref Set sname ='West Yellow Creek near Brookfield, MO' where site_no ='06902200';
Update site_ref Set sname ='Mussel Fork near Musselfork, MO' where site_no ='06906000';
Update site_ref Set sname ='Long Branch Creek near Atlanta, MO' where site_no ='06906150';
Update site_ref Set sname ='Blackwater River at Blue Lick, MO' where site_no ='06908000';
Update site_ref Set sname ='Moreau River near Jefferson City, MO' where site_no ='06910750';
Update site_ref Set sname ='HUNDRED AND TEN MILE C NR QUENEMO, KS' where site_no ='06912500';
Update site_ref Set sname ='MARMATON R NR FORT SCOTT, KS' where site_no ='06917500';
Update site_ref Set sname ='Sac River near Stockton, MO' where site_no ='06919000';
Update site_ref Set sname ='Cedar Creek near Pleasant View, MO' where site_no ='06919500';
Update site_ref Set sname ='Osage River at Osceola, MO' where site_no ='06920500';
Update site_ref Set sname ='Pomme de Terre River at Hermitage, MO' where site_no ='06921500';
Update site_ref Set sname ='Big Creek near Blairstown, MO' where site_no ='06921720';
Update site_ref Set sname ='South Grand River near Clinton, MO' where site_no ='06921760';
Update site_ref Set sname ='South Grand River near Brownington, MO' where site_no ='06922000';
Update site_ref Set sname ='Osage Fork Gasconade River at Drynob, MO' where site_no ='06927800';
Update site_ref Set sname ='Little Piney Creek at Newburg, MO' where site_no ='06932000';
Update site_ref Set sname ='SALINE CREEK NEAR MINNITH MO' where site_no ='07020270';
Update site_ref Set sname ='St. Francis River near Saco, MO' where site_no ='07036100';
Update site_ref Set sname ='Little Black River near Grandin, MO' where site_no ='07068380';
Update site_ref Set sname ='Little Black River below Fairdealing, MO' where site_no ='07068510';
Update site_ref Set sname ='Logan Creek at Oxly, MO' where site_no ='07068540';
Update site_ref Set sname ='Little Black River at Success, AR' where site_no ='07068600';
Update site_ref Set sname ='RED CREEK BELOW SULLIVAN PARK AT FORT CARSON, CO' where site_no ='07099080';
Update site_ref Set sname ='ARK RIVER AT OZARK DAM AT OZARK, ARK.' where site_no ='07252406';
Update site_ref Set sname ='Arkansas River at Dardanelle, AR' where site_no ='07258000';
Update site_ref Set sname ='Arkansas River at Murray Dam near Little Rock, AR' where site_no ='07263450';
Update site_ref Set sname ='YALOBUSHA R AND TOPASHAW C CA AT CALHOUN CITY, MS' where site_no ='07282000';
Update site_ref Set sname ='TOPASHAW CREEK CANAL NR CALHOUN CITY, MS' where site_no ='07282100';
Update site_ref Set sname ='Mulberry Ck nr Brice, TX' where site_no ='07299000';
Update site_ref Set sname ='Beaver Creek near Waurika, OK' where site_no ='07313500';
Update site_ref Set sname ='Big Sandy Ck nr Big Sandy, TX' where site_no ='08019500';
Update site_ref Set sname ='Elm Fk Trinity SWS No. 6-0 nr Muenster, TX' where site_no ='08050200';
Update site_ref Set sname ='Colorado Rv at Robert Lee, TX' where site_no ='08124000';
Update site_ref Set sname ='Nueces Rv nr Tilden, TX' where site_no ='08194500';
Update site_ref Set sname ='RIO CHAMA NEAR ABIQUIU, NM' where site_no ='08287500';
Update site_ref Set sname ='RIO GRANDE NR BERNARDO, NM' where site_no ='08332000';
Update site_ref Set sname ='RIO PUERCO AT RIO PUERCO, NM' where site_no ='08352500';
Update site_ref Set sname ='RIO GRANDE AT SAN ACACIA, NM' where site_no ='08354400';
Update site_ref Set sname ='RIO GRANDE AT SAN MARCIAL, NM' where site_no ='08358500';
Update site_ref Set sname ='SAN JUAN R NR BLANCO, NM' where site_no ='09356500';
Update site_ref Set sname ='RIO NUTRIA ABV RES NO. 3 NR LOWER NURTIA, NM' where site_no ='09386915';
Update site_ref Set sname ='SPILLWAY CHANNEL BLW RES NO 3 NR LOWER NUTRIA, NM' where site_no ='09386919';
Update site_ref Set sname ='COTTONWOOD WASH AT SNOWFLAKE, AZ.' where site_no ='09393400';
Update site_ref Set sname ='MOENKOPI WASH NR MOENKOPI, ARIZ.' where site_no ='09401250';
Update site_ref Set sname ='COLORADO RIVER NR TOPOCK, AZ.' where site_no ='09424000';
Update site_ref Set sname ='SAN SIMON WASH NEAR PISINIMO, AZ.' where site_no ='09535100';
Update site_ref Set sname ='SAN DIEGO R A FASHION VALLEY AT SAN DIEGO CA' where site_no ='11023000';
Update site_ref Set sname ='SAN ANTONIO R A PLEYTO CA' where site_no ='11150000';
Update site_ref Set sname ='RESULTS OF COMBINING 11-3758.1 + 11-3758.7 CA' where site_no ='11375871';
Update site_ref Set sname ='SACRAMENTO R NR RED BLUFF CA' where site_no ='11378000';
Update site_ref Set sname ='DEER C A RED BRIDGE NR VINA CA' where site_no ='11383600';
Update site_ref Set sname ='CACHE C TOTAL FLOW FROM SETTLING BAS NR WOODLD CA' where site_no ='11452901';
Update site_ref Set sname ='NAPA R NR ST HELENA CA' where site_no ='11456000';
Update site_ref Set sname ='NORTH RIVER ABOVE JOE CREEK, NEAR RAYMOND, WA' where site_no ='12016600';
Update site_ref Set sname ='SKOOKUMCHUCK RIVER NEAR BUCODA, WA' where site_no ='12026400';
Update site_ref Set sname ='WYNOOCHEE RIVER NEAR GRISDALE, WA' where site_no ='12035400';
Update site_ref Set sname ='HOH RIVER AT US HIGHWAY 101 NEAR FORKS, WA' where site_no ='12041200';
Update site_ref Set sname ='DUNGENESS RIVER AT DUNGENESS, WA' where site_no ='12049000';
Update site_ref Set sname ='WHITE RIVER FLUME NEAR BUCKLEY, WA' where site_no ='12098910';
Update site_ref Set sname ='SKYKOMISH RIVER AT MONROE, WA' where site_no ='12141100';
Update site_ref Set sname ='SNOQUALMIE RIVER NEAR CARNATION, WA' where site_no ='12149000';
Update site_ref Set sname ='HANGMAN CREEK AT SPOKANE, WA' where site_no ='12424000';
Update site_ref Set sname ='NORTH FORK TETON RIVER AT TETON ID' where site_no ='13055198';
Update site_ref Set sname ='NF TETON RIVER AT AUX BRDG SITE NR TETON ID' where site_no ='13055210';
Update site_ref Set sname ='NF TETON RIVER AT POWERLINE RD NR TETON ID' where site_no ='13055230';
Update site_ref Set sname ='NF TETON RIVER NR SUGAR CITY ID' where site_no ='13055250';
Update site_ref Set sname ='NF TETON RIVER AT HWY BRIDGE NR SALEM ID' where site_no ='13055270';
Update site_ref Set sname ='NF TETON RIVER AT LAST BRIDGE NR SALEM ID' where site_no ='13055300';
Update site_ref Set sname ='POWDER RIVER AT BAKER CITY, OR' where site_no ='13277000';
Update site_ref Set sname ='COLUMBIA RIVER AT MCNARY DAM, NEAR UMATILA, OR' where site_no ='14019200';
Update site_ref Set sname ='CROOKED RIVER NEAR PRINEVILLE, OR' where site_no ='14080500';
Update site_ref Set sname ='YAQUINA RIVER NEAR CHITWOOD,OREG.' where site_no ='14306030';
Update site_ref Set sname ='SIXES RIVER AT SIXES, OREG.' where site_no ='14327150';
Update site_ref Set sname ='QUEBRADA JOSEFINA AT PINERO AVENUE, PR' where site_no ='50049310';
Update site_ref Set sname ='RIO GRANDE DE LOIZA AT QUEBRADA ARENAS, PR' where site_no ='50050900';
Update site_ref Set sname ='QUEBRADA CAIMITO NR JUNCOS, PR' where site_no ='50055650';
Update site_ref Set sname ='QUEBRADA MAMEY NR GURABO, PR' where site_no ='50056900';
Update site_ref Set sname ='LAMESHUR BAY GUT AT LAMESHUR BAY, ST. JOHN  USVI' where site_no ='50292600';
Update site_ref Set sname ='FISH BAY GUT AT FISH BAY, ST. JOHN USVI' where site_no ='50294000';
Update site_ref Set sname ='PECOS RIVER AT PUERTO DE LUNA, NM' where site_no ='08383400';

