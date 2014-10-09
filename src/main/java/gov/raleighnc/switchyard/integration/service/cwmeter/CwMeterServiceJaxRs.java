package gov.raleighnc.switchyard.integration.service.cwmeter;

import gov.raleighnc.switchyard.integration.domain.Result;
import gov.raleighnc.switchyard.integration.domain.ccb.CcbCwWorkOrder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Switchyard REST-based "Service Binding" for the CwMeterService contract.
 * 
 * @author mikev
 */
@Path("/")
public interface CwMeterServiceJaxRs {
	
    @POST
    @Path("/createwo")
    @Consumes({"application/json"})
    @Produces({"application/json"})	
	Result createWorkOrder(final CcbCwWorkOrder workorder);
}