<%@page import="javax.naming.NamingException"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%! 
    protected InitialContext ctx = null;    
    protected boolean development = false;
    protected String googleAnalyticsAccountNumber = "";
    {
        try {
            InitialContext ctx = new InitialContext();
            String devString = (String) ctx.lookup("java:comp/env/sedmap/development");
            development = Boolean.parseBoolean(devString);
            googleAnalyticsAccountNumber = (String) ctx.lookup("java:comp/env/sedmap/googleAnalyticsAccountCode");
        }
        catch (NamingException e) {
            LoggerFactory.getLogger("index.jsp").error("Error reading environment variables.");
        }
    }
%>