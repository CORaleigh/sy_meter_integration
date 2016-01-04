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
 * An example CcbCwWorkOrder JSON input looks like the following:
 * 
 * {
"MeterHeader" : {
	"FieldActivityId" : "1",
	"FieldActivityType" : { "Code" : "MTR_TST" },
	"CustomerName" : "Chad",
	"CustomerPhone" : "123-456-7890",
	"LifeSupport" : "true",
	"OriginalMeter" : {
		"MeterId" : "12345678",
		"BadgeNumber" : "87654321",
		"RemoveMeter" : "true",
		"IsDeviceTest" : "true",
		"CompoundMtr" : "true",
		"ReadDateTime" : null,
		"Size" : {"Code" : "C"},
		"Register1" : {
			"Reading" : "350",
			"Dials" : "3",
			"Miu" : "87654321",
			"ReadType" : {"Code" : "70"},
			"MrSource" : {"Code" : "MTR-BROKEN"},
			"Size" : {"Code" : "C"},
			"LowReadThreshold" : "4500",
			"HighReadThreshold" : "4800"
		},
		"Register2" : {
			"Reading" : "400",
			"Dials" : "2",
			"Miu" : "11223344",
			"ReadType" : {"Code" : "20"},
			"MrSource" : {"Code" : "UB-CATCH"},
			"Size" : {"Code" : "C"},
			"LowReadThreshold" : "4600",
			"HighReadThreshold" : "5000"
		},
		"DeviceTest" : null
	},
	"CurrentMeter" : {
		"MeterId" : "12345678",
		"BadgeNumber" : "87654321",
		"RemoveMeter" : "true",
		"IsDeviceTest" : "true",
		"CompoundMtr" : "true",
		"ReadDateTime" : null,
		"Size" : {"Code" : "C"},
		"Register1" : {
			"Reading" : "350",
			"Dials" : "3",
			"Miu" : "87654321",
			"ReadType" : {"Code" : "70"},
			"MrSource" : {"Code" : "MTR-BROKEN"},
			"Size" : {"Code" : "C"},
			"LowReadThreshold" : "4500",
			"HighReadThreshold" : "4800"
		},
		"Register2" : {
			"Reading" : "400",
			"Dials" : "2",
			"Miu" : "11223344",
			"ReadType" : {"Code" : "20"},
			"MrSource" : {"Code" : "UB-CATCH"},
			"Size" : {"Code" : "C"},
			"LowReadThreshold" : "4600",
			"HighReadThreshold" : "5000"
		},
		"DeviceTest" : null
	},
	"InstallMeter" : null,
	"StreetAddress" : "219 Fayetteville St",
	"FAInstructions" : "Do this and then do that....",
	"FAComments" : "Here are some long super duper duper comments",
	"SPLocationDetails" : "Location Details",
	"SPType" : "This is the sp type",
	"PremiseType" : "Commercial",
	"Township" : "Raleigh",
	"CityLimit" : "Inside",
	"UseClass" : "Non-Residential - Commercial",
	"Postal" : "27601",
	"FARemark" : { "Code" :  "X-NOTES-CM"},
	"SPSourceStatus" : { "Code" : "D"},
	"DisconnectLocation" : { "Code"  : "METR"},
	"DispatchGroup" : null,
	"SPId" : "11223344",
	"StockLocation" : null,
	"AdjustmentType" : null,
	"LetterType" : null,
	"ToDoType" : null,
	"AdjustmentValue" : null,
	"LetterValue" : null,
	"ToDoValue" : null,
	"WorkOrderId": null,
	"Route" : null,
	"RouteSequenceStart" : null,
	"RouteSequenceEnd" : null,
	"FAClass" : null	
},

"WorkOrder" : {
	"WorkOrderId" : "213",
	"Description" : "Hello World Test",
	"Supervisor" : "Me",
	"RequestedBy" : "You 2",
	"InitiatedBy" : "Him 3",
	"InitiateDate" : "2010-03-01",
	"Location" : "South Des Moines",
	"ProjectStartDate" : "2009-03-01",
	"ProjectFinishDate" : "2015-03-01",
	"Priority" : "Up",
	"NumDaysBefore" : "5",
	"WoCategory" : "Category 23",
	"SubmitTo" : "Houston Texas",
	"Status" : "Working",
	"WoTemplateId" : "2",
	"WoAddress" : "219 Fayetteville St, Raleigh, NC 27601",
	"WoXCoordinate" : "35.777958",
	"WoYCoordinate" : "-78.639030"
	}
}
 * @author mikev
 */
@Path("/")
public interface CwMeterServiceJaxRs {
	
    @POST
    @Path("/createwo")
    @Consumes({"application/json"})
    @Produces({"application/json"})	
	Result createWorkOrder(final CcbCwWorkOrder workorder);
    
    @POST
    @Path("/updatewo")
    @Consumes({"application/json"})
    @Produces({"application/json"})	
	Result updateWorkOrder(final CcbCwWorkOrder workorder);
    
    @POST
    @Path("/deletewo")
    @Consumes({"application/json"})
    @Produces({"application/json"})	
	Result deleteWorkOrder(final String workorderId);       
    
    @POST
    @Path("/updatemeter")
    @Consumes({"application/json"})
    @Produces({"application/json"})	
	Result updateMeter(final CcbCwWorkOrder workorder);        
}