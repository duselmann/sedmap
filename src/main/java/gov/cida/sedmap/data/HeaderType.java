
package gov.cida.sedmap.data;

import gov.cida.sedmap.io.IoUtils;

/**
 *
 * @author cschroed
 */
public enum HeaderType {

    GENERAL("/general-header.txt"),
    DISCRETE("/discrete-header.txt"),
    DAILY("/daily-header.txt"),
    SITE("/site-header.txt");
    
    public final String header;

    HeaderType(String resourcePath){
        this.header = IoUtils.readTextResource(resourcePath);
    }

}
