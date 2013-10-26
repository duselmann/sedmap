<%@page contentType="text/html" pageEncoding="UTF-8"%>
    
<div class="blackoverlay hidden">
    <div class="downloadWindow">
        <div class="title center">Sediment Data Download</div><div class="closeWindow">X</div>
        <div class="downloadNote center">Hover over (?) for assistence.</div>
         <ul>
        <li><label><input type="checkbox" id="DL-daily"> Daily Flow and Sediment Data</label>
            <span title="Download mean daily flow, SSC, and SSL data from NWIS."> (?)</span>
        </li>
        <li><label><input type="checkbox" id="DL-discrete"> Discrete Sample Data</label>
            <span title="Download discrete SSC and associated data from the sediment data portal."> (?)</span>
        </li>
        <li><div class="subitem">
                <label><input type="checkbox" id="DL-discreteFlow" style="float:left;margin-right:3px;">
                <div style="float:left;">Include Daily Flow</div></label>
                <span title="Check to download mean daily flow data from NWIS for the discrete sample sites.">&nbsp;(?)</span>
            </div>
        </li>
        <li><label><input type="checkbox" id="DL-sitesOnly"> Site Attribute Information Only</label>
            <span title="Check this option if you only want site attribute information. (Daily or Discrete data must also be selected.)"> (?)</span>
        </li>
        <li>File Format: 
            <select id="DL-format"><option>csv</option><option selected="true">tsv</option></select>
            <span title="Choose your preferred data separation format."> (?)</span>
        </li>
        <li>Email Address:
            <span title="Downloading large amounts of data can take several minutes to hours.  Enter your email address if you would like data to be sent to your email, otherwise data will download through your web browser."> (?)</span>
            <input style="width:220px" type="text" id="DL-email" />
        </li>
        </ul>
        <div id="DL-msg" style="height:20px;text-align:center;"></div>
        <div class="buttons center">
            <input id="DL-download" type="button" class="download" value="Download Data">
            &nbsp;&nbsp;
            <input id="DL-cancel" type="button" class="download" value="Cancel"> 
        </div>
    </div>
</div>

