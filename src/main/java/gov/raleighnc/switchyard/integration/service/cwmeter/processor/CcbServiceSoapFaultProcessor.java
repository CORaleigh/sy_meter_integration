package gov.raleighnc.switchyard.integration.service.cwmeter.processor;

import gov.raleighnc.switchyard.integration.domain.Result;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.component.soap.composer.SOAPComposition;
import org.switchyard.component.soap.composer.SOAPFaultInfo;

/**
 * Switchyard Camel processor for handling SOAP faults from CCB.
 * 
 * @author mikev
 *
 */
@Named("ccbServiceSoapFaultProcessor")
public class CcbServiceSoapFaultProcessor implements Processor {
	private static final String TEXT = "Text:";
	private static final String DESCRIPTION = "Description:";

    @Override
    public void process(Exchange exchange) throws Exception {
    	Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
    	
    	// although get fault below, not used since the fault format coming in is invalid so it's parsed with null values
    	SOAPFaultInfo fault = exchange.getProperty(SOAPComposition.SOAP_FAULT_INFO, SOAPFaultInfo.class);
    	
        Result result = new Result();
        result.setSuccess(false);
        String errorMessage = caused.getMessage();

        // parse out the error message
        if (errorMessage.contains(TEXT) && errorMessage.contains(DESCRIPTION)) {
        	int startIndex = errorMessage.indexOf(TEXT);
            int endIndex = errorMessage.indexOf(DESCRIPTION);
            result.setException(errorMessage.substring(startIndex, endIndex).trim());
        }
        
        exchange.getOut().setBody(result);
    }
}