<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">
        
<body>
    <div id="singleSiteInfo" class="singleSiteInfo">
      <div class="siteDatum">
        <div class="siteInfoLabel">Station</div>              <div class="siteInfoData" id="STATION_NAME"></div>
      </div>
      <div class="siteDatum">
        <div class="siteInfoLabel">USGS Stream Gage ID</div>                   <div class="siteInfoData" id="USGS_STATION_ID"></div>
      </div>
      <div class="siteDatum">
        <div class="siteInfoLabel">Drainage Area (mi<sup>2</sup>)</div>        <div class="siteInfoData" id="DA"></div>
      </div>
      <div class="siteDatum">
        <div class="siteInfoLabel">Period of Record</div>
            <div class="siteInfoLabel siteInfoSubLabel siteInfoDAILY">daily</div>    <div class="siteInfoData siteInfoDAILY"  id="DAILY_PERIOD"></div>
            <div class="siteInfoLabel siteInfoSubLabel siteInfoDISCRETE">discrete</div>    <div class="siteInfoData siteInfoDISCRETE"  id="DISCRETE_PERIOD"></div>
      </div>
      <div class="siteDatum">
        <div class="siteInfoLabel siteInfoDISCRETE">Number of Samples</div>
            <div class="siteInfoLabel siteInfoSubLabel siteInfoDISCRETE">discrete</div>    <div class="siteInfoData siteInfoDISCRETE"  id="DISCRETE_SAMPLES"></div>
      </div>
      <div class="siteDatum">
        <div class="siteInfoLabel siteInfoDAILY">Years Data Sampled</div>
            <div class="siteInfoLabel siteInfoSubLabel siteInfoDAILY">daily</div>  <div class="siteInfoData siteInfoDAILY" id="DAILY_YEARS"></div>
      </div>
    
    </div>
</body>
    
</html>
