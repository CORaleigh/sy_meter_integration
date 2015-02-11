package gov.raleighnc.switchyard.integration.service.cwmeter;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import gov.raleighnc.switchyard.integration.domain.Result;
import gov.raleighnc.switchyard.integration.domain.ccb.CcbCwWorkOrder;
import gov.raleighnc.switchyard.integration.domain.ccb.meter.CodeDescription;
import gov.raleighnc.switchyard.integration.domain.ccb.meter.FieldActivityType;
import gov.raleighnc.switchyard.integration.domain.ccb.meter.MeterHeader;
import gov.raleighnc.switchyard.integration.domain.ccb.meter.Register;
import gov.raleighnc.switchyard.integration.domain.cityworks.workorder.WorkOrder;
import gov.raleighnc.switchyard.integration.domain.cityworks.workorder.WorkOrderEntity;

@Service(CwMeterService.class)
public class CwMeterServiceBean implements CwMeterService {
	
	@Inject
	@Reference
	private CwMeterRestInterface cwMeterRestInterface;
	
	@Inject
	@Reference
	private CwWoRestInterface cwWoRestInterface;	
	
	@Inject
	@Reference
	private ArcGisRestInterface arcGisRestInterface;
	
	private ObjectMapper om;
	
	private final static String METER_ENTITY_TYPE = "WSERVICECONNECTION";
	
	/**
	 * Default no-arg constructor
	 */
	public CwMeterServiceBean() {
		om = new ObjectMapper();
		om.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	@Override
	public Result createWorkOrder(CcbCwWorkOrder workorder) {
		Result validateResult = validateWorkOrder(workorder, false);
		
		if (!validateResult.isSuccess()) {
			return validateResult;
		}
		
		// (1) create WO in CW
		
		WorkOrder wo = workorder.getWorkOrder();
		
		String spId = workorder.getMeterHeader().getSpId();
		
		// requirement is to set text1 of WO in CW to the SP ID value
		wo.setText1(spId);
		
		String woJson = "";
		
		try {
			woJson = om.writeValueAsString(wo);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
			
		String woResultString = cwWoRestInterface.createWorkOrder(woJson);
		Result woResult = null;
				
		try {
			woResult = om.readValue(woResultString, Result.class);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
		
		if (!woResult.isSuccess()) {
			return woResult;
		}
		
		String woId = woResult.getMessage();
		
		// (2) check to see if facility id already exists for SP ID
		
		String spIdResultJson = arcGisRestInterface.getFacilityIdAndObjectId(spId);
		int objectId = 0;
		String facilityId = null;
		
		try {
			JsonNode rootNode = om.readValue(spIdResultJson, JsonNode.class);
			
			objectId = rootNode.path("features").path("attributes").path("OBJECTID").intValue();
			facilityId = rootNode.path("features").path("attributes").path("FACILITYID").textValue();
		} catch (Exception e) {
			return new Result(false, "Issues retrieving object and facility ids for SP ID = " + spId, e.getMessage());
		}
		
		// (3) if both object id and facility id have values from ArcGIS, push this into WOE so it will become attached in CW
		// if nothing is returned for either value, just skip this section and hence WO will be unattached
		if (objectId > 0 && facilityId != null && !facilityId.isEmpty()) {
			WorkOrderEntity woe = new WorkOrderEntity();
			woe.setWorkOrderId(woId);
			woe.setEntityUid(facilityId);
			woe.setObjectId(objectId);
			woe.setEntityType(METER_ENTITY_TYPE);
			
			String woeJson = "";
			
			try {
				woeJson = om.writeValueAsString(woe);
			} catch (Exception e) {
				return new Result(false, null, e.getMessage());
			}		
			
			String woeResultString = cwWoRestInterface.createWorkOrderEntity(woeJson);
			Result woeResult = null;
			
			try {
				woeResult = om.readValue(woeResultString, Result.class);
			} catch (Exception e) {
				return new Result(false, null, e.getMessage());
			}
			
			if (!woeResult.isSuccess()) {
				return woeResult;
			}			
		}
		
		
		// (4) Create Meter
		
		MeterHeader mh = workorder.getMeterHeader();
		mh.setWorkOrderId(woId);
		String mhJson = "";
		
		try {
			mhJson = om.writeValueAsString(mh);
		} catch (Exception e) {
			cwWoRestInterface.deleteWorkOrder(woId);
			return new Result(false, null, e.getMessage());
		}
				
		String mhResultString = cwMeterRestInterface.createMeter(mhJson);
		
		// lowercase fieldnames
		mhResultString = mhResultString.replaceAll("Success", "success");
		mhResultString = mhResultString.replaceAll("Message", "message");
		mhResultString = mhResultString.replaceAll("Exception", "exception");
		
		Result mhResult = null;
		
		try {
			mhResult = om.readValue(mhResultString, Result.class);
		} catch (Exception e) {
			cwWoRestInterface.deleteWorkOrder(woId);
			return new Result(false, null, e.getMessage());
		}
		
		if (!mhResult.isSuccess()) {
			cwWoRestInterface.deleteWorkOrder(woId);
			return mhResult;
		}		
		
		// everything successful so return the successful woResult that contains the woId in the message
		return woResult;  
	}
	
	/**
	 * Note that this does <strong>not</strong> update WOE in CW since it is spec'd to only be done
	 * via creation and if the creation results in an unattached WO, it must be manually attached in CW 
	 * by the user.  Hence no update will attach an unattached WO.
	 */
	@Override
	public Result updateWorkOrder(CcbCwWorkOrder workorder) {
		Result validateResult = validateWorkOrder(workorder, false);
		
		if (!validateResult.isSuccess()) {
			return validateResult;
		}
		
		WorkOrder wo = workorder.getWorkOrder();
		MeterHeader mh = workorder.getMeterHeader();
		
		// requirement is to set text1 of WO in CW to the SP ID value
		wo.setText1(mh.getSpId());
		
		if (wo.getWorkOrderId() == null || wo.getWorkOrderId().isEmpty()) {
			return new Result(false, null, "No work order id was specified");
		}
		
		String woJson = "";
		
		try {
			woJson = om.writeValueAsString(wo);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
		
		String woResultString = cwWoRestInterface.updateWorkOrder(woJson);
		Result woResult = null;
				
		try {
			woResult = om.readValue(woResultString, Result.class);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
		
		if (!woResult.isSuccess()) {
			return woResult;
		}		
		
		mh.setWorkOrderId(wo.getWorkOrderId());
		
		String mhJson = "";
		
		try {
			mhJson = om.writeValueAsString(mh);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
				
		String mhResultString = cwMeterRestInterface.updateMeter(mhJson);
		
		// lowercase fieldnames
		mhResultString = mhResultString.replaceAll("Success", "success");
		mhResultString = mhResultString.replaceAll("Message", "message");
		mhResultString = mhResultString.replaceAll("Exception", "exception");
		
		Result mhResult = null;
		
		try {
			mhResult = om.readValue(mhResultString, Result.class);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
		
		if (!mhResult.isSuccess()) {
			return mhResult;
		}				
		
		return new Result(true, null, null);
	}
	
	@Override
	public Result deleteWorkOrder(String workorderId) {
		if (workorderId == null || workorderId.isEmpty()) {
			return new Result(false, null, "No workorder id was provided");
		}
		
		String mResultString = cwMeterRestInterface.deleteMeter(workorderId);
		
		// lowercase fieldnames
		mResultString = mResultString.replaceAll("Success", "success");
		mResultString = mResultString.replaceAll("Message", "message");
		mResultString = mResultString.replaceAll("Exception", "exception");
		
		Result mResult = null;
		
		try {
			mResult = om.readValue(mResultString, Result.class);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
		
		if (!mResult.isSuccess()) {
			return mResult;
		}					
		
		String woResultString = cwWoRestInterface.deleteWorkOrder(workorderId);
		
		Result woResult = null;
		
		try {
			woResult = om.readValue(woResultString, Result.class);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
		
		return woResult;
	}
	
	@Override
	public Result updateMeter(CcbCwWorkOrder workorder) {
		Result validateResult = validateWorkOrder(workorder, true);
		
		if (!validateResult.isSuccess()) {
			return validateResult;
		}
		
		MeterHeader mh = workorder.getMeterHeader();
						
		if (mh.getWorkOrderId() == null || mh.getWorkOrderId().isEmpty()) {
			return new Result(false, null, "No work order id was specified");
		}
		
		String mhJson = "";
		
		try {
			mhJson = om.writeValueAsString(mh);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
				
		String mhResultString = cwMeterRestInterface.updateMeter(mhJson);
		
		// lowercase fieldnames
		mhResultString = mhResultString.replaceAll("Success", "success");
		mhResultString = mhResultString.replaceAll("Message", "message");
		mhResultString = mhResultString.replaceAll("Exception", "exception");
		
		Result mhResult = null;
		
		try {
			mhResult = om.readValue(mhResultString, Result.class);
		} catch (Exception e) {
			return new Result(false, null, e.getMessage());
		}
		
		if (!mhResult.isSuccess()) {
			return mhResult;
		}				
		
		return new Result(true, null, null);		
	}
	
	
	/**
	 * Helper to validate fields in the CCB work order submitted.
	 * 
	 * @param workorder The workorder to validate
	 * @param isMeterOnly if just validating meter part of work order only
	 * @return a Result-- Success will be false if validation fails anywhere, true otherwise
	 */
	protected Result validateWorkOrder(CcbCwWorkOrder workorder, boolean isMeterOnly) {
		if (workorder == null) {
			return new Result(false, null, "No CCB WorkOrder submitted");
		}
		
		WorkOrder wo = workorder.getWorkOrder();
		MeterHeader mh = workorder.getMeterHeader();
		
		if (wo == null && !isMeterOnly) {
			return new Result(false, null, "No workorder information was submitted");
		}
		
		if (mh == null) {
			return new Result(false, null, "No meter information was submitted");
		}
		
		if (mh.getSpId() == null || mh.getSpId().isEmpty()) {
			return new Result(false, null, "No SP ID provided");
		}
		
		if (mh.getStreetAddress() == null || mh.getStreetAddress().isEmpty()) {
			return new Result(false, null, "No street address provided");
		}
		
		if (mh.getTownship() == null || mh.getTownship().isEmpty()) {
			return new Result(false, null, "No township provided");
		}	
		
		if (mh.getPostal() == null || mh.getPostal().isEmpty()) {
			return new Result(false, null, "No postal code provided");
		}		
			
		if (mh.getFieldActivityType() == null || mh.getFieldActivityType().getCode() == null || mh.getFieldActivityType().getCode().isEmpty()) {
			return new Result(false, null, "No field activity type provided");
		} else {
			String faType = cwMeterRestInterface.getFaType(mh.getFieldActivityType().getCode());
			
			if (faType == null || faType.isEmpty()) {
				return new Result(false, null, "Invalid field activity type provided");
			} else {
				try {
					mh.setFieldActivityType(om.readValue(faType, FieldActivityType.class));
				} catch (Exception e) {
					return new Result(false, null, e.getMessage());
				}
			}
		}
		
		if (mh.getFieldActivityId() == null || mh.getFieldActivityId().isEmpty()) {
			return new Result(false, null, "No field activity id provided");
		}
		
		if (mh.getFaRemark() != null && mh.getFaRemark().getCode() != null && !mh.getFaRemark().getCode().isEmpty()) {
			String faRemark = cwMeterRestInterface.getFARemark(mh.getFaRemark().getCode());
			
			if (faRemark == null || faRemark.isEmpty()) {
				return new Result(false, null, "Invalid field activity remark provided");
			} else {
				try {
					mh.setFaRemark(om.readValue(faRemark, CodeDescription.class));
				} catch (Exception e) {
					return new Result(false, null, e.getMessage());
				}
			}
		}
		
		if (mh.getSpSourceStatus() != null && mh.getSpSourceStatus().getCode() != null && !mh.getSpSourceStatus().getCode().isEmpty()) {
			String spSource = cwMeterRestInterface.getSourceStatus(mh.getSpSourceStatus().getCode());
			
			if (spSource == null || spSource.isEmpty()) {
				return new Result(false, null, "Invalid SP source status provided");
			} else {
				try {
					mh.setSpSourceStatus(om.readValue(spSource, CodeDescription.class));
				} catch (Exception e) {
					return new Result(false, null, e.getMessage());
				}
			}
		}		
		
		if (mh.getDisconnectLocation() != null && mh.getDisconnectLocation().getCode() != null && !mh.getDisconnectLocation().getCode().isEmpty()) {
			String disconnect = cwMeterRestInterface.getDisconnectLocation(mh.getDisconnectLocation().getCode());
			
			if (disconnect == null || disconnect.isEmpty()) {
				return new Result(false, null, "Invalid disconnect location provided");
			} else {
				try {
					mh.setDisconnectLocation(om.readValue(disconnect, CodeDescription.class));
				} catch (Exception e) {
					return new Result(false, null, e.getMessage());
				}
			}
		}		
		
		if (mh.getCurrentMeter() != null) {
			if (mh.getCurrentMeter().getMeterId() <= 0) {
				return new Result(false, null, "Invalid or no current meter id provided");
			}
			
			if (mh.getCurrentMeter().getBadgeNumber() <= 0) {
				return new Result(false, null, "Invalid or no current meter badge number provided");
			}			
			
			if (mh.getCurrentMeter().getSize() == null || mh.getCurrentMeter().getSize().getCode() == null || mh.getCurrentMeter().getSize().getCode().isEmpty()) {
				return new Result(false, null, "Invalid or no current meter size provided");
			} else {
				String size = cwMeterRestInterface.getSize(mh.getCurrentMeter().getSize().getCode());
				
				if (size == null || size.isEmpty()) {
					return new Result(false, null, "Invalid current meter size provided");
				} else {
					try {
						mh.getCurrentMeter().setSize(om.readValue(size, CodeDescription.class));
					} catch (Exception e) {
						return new Result(false, null, e.getMessage());
					}
				}
			}
			
			Register reg1 = mh.getCurrentMeter().getRegister1();
			
			if (reg1 == null) {
				return new Result(false, null, "No register 1 information was provided");
			} 
			
			if (reg1.getReadType() == null || reg1.getReadType().getCode() == null || reg1.getReadType().getCode().isEmpty()) {
				return new Result(false, null, "No register 1 read type information provided");
			} else {
				String readType = cwMeterRestInterface.getReadType(reg1.getReadType().getCode());
				
				if (readType == null || readType.isEmpty()) {
					return new Result(false, null, "Invalid register 1 read type provided");
				} else {
					try {
						reg1.setReadType(om.readValue(readType, CodeDescription.class));
					} catch (Exception e) {
						return new Result(false, null, e.getMessage());
					}
				}
			}
			
			if (reg1.getMrSource() == null || reg1.getMrSource().getCode() == null || reg1.getMrSource().getCode().isEmpty()) {
				return new Result(false, null, "No register 1 mr source information provided");
			} else {
				String mrSource = cwMeterRestInterface.getMRSource(reg1.getMrSource().getCode());
				
				if (mrSource == null || mrSource.isEmpty()) {
					return new Result(false, null, "Invalid register 1 mr source provided");
				} else {
					try {
						reg1.setMrSource(om.readValue(mrSource, CodeDescription.class));
					} catch (Exception e) {
						return new Result(false, null, e.getMessage());
					}
				}
			}	
			
			if (reg1.getSize() == null || reg1.getSize().getCode() == null || reg1.getSize().getCode().isEmpty()) {
				return new Result(false, null, "Invalid or no register 1 size provided");
			} else {
				String size = cwMeterRestInterface.getSize(reg1.getSize().getCode());
				
				if (size == null || size.isEmpty()) {
					return new Result(false, null, "Invalid register 1 size provided");
				} else {
					try {
						reg1.setSize(om.readValue(size, CodeDescription.class));
					} catch (Exception e) {
						return new Result(false, null, e.getMessage());
					}
				}
			}			
			
			if (mh.getCurrentMeter().isCompoundMeter()) {
				Register reg2 = mh.getCurrentMeter().getRegister2();
				
				if (reg2 == null) {
					return new Result(false, null, "No register 2 information was provided for the compound meter");
				} 
				
				if (reg2.getReadType() == null || reg2.getReadType().getCode() == null || reg2.getReadType().getCode().isEmpty()) {
					return new Result(false, null, "No register 2 read type information provided");
				} else {
					String readType = cwMeterRestInterface.getReadType(reg2.getReadType().getCode());
					
					if (readType == null || readType.isEmpty()) {
						return new Result(false, null, "Invalid register 2 read type provided");
					} else {
						try {
							reg2.setReadType(om.readValue(readType, CodeDescription.class));
						} catch (Exception e) {
							return new Result(false, null, e.getMessage());
						}
					}
				}
				
				if (reg2.getMrSource() == null || reg2.getMrSource().getCode() == null || reg2.getMrSource().getCode().isEmpty()) {
					return new Result(false, null, "No register 2 mr source information provided");
				} else {
					String mrSource = cwMeterRestInterface.getMRSource(reg2.getMrSource().getCode());
					
					if (mrSource == null || mrSource.isEmpty()) {
						return new Result(false, null, "Invalid register 2 mr source provided");
					} else {
						try {
							reg2.setMrSource(om.readValue(mrSource, CodeDescription.class));
						} catch (Exception e) {
							return new Result(false, null, e.getMessage());
						}
					}
				}	
				
				if (reg2.getSize() == null || reg2.getSize().getCode() == null || reg2.getSize().getCode().isEmpty()) {
					return new Result(false, null, "Invalid or no register 2 size provided");
				} else {
					String size = cwMeterRestInterface.getSize(reg2.getSize().getCode());
					
					if (size == null || size.isEmpty()) {
						return new Result(false, null, "Invalid register 2 size provided");
					} else {
						try {
							reg2.setSize(om.readValue(size, CodeDescription.class));
						} catch (Exception e) {
							return new Result(false, null, e.getMessage());
						}
					}
				}					
			}
		
		} else if (mh.getFieldActivityType().getStep1() == null || mh.getFieldActivityType().getStep1().getAction() == null) {
			return new Result(false, null, "A step1 and corresponding action must be provided if current meter information is not provided");
		
		} else if (mh.getFieldActivityType().getStep1().getAction().equals("MR") || mh.getFieldActivityType().getStep1().getAction().equals("RM")) {
			return new Result(false, null, "Field activity type specfied requires current meter information but none was provided");
		}
			
		return new Result(true, null, null);
	}
}