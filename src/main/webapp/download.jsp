<%@page contentType="text/html" pageEncoding="UTF-8"%>
    
<div class="blackoverlay hidden">
    <div class="downloadWindow">
        <div class="title center">Sediment Data Download</div><div class="closeWindow">X</div>
        <ul>
        <li><input type="checkbox" id="DL-daily"> Daily Flow Data
            <span title="Download daily average flow, SSC, and SSL sensor data from NWIS."> (?)</span>
        </li>
        <li><input type="checkbox" id="DL-discrete"> Discrete Sample Data
            <span title="Download QA/QC Discrete samples from Sediment Data Portal."> (?)</span>
        </li>
        <li><div class="subitem">
                <input type="checkbox" id="DL-discreteFlow" style="float:left;margin-right:3px;">
                <div style="float:left;">Include daily flow</div>
                <span title="Include daily flow data for discrete sample sites.">&nbsp;(?)</span>
            </div>
        </li>
        <li><input type="checkbox" id="DL-sitesOnly"> Sites Only
            <span title="Check this option if you do not want sensor data."> (?)</span>
        </li>
        <li>File Format: 
            <select id="DL-format"><option>csv</option><option selected="true">tsv</option></select>
            <span title="Choose your preferred data separation format."> (?)</span>
        </li>
        <li>Email Address:
            <span title="Jobs can run long. We will send you an email with a link to your data file upon complete. The file will be retained for 7 days after it is sent. Address are not retained after message is sent."> (?)</span>
            <input style="width:220px" type="text" id="DL-email" />
        </li>
        </ul>
        <div class="buttons center">
            <input id="DL-download" type="button" class="download" value="Download Data">
            &nbsp;&nbsp;
            <input id="DL-cancel" type="button" class="download" value="Cancel"> 
        </div>
    </div>
</div>

