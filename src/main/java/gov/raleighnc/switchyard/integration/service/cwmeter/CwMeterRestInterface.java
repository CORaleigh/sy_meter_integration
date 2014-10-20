package gov.raleighnc.switchyard.integration.service.cwmeter;


/**
 * Switchyard REST-based "Composite Reference" wired to the Cityworks system for using Cityworks custom REST-based Meter services only.
 * 
 * @author mikev
 *
 */
public interface CwMeterRestInterface {
	String createMeter(String jsonString);
	
	String updateMeter(String jsonString);
	
	String getFaType(String fatype);

    String getFARemark(String code);     
    
    String getSourceStatus(String code);    
    
    String getDisconnectLocation(String code);
    
    String getSize(String code);    

    String getReadType(String code);    
    
    String getMRSource(String code);     
}