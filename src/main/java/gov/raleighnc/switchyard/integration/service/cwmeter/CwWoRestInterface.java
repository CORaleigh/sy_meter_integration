package gov.raleighnc.switchyard.integration.service.cwmeter;


/**
 * Switchyard REST-based "Composite Reference" wired to the Cityworks system for using Cityworks custom REST-based WO services only.
 * 
 * @author mikev
 *
 */
public interface CwWoRestInterface {
	/**
	 * Create a new work order in Cityworks.
	 * 
	 * @param workorderJson The WorkOrder to be created as a JSON string
	 * @return The Result as a JSON string
	 */
	String createWorkOrder(String workorderJson);
	
	/**
	 * Update an existing work order in Cityworks.
	 * 
	 * @param workorderJson The WorkOrder to be updated as a JSON string
	 * @return The Result as a JSON string
	 */
	String updateWorkOrder(String workorderJson);
	
	/**
	 * Deletes an existing work order in Cityworks.
	 * 
	 * @param workorderId The id of the existing work order to be deleted
	 * @return The Result as a JSON string
	 */
	String deleteWorkOrder(String workorderId);
	
	/**
	 * Create a new work order entity in Cityworks.
	 * 
	 * @param workorderEntityJson The WorkOrderEntity to be created as a JSON string
	 * @return The Result as a JSON string
	 */
	String createWorkOrderEntity(String workorderEntityJson);
	
    /**
     * Creates a new custom field entry in the Cityworks.
     * 
     * @param wocfJson The WorkOrderCustomField to be created as a JSON string
     * @return The Result as a JSON string
     */
	String createWorkOrderCustomField(String wocfJson);
	
    /**
     * Updates a custom field entry in the Cityworks.
     * 
     * @param wocfJson The WorkOrderCustomField to be updated as a JSON string
     * @return The Result as a JSON string
     */
	String updateWorkOrderCustomField(String wocfJson);	
	
    /**
     * Retrieve a custom field id.
     * 
     * @param wocf The WorkOrderCustomField
     * @return The Result as a JSON string
     */
	String retrieveCustomFieldId(String wocfJson);		
	
    /**
     * Retrieve a category id.
     * 
     * @param wocf The WorkOrderCustomField
     * @return The Result as a JSON string
     */
	String retrieveCategoryId(String wocfJson);		
}