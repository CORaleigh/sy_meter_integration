package gov.raleighnc.switchyard.integration.service.cwmeter.composer;

import gov.raleighnc.switchyard.integration.domain.ccb.CcbCwWorkOrder;

import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.composer.RESTEasyMessageComposer;

/**
 * Tailored message composer for translation to/from domain objects and json strings.
 * 
 * @author mikev
 */
public class CwMeterServiceRestComposer extends RESTEasyMessageComposer {
	 /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(RESTEasyBindingData source, Exchange exchange) throws Exception {
        final Message message = super.compose(source, exchange);
        
        if (source.getOperationName().equals("createWorkOrder")) {
        	if (source.getParameters().length == 1) {
        		message.setContent((CcbCwWorkOrder)source.getParameters()[0]);
        	}
    	} else if (source.getParameters().length == 1 && (source.getParameters()[0] instanceof List)) {
    		message.setContent(source.getParameters()[0]);
    	}
    
        return message;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RESTEasyBindingData decompose(Exchange exchange, RESTEasyBindingData target) throws Exception {
        Object content = exchange.getMessage().getContent();

        target = super.decompose(exchange, target);

        if (target.getOperationName().equals("createWorkOrder")
                && (content != null) && (content instanceof CcbCwWorkOrder)) {
                // Unwrap the parameters
                target.setParameters(
                	// ccbcw
            		new Object[] {
            			// meter header
        				new Object[] {
    						((CcbCwWorkOrder)content).getMeterHeader().getFieldActivityId(),
    						((CcbCwWorkOrder)content).getMeterHeader().getFieldActivityType(),
    						((CcbCwWorkOrder)content).getMeterHeader().getCustomerName(),
    						((CcbCwWorkOrder)content).getMeterHeader().getCustomerPhone(),
    						((CcbCwWorkOrder)content).getMeterHeader().isLifeSupport(),
    						((CcbCwWorkOrder)content).getMeterHeader().getAddress(),
    						((CcbCwWorkOrder)content).getMeterHeader().getFaInstructions(),
    						((CcbCwWorkOrder)content).getMeterHeader().getFaComments(),
    						((CcbCwWorkOrder)content).getMeterHeader().getSpLocationDetails(),
    						((CcbCwWorkOrder)content).getMeterHeader().getSpType(),
    						((CcbCwWorkOrder)content).getMeterHeader().getFaRemark(),
    						((CcbCwWorkOrder)content).getMeterHeader().getSpSourceStatus(),
    						((CcbCwWorkOrder)content).getMeterHeader().getDisconnectLocation(),
    						((CcbCwWorkOrder)content).getMeterHeader().getAdjustmentType(),
    						((CcbCwWorkOrder)content).getMeterHeader().getLetterType(),
    						((CcbCwWorkOrder)content).getMeterHeader().getToDoType(),
    						((CcbCwWorkOrder)content).getMeterHeader().getAdjustmentValue(),
    						((CcbCwWorkOrder)content).getMeterHeader().getLetterValue(),
    						((CcbCwWorkOrder)content).getMeterHeader().getToDoValue(),
    						// current meter
    						new Object[] {
								((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getMeterId(),
								((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().isRemoveMeter(),
								((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().isPerformDeviceTest(),
								((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().isCompoundMeter(),
								((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().isReadDateTime(),
								// register 1
	    						new Object[] {
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister1().getReading(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister1().getDials(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister1().getMiu(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister1().getReadType(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister1().getMrSource(),
	    						},
								// register 2
	    						new Object[] {
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister2().getReading(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister2().getDials(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister2().getMiu(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister2().getReadType(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getRegister2().getMrSource(),
	    						},    	    						
								// device test
	    						new Object[] {
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getTestStart(),
									((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getTestEnd(),
									// register 1 before
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1Before().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1Before().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1Before().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1Before().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1Before().getMrSource(),
		    						},		
									// register 2 before
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before().getMrSource(),
		    						},		
									// register 1 after
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1After().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1After().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1After().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1After().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1After().getMrSource(),
		    						},		
									// register 2 after
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After().getMrSource(),
		    						},			    						
		    						((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getTestStatus(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getLowAccuracy(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getMidAccuracy(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getHighAccuracy(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getResults(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getCurrentMeter().getDeviceTest().getNotes(),
	    						},    	    						
    						},
    						// install meter
    						new Object[] {
								((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getMeterId(),
								((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().isRemoveMeter(),
								((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().isPerformDeviceTest(),
								((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().isCompoundMeter(),
								((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().isReadDateTime(),
								// register 1
	    						new Object[] {
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister1().getReading(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister1().getDials(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister1().getMiu(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister1().getReadType(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister1().getMrSource(),
	    						},
								// register 2
	    						new Object[] {
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister2().getReading(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister2().getDials(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister2().getMiu(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister2().getReadType(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getRegister2().getMrSource(),
	    						},    	    						
								// device test
	    						new Object[] {
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getTestStart(),
									((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getTestEnd(),
									// register 1 before
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1Before().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1Before().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1Before().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1Before().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1Before().getMrSource(),
		    						},		
									// register 2 before
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2Before().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2Before().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2Before().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2Before().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2Before().getMrSource(),
		    						},		
									// register 1 after
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1After().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1After().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1After().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1After().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister1After().getMrSource(),
		    						},		
									// register 2 after
		    						new Object[] {
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2After().getReading(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2After().getDials(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2After().getMiu(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2After().getReadType(),
										((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getRegister2After().getMrSource(),
		    						},			    						
		    						((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getTestStatus(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getLowAccuracy(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getMidAccuracy(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getHighAccuracy(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getResults(),
		    						((CcbCwWorkOrder)content).getMeterHeader().getInstallMeter().getDeviceTest().getNotes(),
	    						},  								
    						},        						
    					},      
        					
            			// workorder
    					new Object[] {
		            		((CcbCwWorkOrder)content).getWorkOrder().getWorkOrderId(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getDescription(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getSupervisor(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getRequestedBy(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getInitiatedBy(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getInitiateDate(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getLocation(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getProjectStartDate(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getProjectFinishDate(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getPriority(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getNumDaysBefore(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getWoCategory(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getSubmitTo(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getStatus(),
		            		((CcbCwWorkOrder)content).getWorkOrder().getWoTemplateId()
    					}
            		}
        		);
            } 
        
        return target;
    }
}