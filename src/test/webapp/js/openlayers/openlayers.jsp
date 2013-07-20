
<%
    String debug = Boolean.parseBoolean(request.getParameter("debug-qualifier")) ? "/lib" : "";
    String deprecated = Boolean.parseBoolean(request.getParameter("include-deprecated")) ? "true" : "false";
%>
<script type="text/javascript" src="${param['relPath']}js/openlayers<%= debug %>/OpenLayers.js"></script>
<% if (deprecated == "true") { %>
    <script type="text/javascript" src="${param['relPath']}js/openlayers/lib/deprecated.js"></script>
<% }%>
