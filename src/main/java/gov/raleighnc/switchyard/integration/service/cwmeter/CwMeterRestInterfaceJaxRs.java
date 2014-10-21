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
    @GET
    @Path("/getheader/{workorderid}")
    @Produces({"application/json"})
    String getMeter(@PathParam("workorderid") String workorderid);	
	
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
    
    @POST
    @Path("/deleteheader")
    @Consumes({"application/json", "application/xml"})    
    @Produces({"application/json", "application/xml"})
    String deleteMeter(String workorderid);	    
    
    @GET
    @Path("/getfatype/{fatype}")
    @Produces({"application/json"})
    String getFaType(@PathParam("fatype") String fatype);
    
    @GET
    @Path("/getcode/{code}/FARemark")
    @Produces({"application/json"})
    String getFARemark(@PathParam("code") String code);     
    
    @GET
    @Path("/getcode/{code}/SourceStatus")
    @Produces({"application/json"})
    String getSourceStatus(@PathParam("code") String code);    
    
    @GET
    @Path("/getcode/{code}/Disconnection")
    @Produces({"application/json"})
    String getDisconnectLocation(@PathParam("code") String code);
    
    @GET
    @Path("/getcode/{code}/Size")
    @Produces({"application/json"})
    String getSize(@PathParam("code") String code);    
    
    @GET
    @Path("/getcode/{code}/ReadType")
    @Produces({"application/json"})
    String getReadType(@PathParam("code") String code);    
    
    @GET
    @Path("/getcode/{code}/MRSource")
    @Produces({"application/json"})
    String getMRSource(@PathParam("code") String code);     
}