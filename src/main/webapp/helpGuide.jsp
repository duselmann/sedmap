<%@ include file="/context.jsp" %>
<%@ page import="gov.cida.sedmap.web.AtomReaderUtil" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">
    
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=edge" >  
        <meta name="google-site-verification" content="RIM1jcVM_GCYDz0SivW1kUhog3SNhVAgoA8A0SFWfQM" />  
        
        <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
        <link rel="icon" href="favicon.ico" type="image/x-icon" />
        <link type="text/css" rel="stylesheet" href="css/normalize/normalize.css" />
        <link type="text/css" rel="stylesheet" href="css/smoothness/jquery-ui-1.10.0.custom.min.css" />
        <link type="text/css" rel="stylesheet" href="css/custom.css" />
        <link type="text/css" rel="stylesheet" href="css/app.css" />
        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
        <jsp:include page="template/USGSHead.jsp">
            <jsp:param name="relPath" value="" />
            <jsp:param name="shortName" value="USGS Sediment Data Portal" />
            <jsp:param name="title" value="USGS Sediment Data Portal" />
            <jsp:param name="description" value="" />
            <jsp:param name="author" value="USGS" />
            <jsp:param name="keywords" value="" />
            <jsp:param name="publisher" value="" />
            <jsp:param name="revisedDate" value="" />
            <jsp:param name="nextReview" value="" />
            <jsp:param name="expires" value="never" />
            <jsp:param name="development" value="<%= development %>" />
            <jsp:param name="google-analytics-account-code" value="<%= googleAnalyticsAccountNumber %>" />

        </jsp:include>
        <script type="text/javascript" src="js/jquery/jquery-1.10.2.min.js"></script>
        <script type="text/javascript" src="js/utils.js"></script>
    </head>
    
    <body onload="init()">
        <%-- Loads during application startup, fades out when application is built --%>
        <!--  jsp:include page="components/application-overlay.jsp">< /jsp:include -->

        <div class="container-fluid">
            <div class="row-fluid" id="header-row">
                <jsp:include page="template/USGSHeader.jsp">
                    <jsp:param name="relPath" value="" />
                    <jsp:param name="header-class" value="" />
                    <jsp:param name="site-title" value="USGS Sediment Data Portal" />
                </jsp:include>
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

                <h1>Sediment Data Portal Guide</h1> <!-- &title=SPARROW_DSS_DOCS_FAQ&labelString=sparrow_dss_docs_faq& -->
                <%= AtomReaderUtil.getAtomFeedContentOnlyAsString("https://my.usgs.gov/confluence/createrssfeed.action?types=page&spaces=conf_all&title=SEDIMENT_GUIDE&labelString=sediment_help_guide&excludedSpaceKeys=&sort=modified&maxResults=1&timeSpan=3650&showContent=true") %>



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
                    <jsp:param name="contact-info" value="<a href='mailto:SedimentPortal_HELP@usgs.gov?Subject=Sediment%20Portal%20Feedback'>Sediment Portal Help</a>" />
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
</html>
