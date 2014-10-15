package gov.raleighnc.switchyard.integration.service.cwmeter.composer;

import gov.raleighnc.switchyard.integration.domain.cityworks.workorder.WorkOrder;

import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.composer.RESTEasyMessageComposer;
import org.switchyard.Exchange;
import org.switchyard.Message;

/**
 * Tailored message composer for translation to/from domain objects and json strings.
 * 
 * @author mikev
 */
public class CwWoRestInterfaceComposer extends RESTEasyMessageComposer {
	 /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(RESTEasyBindingData source, Exchange exchange) throws Exception {
        final Message message = super.compose(source, exchange);
        
        if (source.getOperationName().equals("createWorkOrder") || source.getOperationName().equals("updateWorkOrder")) {
        	if (source.getParameters().length == 1) {
        		if (source.getParameters()[0] instanceof WorkOrder) {
        			message.setContent((WorkOrder)source.getParameters()[0]);
        		} else {
        			message.setContent(source.getParameters()[0]);
        		}
        	}
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

        if ((target.getOperationName().equals("createWorkOrder") || target.getOperationName().equals("updateWorkOrder"))
            && (content != null) && (content instanceof WorkOrder)) {
            // Unwrap the parameters
            target.setParameters(new Object[]{
        		((WorkOrder)content).getWorkOrderId(),
        		((WorkOrder)content).getDescription(),
        		((WorkOrder)content).getSupervisor(),
        		((WorkOrder)content).getRequestedBy(),
        		((WorkOrder)content).getInitiatedBy(),
        		((WorkOrder)content).getInitiateDate(),
        		((WorkOrder)content).getLocation(),
        		((WorkOrder)content).getProjectStartDate(),
        		((WorkOrder)content).getProjectFinishDate(),
        		((WorkOrder)content).getPriority(),
        		((WorkOrder)content).getNumDaysBefore(),
        		((WorkOrder)content).getWoCategory(),
        		((WorkOrder)content).getSubmitTo(),
        		((WorkOrder)content).getStatus(),
        		((WorkOrder)content).getWoTemplateId(),
        		((WorkOrder)content).getWoAddress(),
        		((WorkOrder)content).getWoXCoordinate(),
        		((WorkOrder)content).getWoYCoordinate()
    		});
        } 
        
        return target;
    }
}