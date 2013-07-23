<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="gov.usgs.cida.config.DynamicReadOnlyProperties"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%!    
    protected DynamicReadOnlyProperties props = new DynamicReadOnlyProperties();

    {
        try {
            props.addJNDIContexts(new String[0]);
        } catch (Exception e) {
            LoggerFactory.getLogger("index.jsp").error("Could not find JNDI - Application will probably not function correctly");
        }
    }
    boolean development = Boolean.parseBoolean(props.getProperty("development"));
%>

<html lang="en">
    
    <head>
        <META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE" />
        <META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE" />
        <META HTTP-EQUIV="EXPIRES" CONTENT="0" />
        <META HTTP-EQUIV="CONTENT-LANGUAGE" CONTENT="en-US" />
        <META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width">
        <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
        <link rel="icon" href="favicon.ico" type="image/x-icon" />
        <link type="text/css" rel="stylesheet" href="css/normalize/normalize.css" />
	    <link type="text/css" rel="stylesheet" href="css/smoothness/jquery-ui-1.10.0.custom.min.css" />
        <!-- Import OL CSS, auto import does not work with our minified OL.js build -->
        <link rel="stylesheet" type="text/css" href="js/openlayers/theme/default/style.css">
        <!-- Basic CSS definitions -->
        <link rel="stylesheet" type="text/css" href="css/openlayers/basic.css">
        <link type="text/css" rel="stylesheet" href="css/custom.css" />
        <link type="text/css" rel="stylesheet" href="css/app.css" />
        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
        <jsp:include page="template/USGSHead.jsp">
            <jsp:param name="relPath" value="" />
            <jsp:param name="shortName" value="USGS Sediment Mapper" />
            <jsp:param name="title" value="USGS Sediment Mapper" />
            <jsp:param name="description" value="" />
            <jsp:param name="author" value="David Uselmann" />
            <jsp:param name="keywords" value="" />
            <jsp:param name="publisher" value="" />
            <jsp:param name="revisedDate" value="" />
            <jsp:param name="nextReview" value="" />
            <jsp:param name="expires" value="never" />
            <jsp:param name="development" value="<%= development %>" />
        </jsp:include>
        <script type="text/javascript" src="js/jquery/jquery-1.10.2.min.js"></script>
        <script type="text/javascript" src="js/openlayers/OpenLayers.js"></script>
        <script type="text/javascript" src="js/utils.js"></script>
	    <script type="text/javascript" src="js/filter-api.js"></script>
	    <script type="text/javascript" src="js/filter.js"></script>
    </head>
    
    <body onload="init()">
        <%-- Loads during application startup, fades out when application is built --%>
        <!--  jsp:include page="components/application-overlay.jsp">< /jsp:include -->

        <div class="container-fluid">
            <div class="row-fluid" id="header-row">
                <jsp:include page="template/USGSHeader.jsp">
                    <jsp:param name="relPath" value="" />
                    <jsp:param name="header-class" value="" />
                    <jsp:param name="site-title" value="USGS Sediment Mapper" />
                </jsp:include>
				<jsp:include page="components/app-navbar.jsp"></jsp:include>
            </div>
            
            <div class="row-fluid" id="content-row">
                <!-- NAV -->
                <div class="span1" id='nav-list'>
                    <ul id="stage-select-tablist" class="nav nav-pills nav-stacked">
                    <!-- 
                        <li class="active"><a href="#shorelines" data-toggle="tab"><img id="shorelines_img" src="images/workflow_figures/shorelines.png" title="Display Shorelines"/></a></li>
                        <li><a href="#baseline" data-toggle="tab"><img id="baseline_img" src="images/workflow_figures/baseline_future.png" title="Display Baseline"/></a></li>
                        <li><a href="#transects" data-toggle="tab"><img id="transects_img" src="images/workflow_figures/transects_future.png" title="Calculate Transects"/></a></li>
                        <li><a href="#calculation" data-toggle="tab"><img id="calculation_img" src="images/workflow_figures/calculation_future.png" title="Show Calculation"/></a></li>
                        <li><a href="#results" data-toggle="tab"><img id="results_img" src="images/workflow_figures/results_future.png" title="Display Results"/></a></li>
                     -->
                    </ul>
                    <div id="application-spinner"><img src="images/spinner/spinner2.gif" /></div>
                </div>

                <!-- Toolbox -->
                <div class="span4" id="toolbox-span">
                    <div id="toolbox-well" class="well well-small tab-content">


                    </div>

                </div>

                <!-- MAP -->
                <div class="span7" id="map-span">
                    <div id="map-well" class="well well-small tab-content">
                        <div class="olMap" id="map"></div>
                        <div id="nlcdlegend">
                            <span id="nlcdthumb">NLCD Legend</span>
                            <a href="http://www.mrlc.gov/nlcd06_leg.php" target="_tab"><img id="nlcdimg" src="images/nlcdlegend.png"></a>
                        </div>
                        <div class="filter" id="filter" >
					        <div id="filterDiv" style="border:1px solid;padding:3px;">
					            <div class="floatLeft" style="height:28px;">
					            Filter By:
					            <input type="button" id="clearFilter" value="Clear">
					            <input type="button" id="applyFilter" value="Apply"> 
					            </div>
					            <div id="applyFilter-warn" class="hidden filterWarn inputFilterWarnOn floatLeft" >Please address warnings.</div>
					            <br class="floatClear">
					        </div>
                        </div>
                        <div id="siteInfo">
                            <jsp:include page="siteInfo.jsp"/>
                        </div>
                    </div>
                </div>

            </div>
            <div class="row-fluid" id="alert-row">
                <div id="application-alert-container" class="span11 offset1"></div>
            </div>

            <div class="row-fluid" id="footer-row">
                <jsp:include page="template/USGSFooter.jsp">
                    <jsp:param name="relPath" value="" />
                    <jsp:param name="header-class" value="" />
                    <jsp:param name="site-url" value="<script type='text/javascript'>document.write(document.location.href);</script>" />
                    <jsp:param name="contact-info" value="<a href='mailto:cjlee@usgs.gov?Subject=Coastal%20Hazards%20Feedback'>Casey Lee</a>" />
                </jsp:include>
            </div>
        
        </div>
		
		<%-- Stuff that isn't shown in the application but is used by JS --%>
        <div id="modal-window" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modal-window-label" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h3 id="modal-window-label"></h3>
            </div>
            <div class="modal-body">
                <div id="modal-body-content"></div>
            </div>
            <div class="modal-footer"></div>
        </div>
        
    </body>
    
    <jsp:include page="js/dygraphs/dygraphs.jsp">
        <jsp:param name="debug-qualifier" value="true" />
    </jsp:include>
    
    <script type="text/javascript" src="js/jquery-ui/jquery-ui-1.10.0.custom.min.js"></script>
    <script type="text/javascript" src="js/ui/ui.js"></script>
    <script type="text/javascript" src="js/util/util.js"></script>
    <script src="js/openlayers/extension/Raster.js"></script>
    <script src="js/openlayers/extension/Raster/Grid.js"></script>
    <script src="js/openlayers/extension/Raster/Composite.js"></script>
    <script src="js/openlayers/extension/Raster/Operation.js"></script>
    <script src="js/openlayers/extension/Layer/Raster.js"></script>    
    <script type="text/javascript" src="js/utils.js"></script>
    <script type="text/javascript" src="js/map.js" defer="defer" ></script>
    <!-- script type="text/javascript" src="js/onReady.js"></script -->
    <jsp:include page="components/config.jsp"></jsp:include>
</html>
