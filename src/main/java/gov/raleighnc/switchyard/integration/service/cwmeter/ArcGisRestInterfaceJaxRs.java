package gov.raleighnc.switchyard.integration.service.cwmeter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Switchyard "Reference Binding" implementation to communicate to ArcGIS REST-based Meter services.
 * 
 * @author mikev
 *
 */
@Path("/")
public interface ArcGisRestInterfaceJaxRs {
    @GET
    @Path("?where=CCBSPID={spId}&outFields=objectid,facilityid&f=json")
    @Produces({"application/json"})
    String getFacilityIdAndObjectId(@PathParam("spId") String spId);	
}