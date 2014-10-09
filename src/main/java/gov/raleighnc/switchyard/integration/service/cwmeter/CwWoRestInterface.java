package gov.raleighnc.switchyard.integration.service.cwmeter;


/**
 * Switchyard REST-based "Composite Reference" wired to the Cityworks system for using Cityworks custom REST-based WO services only.
 * 
 * @author mikev
 *
 */
public interface CwWoRestInterface {
	String createWorkOrder(String workorderJson);
	
	String updateWorkOrder(String workorderJson);
}