package gov.raleighnc.switchyard.integration.service.cwmeter.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import gov.raleighnc.switchyard.integration.domain.ccb.CcbCwWorkOrder;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Switchyard Camel processor for handling REST-based requests directed to CCB's SOAP services.
 * Essentially we are grabbing the values and creating a SOAP message response according to CCB's API.
 * This is the current SOAP XML with corresponding mapping:
 * 
 * <CMCompleteFieldActivity>
    <fieldActivityId>[FA ID]</fieldActivityId>
    <workOrderId>[CW WO ID]</workOrderId>
    <meterLocation>[SP location details]</meterLocation>  
    <servicePointSourceStatus>[SPSourceStatus]</servicePointSourceStatus> 
    <servicePointDisconnectLocation>[DisconnectLocation]</servicePointDisconnectLocation> 
    <customerContactLogInformation>[LEAVE_BLANK_FOR_NOW]</customerContactLogInformation> 
    <interfaceLogs>[LEAVE_BLANK_FOR_NOW]</interfaceLogs> 
    <meterInstallDate>[LEAVE_BLANK]</meterInstallDate> 
    <fieldActivityRemarks>[faRemark]</fieldActivityRemarks> 
    <workDateTime>new Date()</workDateTime> 
    <workedBy>[CW WorkOrderCompletedBy]</workedBy> <!-- needs to match up with CCB name -->
    <comments>[FAComments]</comments> 
    <meterReadInformation> 
		<!-- dynamically generate elements based on FA type (steps and if they were actually performed for optional steps) -->
        <meterRead> <!-- read -->
            <meterBadgeNumber>[Badge Number]</meterBadgeNumber>
            <readDateTime>[LEAVE_BLANK]</readDateTime>  
            <readType>[ReadType]</readType>  
            <mrSource>[MrSource]</mrSource>  
            <useOnBill>true</useOnBill>  
            <onOff>1</onOff>  
            <registerRead> 
                <registerSequence>[register 1 -- use 1]</registerSequence> 
                <currentMeterRead>[Reading]</currentMeterRead> 
                <miu>[miu]</miu>
            </registerRead>
            <registerRead> 
                <registerSequence>[register 2 -- use 2 (if compound)]</registerSequence> 
                <currentMeterRead>[Reading]</currentMeterRead> 
                <miu>[miu]</miu>
            </registerRead> 
        </meterRead> 

        <meterRead>  <!-- remove if populated -->
            <meterBadgeNumber>[Badge Number]</meterBadgeNumber>
            <readDateTime>[LEAVE_BLANK]</readDateTime>  
            <readType>[ReadType]</readType>  
            <mrSource>[MrSource]</mrSource>  
            <useOnBill>true</useOnBill>  
            <onOff>1</onOff>  
            <registerRead> 
                <registerSequence>[register 1 -- use 1]</registerSequence> 
                <currentMeterRead>[Reading]</currentMeterRead> 
                <miu>[miu]</miu>
    		</registerRead>
            <registerRead>     
                <registerSequence>[register 2 -- use 2 (if compound)]</registerSequence> 
                <currentMeterRead>[Reading]</currentMeterRead> 
                <miu>[miu]</miu>
            </registerRead> 
        </meterRead> 


        <meterRead> <!-- install if populated -->
            <meterBadgeNumber>[Badge Number]</meterBadgeNumber>
            <readDateTime>[LEAVE_BLANK]</readDateTime>  
            <readType>[ReadType]</readType>  
            <mrSource>[MrSource]</mrSource>  
            <useOnBill>true</useOnBill>  
            <onOff>1</onOff>
            <registerRead> 
                <registerSequence>[register 1 -- use 1]</registerSequence> 
                <currentMeterRead>[Reading]</currentMeterRead> 
                <miu>[miu]</miu>
            </registerRead>
            <registerRead>                
                <registerSequence>[register 2 -- use 2 (if compound)]</registerSequence> 
                <currentMeterRead>[Reading]</currentMeterRead> 
                <miu>[miu]</miu>
            </registerRead> 
        </meterRead> 

    </meterReadInformation>  

	<characteristic> <!-- if adjustment / fees exists  -->
		<characteristicType>NO_TODO</characteristicType>
		<characteristicValue>YES</characteristicValue>
	</characteristic>

	<characteristic> <!-- if letters exists  -->
		<characteristicType>NO_TODO</characteristicType>
		<characteristicValue>YES</characteristicValue>
	</characteristic>

	<characteristic> <!-- if todos / alerts exists  -->
		<characteristicType>NO_TODO</characteristicType>
		<characteristicValue>YES</characteristicValue>
	</characteristic>

    <deviceTestInformation>  
		<deviceTestResult>[TestStatus]</deviceTestResult>
		<deviceTest>
			<sequence>10</sequence>
			<readingBefore>[register1Before, register2Before (optional if reg 2 used)]</readingBefore>
			<lowFlowAccuracy>[lowAccuracy]</lowFlowAccuracy>
			<midFlowAccuracy>[midAccuracy]</midFlowAccuracy>
			<highFlowAccuracy>[highAccuracy]</highFlowAccuracy>
			<readingAfter>[register1After, register2After (optional if reg 2 used)]</readingAfter>
			<notes>[Notes]</notes>
		</deviceTest>
    </deviceTestInformation>  
    <stockLocation>[STCK (if removing a meter) otherwise LEAVE_BLANK]</stockLocation> 
</CMCompleteFieldActivity>
 * 
 * @author mikev
 *
 */
@Named("ccbServiceRestProcessor")
public class CcbServiceRestProcessor implements Processor {
	private static final String IM = "IM";
	private static final String RM = "RM";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");  // ccb timestamp format 
	
	/**
	 * Helper method to return blank values for long values that are 0
	 *  
	 * @param value The long value to check
	 * @return Appropriate string value
	 */
	protected String processLongToString(long value) {
		if (value == 0) {
			return "";
		} else {
			return String.valueOf(value);
		}
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
    	CcbCwWorkOrder ccbcw = exchange.getIn().getBody(CcbCwWorkOrder.class);
    	   	               
    	// used to build SOAP message payload
        StringBuilder sb = new StringBuilder();
        
        sb.append("<CMCompleteFieldActivity>");
	        sb.append("<fieldActivityId>");
	        sb.append(ccbcw.getMeterHeader().getFieldActivityId());
	        sb.append("</fieldActivityId>");
	        sb.append("<workOrderId>");
	        sb.append(ccbcw.getWorkOrder().getWorkOrderId());
	        sb.append("</workOrderId>");
	        sb.append("<meterLocation>");
	        sb.append(ccbcw.getMeterHeader().getSpLocationDetails());
	        sb.append("</meterLocation>");                
	        sb.append("<servicePointSourceStatus>");
	        if (ccbcw.getMeterHeader().getSpSourceStatus() != null) {
	        	sb.append(ccbcw.getMeterHeader().getSpSourceStatus().getCode());
	        } else {
	        	sb.append("");
	        }
	        sb.append("</servicePointSourceStatus>");       
	        sb.append("<servicePointDisconnectLocation>");
	        if (ccbcw.getMeterHeader().getDisconnectLocation() != null) {
	        	sb.append(ccbcw.getMeterHeader().getDisconnectLocation().getCode());
	        } else {
	        	sb.append("");
	        }
	        sb.append("</servicePointDisconnectLocation>");            
	        sb.append("<customerContactLogInformation>");
	        sb.append("");
	        sb.append("</customerContactLogInformation>");        
	        sb.append("<interfaceLogs>");
	        sb.append("");
	        sb.append("</interfaceLogs>");        
	        sb.append("<meterInstallDate>");
	        sb.append("");
	        sb.append("</meterInstallDate>");        
	        sb.append("<fieldActivityRemarks>");
	        if (ccbcw.getMeterHeader().getFaRemark() != null) {
	        	sb.append(ccbcw.getMeterHeader().getFaRemark().getCode());
	        } else {
	        	sb.append("");
	        }
	        sb.append("</fieldActivityRemarks>");              
	        sb.append("<workDateTime>");
	        sb.append(sdf.format(new Date()));
	        sb.append("</workDateTime>");        
	        sb.append("<workedBy>");
	        sb.append(ccbcw.getWorkOrder().getWorkCompletedBy());
	        sb.append("</workedBy>");        
	        sb.append("<comments>");
	        sb.append(ccbcw.getMeterHeader().getFaComments());
	        sb.append("</comments>");     
	        
	        // check for meter information
	        sb.append("<meterReadInformation>");
	        	// step 1
		        sb.append("<meterRead>");
			        sb.append("<meterBadgeNumber>");
			        if (ccbcw.getMeterHeader().getFieldActivityType().getStep1().getAction().equals(IM) 
		        		&& ccbcw.getMeterHeader().getInstallMeter() != null) {
			        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getBadgeNumber());
			        } else {
			        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getBadgeNumber());
			        }
			        sb.append("</meterBadgeNumber>");  
			        sb.append("<readDateTime>");  
			        sb.append("");        
			        sb.append("</readDateTime>");        
			        sb.append("<readType>");  
			        if (ccbcw.getMeterHeader().getFieldActivityType().getStep1().getAction().equals(IM) 
			        	&& ccbcw.getMeterHeader().getInstallMeter() != null
			        	&& ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReadType() != null
			        	&& ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReadType().getCode() != null) {
			        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReadType().getCode());
			        } else {
			        	if (ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReadType() != null && ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReadType().getCode() != null)
			        	{
			        		sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReadType().getCode());
			        	}
			        }
			        sb.append("</readType>");       
			        sb.append("<mrSource>");  
			        if (ccbcw.getMeterHeader().getFieldActivityType().getStep1().getAction().equals(IM)
		        		&& ccbcw.getMeterHeader().getInstallMeter() != null 
		        		&& ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMrSource() != null
		        		&& ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMrSource().getCode() != null) {
			        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMrSource().getCode());
			        } else {
			        	if (ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMrSource() != null && ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMrSource().getCode() != null)
			        	{
			        		sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMrSource().getCode());
			        	}
			        }
			        sb.append("</mrSource>");     
			        sb.append("<useOnBill>");  
			        sb.append("true");        
			        sb.append("</useOnBill>");         
			        sb.append("<onOff>");  
		        	sb.append("1");
			        sb.append("</onOff>");     
			        sb.append("<registerRead>");  
				        sb.append("<registerSequence>");
				        sb.append("1");        
				        sb.append("</registerSequence>");        
				        sb.append("<currentMeterRead>");
				        if (ccbcw.getMeterHeader().getFieldActivityType().getStep1().getAction().equals(IM)
			        		&& ccbcw.getMeterHeader().getInstallMeter() != null) {
				        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReading());
				        } else {
				        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReading());
				        }
				        sb.append("</currentMeterRead>");        
				        sb.append("<miu>");
				        if (ccbcw.getMeterHeader().getFieldActivityType().getStep1().getAction().equals(IM)
			        		&& ccbcw.getMeterHeader().getInstallMeter() != null) {
				        	sb.append(processLongToString(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMiu()));
				        } else {
				        	sb.append(processLongToString(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMiu()));
				        }
				        sb.append("</miu>");
		        	sb.append("</registerRead>");
			        if (ccbcw.getMeterHeader().getFieldActivityType().getStep1().getAction().equals(IM) 
		        		&& ccbcw.getMeterHeader().getInstallMeter() != null				        		
		        		&& ccbcw.getMeterHeader().getInstallMeter().getRegister2() != null 
		        		&& ccbcw.getMeterHeader().getInstallMeter().getRegister2().getReading() > 0) {
			        	sb.append("<registerRead>");
				        	sb.append("<registerSequence>");
					        sb.append("2");        
					        sb.append("</registerSequence>");
					        sb.append("<currentMeterRead>");
					        sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister2().getReading());
					        sb.append("</currentMeterRead>");        
					        sb.append("<miu>");
					        sb.append(processLongToString(ccbcw.getMeterHeader().getInstallMeter().getRegister2().getMiu()));
					        sb.append("</miu>");
				        sb.append("</registerRead>");
			        } else if (ccbcw.getMeterHeader().getCurrentMeter().getRegister2() != null 
		        		&& ccbcw.getMeterHeader().getCurrentMeter().getRegister2().getReading() > 0) {
			        	sb.append("<registerRead>");
				        	sb.append("<registerSequence>");
					        sb.append("2");        
					        sb.append("</registerSequence>");
					        sb.append("<currentMeterRead>");
					        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister2().getReading());
					        sb.append("</currentMeterRead>");        
					        sb.append("<miu>");
					        sb.append(processLongToString(ccbcw.getMeterHeader().getCurrentMeter().getRegister2().getMiu()));
					        sb.append("</miu>");
				        sb.append("</registerRead>");
			        }
        
		        sb.append("</meterRead>"); 
		        
		        // step 2 (RM)
		        if (ccbcw.getMeterHeader().getFieldActivityType().getStep2() != null
	        		&& ccbcw.getMeterHeader().getFieldActivityType().getStep2().getAction() != null 
		        	&& ccbcw.getMeterHeader().getFieldActivityType().getStep2().getAction().equals(RM)
	        		&& ccbcw.getMeterHeader().getCurrentMeter().isRemoveMeter()) {
		        		
			        sb.append("<meterRead>");
				        sb.append("<meterBadgeNumber>");
			        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getBadgeNumber());
				        sb.append("</meterBadgeNumber>");  
				        sb.append("<readDateTime>");  
				        sb.append("");        
				        sb.append("</readDateTime>");        
				        sb.append("<readType>");  
				        if (ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReadType() != null && ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReadType().getCode() != null)
				        {
				        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReadType().getCode());
				        }
				        sb.append("</readType>");       
				        sb.append("<mrSource>");  
				        if (ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMrSource() != null && ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMrSource().getCode() != null)
				        {
				        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMrSource().getCode());
				        }
				        sb.append("</mrSource>");     
				        sb.append("<useOnBill>");  
				        sb.append("true");        
				        sb.append("</useOnBill>");         
				        sb.append("<onOff>");  
			        	sb.append("1");
				        sb.append("</onOff>");     
				        sb.append("<registerRead>");  
					        sb.append("<registerSequence>");
					        sb.append("1");        
					        sb.append("</registerSequence>");        
					        sb.append("<currentMeterRead>");
				        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getReading());
					        sb.append("</currentMeterRead>");        
					        sb.append("<miu>");
				        	sb.append(processLongToString(ccbcw.getMeterHeader().getCurrentMeter().getRegister1().getMiu()));
					        sb.append("</miu>");
				        sb.append("</registerRead>");
				        
				        if (ccbcw.getMeterHeader().getCurrentMeter().getRegister2() != null 
			        		&& ccbcw.getMeterHeader().getCurrentMeter().getRegister2().getReading() > 0) {
				        	sb.append("<registerRead>"); 
					        	sb.append("<registerSequence>");
						        sb.append("2");        
						        sb.append("</registerSequence>");
						        sb.append("<currentMeterRead>");
						        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getRegister2().getReading());
						        sb.append("</currentMeterRead>");        
						        sb.append("<miu>");
						        sb.append(processLongToString(ccbcw.getMeterHeader().getCurrentMeter().getRegister2().getMiu()));
						        sb.append("</miu>");
					        sb.append("</registerRead>"); 
				        }       
			        sb.append("</meterRead>"); 		    
		        }
		        
		        // step 3 (IM)
		        if (ccbcw.getMeterHeader().getFieldActivityType().getStep3() != null
		        	&& ccbcw.getMeterHeader().getFieldActivityType().getStep3().getAction() != null 
		        	&& ccbcw.getMeterHeader().getFieldActivityType().getStep3().getAction().equals(IM)
	        		&& ccbcw.getMeterHeader().getInstallMeter() != null
	        		&& ccbcw.getMeterHeader().getInstallMeter().getBadgeNumber() > 0) {
		        		
			        sb.append("<meterRead>");
				        sb.append("<meterBadgeNumber>");
			        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getBadgeNumber());
				        sb.append("</meterBadgeNumber>");  
				        sb.append("<readDateTime>");  
				        sb.append("");        
				        sb.append("</readDateTime>");        
				        sb.append("<readType>");  
				        if (ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReadType() != null && ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReadType().getCode() != null)
				        {
				        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReadType().getCode());
				        }
				        sb.append("</readType>");       
				        sb.append("<mrSource>");  
				        if (ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMrSource() != null && ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMrSource().getCode() != null)
				        {
				        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMrSource().getCode());
				        }
				        sb.append("</mrSource>");     
				        sb.append("<useOnBill>");  
				        sb.append("true");        
				        sb.append("</useOnBill>");         
				        sb.append("<onOff>");  
			        	sb.append("1");
				        sb.append("</onOff>");     
				        sb.append("<registerRead>");  
					        sb.append("<registerSequence>");
					        sb.append("1");        
					        sb.append("</registerSequence>");        
					        sb.append("<currentMeterRead>");
				        	sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getReading());
					        sb.append("</currentMeterRead>");        
					        sb.append("<miu>");
				        	sb.append(processLongToString(ccbcw.getMeterHeader().getInstallMeter().getRegister1().getMiu()));
					        sb.append("</miu>");
				        sb.append("</registerRead>");
				        
				        if (ccbcw.getMeterHeader().getInstallMeter().getRegister2() != null 
			        		&& ccbcw.getMeterHeader().getInstallMeter().getRegister2().getReading() > 0) {
				        	sb.append("<registerRead>");
					        	sb.append("<registerSequence>");
						        sb.append("2");        
						        sb.append("</registerSequence>");
						        sb.append("<currentMeterRead>");
						        sb.append(ccbcw.getMeterHeader().getInstallMeter().getRegister2().getReading());
						        sb.append("</currentMeterRead>");        
						        sb.append("<miu>");
						        sb.append(processLongToString(ccbcw.getMeterHeader().getInstallMeter().getRegister2().getMiu()));
						        sb.append("</miu>");
					        sb.append("</registerRead>");
				        }        
			        sb.append("</meterRead>"); 		    
		        }
	        sb.append("</meterReadInformation>");
	        
	        // check adjustment / fees
	        if (ccbcw.getMeterHeader().getAdjustmentType() != null && ccbcw.getMeterHeader().getAdjustmentType().getCode() != null 
        		&& !ccbcw.getMeterHeader().getAdjustmentType().getCode().isEmpty() && ccbcw.getMeterHeader().getAdjustmentValue() != null
        		&& ccbcw.getMeterHeader().getAdjustmentValue().getCode() != null && !ccbcw.getMeterHeader().getAdjustmentValue().getCode().isEmpty())
	        {
	        	sb.append("<characteristic>");
	        		sb.append("<characteristicType>");
	        			sb.append(ccbcw.getMeterHeader().getAdjustmentType().getCode());
	        		sb.append("</characteristicType>");
	        		sb.append("<characteristicValue>");
        				sb.append(ccbcw.getMeterHeader().getAdjustmentValue().getCode());
    				sb.append("</characteristicValue>");	        		
	        	sb.append("</characteristic>");
	        }
	        
	        // check letters
	        if (ccbcw.getMeterHeader().getLetterType() != null && ccbcw.getMeterHeader().getLetterType().getCode() != null 
        		&& !ccbcw.getMeterHeader().getLetterType().getCode().isEmpty() && ccbcw.getMeterHeader().getLetterValue() != null
        		&& ccbcw.getMeterHeader().getLetterValue().getCode() != null && !ccbcw.getMeterHeader().getLetterValue().getCode().isEmpty())
	        {
	        	sb.append("<characteristic>");
	        		sb.append("<characteristicType>");
	        			sb.append(ccbcw.getMeterHeader().getLetterType().getCode());
	        		sb.append("</characteristicType>");
	        		sb.append("<characteristicValue>");
        				sb.append(ccbcw.getMeterHeader().getLetterValue().getCode());
    				sb.append("</characteristicValue>");	        		
	        	sb.append("</characteristic>");
	        }
	        
	        // check todos / alerts
	        if (ccbcw.getMeterHeader().getToDoType() != null && ccbcw.getMeterHeader().getToDoType().getCode() != null 
        		&& !ccbcw.getMeterHeader().getToDoType().getCode().isEmpty() && ccbcw.getMeterHeader().getToDoValue() != null
        		&& ccbcw.getMeterHeader().getToDoValue().getCode() != null && !ccbcw.getMeterHeader().getToDoValue().getCode().isEmpty())
	        {
	        	sb.append("<characteristic>");
	        		sb.append("<characteristicType>");
	        			sb.append(ccbcw.getMeterHeader().getToDoType().getCode());
	        		sb.append("</characteristicType>");
	        		sb.append("<characteristicValue>");
        				sb.append(ccbcw.getMeterHeader().getToDoValue().getCode());
    				sb.append("</characteristicValue>");	        		
	        	sb.append("</characteristic>");
	        }	        
	        
	        // check if device test performed then populate accordingly
	        if (ccbcw.getMeterHeader().getCurrentMeter() != null 
        		&& ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest() != null 
        		&& ccbcw.getMeterHeader().getCurrentMeter().isPerformDeviceTest()) {
		        	sb.append("<deviceTestInformation>");
				        sb.append("<deviceTestResult>");
				        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getTestStatus().getCode());
				        sb.append("</deviceTestResult>");  
				        sb.append("<deviceTest>");
					        sb.append("<sequence>");
					        sb.append("10");        
					        sb.append("</sequence>");    
					        sb.append("<readingBefore>");
					        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1Before().getReading());
					        if (ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before() != null && 
					        		ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before().getReading() > 0) {
					        	sb.append(", ");
					        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2Before().getReading());
					        }
					        sb.append("</readingBefore>");		        
					        sb.append("<lowFlowAccuracy>");
					        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getLowAccuracy());        
					        sb.append("</lowFlowAccuracy>");		        			        
					        sb.append("<midFlowAccuracy>");
					        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getMidAccuracy());        
					        sb.append("</midFlowAccuracy>");			        
					        sb.append("<highFlowAccuracy>");
					        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getHighAccuracy());        
					        sb.append("</highFlowAccuracy>");	
					        sb.append("<readingAfter>");
					        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister1After().getReading());      
					        if (ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After() != null && 
					        		ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After().getReading() > 0) {
					        	sb.append(", ");
					        	sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getRegister2After().getReading());
					        }
					        sb.append("</readingAfter>");			        
					        sb.append("<notes>");
					        sb.append(ccbcw.getMeterHeader().getCurrentMeter().getDeviceTest().getNotes());        
					        sb.append("</notes>");			        
				        sb.append("</deviceTest>");
			        sb.append("</deviceTestInformation>");
	        }
	        
	        sb.append("<stockLocation>");
	        if (ccbcw.getMeterHeader().getCurrentMeter().isRemoveMeter()) {
	        	sb.append("STCK");
	        } else {
	        	sb.append("");
	        }
	        sb.append("</stockLocation>");	        
        sb.append("</CMCompleteFieldActivity>");
        
        exchange.getOut().setBody(sb.toString());
        
        exchange.getOut().getHeaders().put("SOAPAction", "http://ouaf.oracle.com/spl/XAIXapp/xaiserver/CMCompleteFieldActivity");       
        exchange.getOut().getHeaders().put("Content-Type", "text/xml; charset=\"utf-8\"");
    }
}