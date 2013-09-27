<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">
        
<head>
    <link type="text/css" rel="stylesheet" href="../../main/webapp/css/app.css" />
    
<style>
div.menubar {
    margin:0;
    padding:0;
    position:absolute;
    top:74px;
    left:1200px;
    z-index:100;
    font-weight:bold;
}
div.menubar div.menu {
    float: left;
    width: 62px;
    height: 20px;
    overflow-y: hidden;
    color: white;
    background: #345280;
    padding: 4px;
    border: 1px solid #345280;
}
div.menubar div.menu:hover {
    float: left;
    height: auto;
    color: #345280;
    background: white;
    padding: 5px;
    padding-bottom: 0px;
}
div.menu div.header {
    height:30px;
    display:inline-block;
}
div.menu div.item {
    color: white;
    background: #345280;
    display:inline-block;
    width:60px;
    border: 1px solid #345280;
    border-bottom:0px;
    padding-top: 4px;
    padding-bottom: 4px;
}
div.menu div.item:hover {
    color: blue;
    background: white;
}
div.menu a {
    display:block;
    background-color:inherit;
    color:inherit;
    text-decoration: none;
}
div.menu a:before {
    content: '\0000a0\0000a0';
}

</style
    
</head>
<body>
<div class="menubar">
    <div class="menu">
        <div class="header">HELP</div>
        <div class="item"><a onclick="openUrlInNewTab('quickStart.hmtl')" href="#">Quick Reference</a></div>
        <div class="item"><a onclick="openUrlInNewTab('documentation.hmtl')" href="#">Documentation</a></div>
    </div>
</div>
</body>
</html>
