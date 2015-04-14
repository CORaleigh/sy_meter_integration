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
    
    @POST
    @Path("/deletewo")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String deleteWorkOrder(String workorderId);    
    
    @POST
    @Path("/createwoe")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String createWorkOrderEntity(String workorderEntityJson); 
    
    @POST
    @Path("/createwocf")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String createWorkOrderCustomField(String wocfJson);     
    
    @POST
    @Path("/updatewocf")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String updateWorkOrderCustomField(String wocfJson);         
    
    @POST
    @Path("/getcustfieldid")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String retrieveCustomFieldId(String wocfJson);
    
    @POST
    @Path("/getcategoryid")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String retrieveCategoryId(String wocfJson);     
}