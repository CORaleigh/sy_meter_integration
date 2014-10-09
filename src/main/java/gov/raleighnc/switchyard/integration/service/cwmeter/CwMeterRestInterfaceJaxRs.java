package gov.raleighnc.switchyard.integration.service.cwmeter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


/**
 * Switchyard "Reference Binding" implementation to communicate to the Cityworks system for using Cityworks custom REST-based Meter services only.
 * 
 * @author mikev
 *
 */
@Path("/")
public interface CwMeterRestInterfaceJaxRs {
    @POST
    @Path("/saveheader")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String createMeter(String jsonString);
    
    @POST
    @Path("/updateheader")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    String updateMeter(String jsonString);    
    
    @GET
    @Path("/getfatype/{fatype}")
    @Produces({"application/json"})
    String getFaType(@PathParam("fatype") String fatype);
    
    @GET
    @Path("/getcode/{code}")
    @Produces({"application/json"})
    String getCodeDescription(@PathParam("code") String code);     
}