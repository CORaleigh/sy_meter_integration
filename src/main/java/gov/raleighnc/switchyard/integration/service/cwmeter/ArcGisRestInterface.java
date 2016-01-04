package gov.raleighnc.switchyard.integration.service.cwmeter;

/**
 * Switchyard REST-based "Composite Reference" wired to the ArcGIS system for REST-based services only.
 * 
 * @author mikev
 *
 */
public interface ArcGisRestInterface {
	String getFacilityIdAndObjectId(String payload);
}