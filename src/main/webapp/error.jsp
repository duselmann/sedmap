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
        <meta http-equiv="X-UA-Compatible" content="IE=edge" > 
        <meta name="google-site-verification" content="RIM1jcVM_GCYDz0SivW1kUhog3SNhVAgoA8A0SFWfQM" />
        
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
                    <jsp:param name="site-title" value="USGS Sediment Mapper" />
                </jsp:include>
				<jsp:include page="components/app-navbar.jsp"></jsp:include>
            </div>
            
            <div style="height:200px;" id="content-row">

                <div style="margin:100px">
                    We are sorry, the application encountered an unexpected result. You may try again. 
                    <br><br>
                    If you reach this a second time (or if you prefer, your first time)
                    <br>
                    please contact 
                    <a href='mailto:SedimentPortal_HELP@usgs.gov?Subject=Sediment%20Portal%20Ticket%20<%= null==session.getAttribute("errorid") ?"sdp123" :session.getAttribute("errorid") %>'>
                    Sediment Portal Help</a>
                    with this ticket ID.
                    <br><br>
                    Ticket ID: <%= null==session.getAttribute("errorid") ?"sdp123" :session.getAttribute("errorid") %>
                </div>

            </div>
            <div class="row-fluid" id="alert-row">
                <div id="application-alert-container" class="span11 offset1"></div>
            </div>

			<style>#footer-url-info {display:none;}</style>

            <div class="row-fluid" id="footer-row">
                <jsp:include page="template/USGSFooter.jsp">
                    <jsp:param name="relPath" value="" />
                    <jsp:param name="header-class" value="" />
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
    
    <script type="text/javascript" src="js/jquery-ui/jquery-ui-1.10.0.custom.min.js"></script>
    <script type="text/javascript" src="js/ui/ui.js"></script>
    <script type="text/javascript" src="js/util/util.js"></script>
</html>
