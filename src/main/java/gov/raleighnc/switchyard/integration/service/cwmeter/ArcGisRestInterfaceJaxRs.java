package gov.raleighnc.switchyard.integration.service.cwmeter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Switchyard "Reference Binding" implementation to communicate to ArcGIS REST-based Meter services.
 * 
 * @author mikev
 *
 */
@Path("/")
public interface ArcGisRestInterfaceJaxRs {
    @POST
    @Consumes({"application/x-www-form-urlencoded; charset=UTF-8"})
    @Produces({"application/json"})
    String getFacilityIdAndObjectId(String payload);	
}