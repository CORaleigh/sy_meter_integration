package gov.raleighnc.switchyard.integration.service.cwmeter;

import gov.raleighnc.switchyard.integration.domain.Result;
import gov.raleighnc.switchyard.integration.domain.ccb.CcbCwWorkOrder;

/**
 * A Switchyard "Composite Service" consisting of CC&B-related services usable by the outside world.
 * 
 * @author mikev
 *
 */
public interface CcbService {
	/**
	 * Closes a field activity in the CC&B system and returns a successful or an error message as a Result.
	 * 
	 * @param workorder The WorkOrder containing WO and Meter information to close
	 * @return The result of the closing of the field activity
	 */	
	Result closeFieldActivity(CcbCwWorkOrder workorder);
}