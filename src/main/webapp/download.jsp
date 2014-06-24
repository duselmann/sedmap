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
        <li><label><input type="checkbox" id="DL-directdownload"> Direct Download</label>
            <span title="Download directly from this page submission.  An email address must be entered in case the data process takes too long.  If the process extends more than 1 minute, an email with a link to the finished file will be sent."> (?)</span>
        </li>
        <li>Email Address:
            <span title="Downloading large amounts of data can take several minutes to hours.  Enter your email address if you would like a notification sent when your data is ready to be downloaded."> (?)</span>
            <input style="width:220px" type="text" id="DL-email" />
        </li>
        </ul>
        <div id="DL-msg" style="height:35px;text-align:center;padding-left:15px;padding-right:15px;display: none;"></div>
        <div class="center">
        	<img id="DL-downloadprogress" src="images/spinner/spinner3.gif" alt="Working..." style="height: 20px;width: 20px;display: none;"/>
        </div>
        <div class="buttons center" style="height:35px;margin-top:10px;">
            <input id="DL-download" type="button" class="download" value="Download Data">
            &nbsp;&nbsp;
            <input id="DL-cancel" type="button" class="download" value="Cancel"> 
        </div>
    </div>
    <form id="dlf_form" action="data" method="post" target="dlf_iframe" style="display:none;">
        <input type="text" value="" name="format"      id="dlf_format">
        <input type="text" value="" name="email"       id="dlf_email">
        <input type="text" value="" name="dataTypes"   id="dlf_dataTypes">
        <input type="text" value="" name="dailyFilter" id="dlf_dailyFilter">
        <input type="text" value="" name="discreteFilter" id="dlf_discreteFilter">
        <input type="text" value="" name="directDownload" id="dlf_directDownload">
        <input type="submit">
    </form>
    <iframe id="dlf_iframe" name="dlf_iframe" style="display: none;" ></iframe>
    
</div>

