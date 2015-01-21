package gov.raleighnc.switchyard.integration.service.cwmeter;

import gov.raleighnc.switchyard.integration.domain.Result;
import gov.raleighnc.switchyard.integration.domain.ccb.CcbCwWorkOrder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Switchyard REST-based "Service Binding" for the CcbService contract.
 * 
 * @author mikev
 */
@Path("/")
public interface CcbServiceJaxRs {
    @POST
    @Path("/closefa")
    @Consumes({"application/json"})
    @Produces({"application/json"})	
	Result closeFieldActivity(final CcbCwWorkOrder workorder);
}