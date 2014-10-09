package gov.raleighnc.switchyard.integration.service.cwmeter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


/**
 * Switchyard "Reference Binding" implementation to communicate to the Cityworks system directly using Web Services.
 * 
 * @author mikev
 *
 */
@Path("/")
public interface CwWoRestInterfaceJaxRs {
    @POST
    @Path("/createwo")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String createWorkOrder(String workorderJson);
    
    @POST
    @Path("/updatewo")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String updateWorkOrder(String workorderJson);
}