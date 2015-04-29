package org.egov.works.services;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.egov.works.models.estimate.AbstractEstimate;
import org.egov.works.models.masters.Contractor;
import org.egov.works.models.measurementbook.MBHeader;
import org.egov.works.models.tender.EstimateLineItemsForWP;
import org.egov.works.models.tender.TenderResponse;
import org.egov.works.models.tender.WorksPackage;
import org.egov.works.models.workorder.AbstractEstimateForWp;
import org.egov.works.models.workorder.WorkOrder;
import org.egov.works.models.workorder.WorkOrderActivity;
import org.egov.works.models.workorder.WorkOrderEstimate;

/**
 * This class will declare all the API's related to work order model.
 * @author prashant.gaurav
 *
 */
public interface WorkOrderService extends BaseService<WorkOrder, Long> {
	

	/**
	 * This method will set workorder number to the work order object
	 * @param entity
	 * @param workOrder
	 */
	WorkOrder setWorkOrderNumber(AbstractEstimate entity,WorkOrder workOrder,WorksPackage worksPackage);

	
	/**
	 * This method will return all the contractors which are having active work orders.
	 * @return List of ContractorDetail
	 */
	List<Contractor> getContractorsWithWO();
	
	/**
	 * This method will search list of WO's for the given criteria and eligible for MB.
	 * CriteriaMap may have:CONTRACTOR_ID,CREATE_DATE,TENDER_NO,WORKORDER_NO
	 * @param criteriaMap
	 * @return
	 */
	List<WorkOrder> searchWOForMB(Map<String,Object> criteriaMap);
	
	/**
	 * This method will search list of WO's for the given criteria and eligible for MB.
	 * CriteriaMap may have:CONTRACTOR_ID,CREATE_DATE,TENDER_NO,WORKORDER_NO,PROJECT_CODE
	 * @param criteriaMap
	 * @return
	 */
	List<WorkOrder> searchWOForBilling(Map<String,Object> criteriaMap);
	
	/**
	 * This method will search list of WO's for the given criteria and eligible to be view.
	 * CriteriaMap may have:CONTRACTOR_ID,FROM_DATE,TO_DATE,WORKORDER_NO,STATUS
	 * @param criteriaMap
	 * @return List<WorkOrder> 
	 */
	List<WorkOrder> searchWOToView(Map<String,Object> criteriaMap);
	
	/** using for pagination 
	 * This method will search list of WO's for the given criteria and eligible to be view.
	 * CriteriaMap may have:CONTRACTOR_ID,FROM_DATE,TO_DATE,WORKORDER_NO,STATUS
	 * @param criteriaMap
	 * @return List<WorkOrder> 
	 */
	List<String> searchWOToPaginatedView(Map<String,Object> criteriaMap,List<Object> paramList);
	
	/**
	 * This method will search and return list of woactivity based on searched criteria.
	 * Search criteria: WORKORDER_NO,ACTIVITY_DESC,LINEITEM_CODE
	 * Story #567 Search Line item to record measurement 
	 * @param criteriaMap
	 * @return
	 */
	List<WorkOrderActivity> searchWOActivities(Map<String,Object> criteriaMap);
	
	/**
	 * For the purpose of change quantity in revision estimate
	 * Will get work order activity list for the original work order and subsequent revision work orders
	 * It will not get the activities for which MB is present in workflow
	 * @param criteriaMap
	 * @return
	 */
	List<WorkOrderActivity> searchWOActivitiesForChangeQuantity(Map<String,Object> criteriaMap);
	
	/**
	 * This method will search and return list of woactivity from only revision estimates based on searched criteria.
	 * Search criteria: WORKORDER_NO,ACTIVITY_DESC,LINEITEM_CODE
	 * 
	 * @param criteriaMap
	 * @return
	 */
	List<WorkOrderActivity> searchWOActivitiesFromRevEstimates(Map<String,Object> criteriaMap);
	
	
	/**
	 * This method will return toPageNo for a line item from the last mb entry. 
	 * @param workOrderActivity
	 * @return
	 */
	MBHeader findLastMBPageNoForLineItem(WorkOrderActivity workOrderActivity, Long mbHeaderId);
	
//	/**
//	 * This method will check whether approval limit is already used for all line item for the WO.
//	 * @param woId
//	 * @return
//	 */
//	Boolean isApprovalLimitReachedForWO(Long woId);
	
//	/**
//	 * This method will check whether MB is created and pending for approval for the given WO.
//	 * @param woId
//	 * @return
//	 */
//	Boolean isMBCreatedAndPendingForWO(Long woId);
	
//	/**
//	 * This method will check whether final bill is already approved for wo or not.
//	 * @param woId
//	 * @return
//	 */
//	Boolean isFinalBillApprovedForWO(Long woId);
	
	List<Contractor> getAllContractorForWorkOrder();
	
	/**
	 * Check whether any MB entry is pending for approval for the given WorkOrder
	 * @param woId
	 * @return
	 */
	Boolean isMBInApprovalPendingForWO(String woNumber);
	
	/**
	 * This method will return ActivitiesForWorkorder.
	 * @param tenderResponse
	 * @return Collection<EstimateLineItemsForWP>
	 */

	
	 Collection<EstimateLineItemsForWP> getActivitiesForWorkorder(TenderResponse tenderResponse);
	 
		/**
		 * This method will return Activities For WorksPackage.
		 * @param tenderResponse
		 * @return Collection<EstimateLineItemsForWP>
		 */

		public Collection<EstimateLineItemsForWP> getActivitiesForWorksPackage(TenderResponse tenderResponse);

	 
	 /**
		 * This method will return ActivitiesForWorkorder.
		 * @param WorkOrder
		 * @return Collection<EstimateLineItemsForWP>
		 */
	 Collection<EstimateLineItemsForWP> getActivitiesForWorkorder(WorkOrder workOrder);
	/**
	 * returns headermap for pdf
	 * @param workOrder
	 * @return
	 */
	 
	public Map<String,Object> createHeaderParams(WorkOrder workOrder,String type);
		

	/**
	 * returns AbstractEstimateForWp list
	 * @param workOrder
	 * @return
	 */
	public List<AbstractEstimateForWp>  getAeForWp(WorkOrder workOrder);
	
	/**
	 * gets the security desposit appconfig value
	 * @return Double
	 */
	public Double getSecurityDepositConfValue();
	
	/**
	 * gets the LabourWelfareFund appconfig value
	 * @return Double
	 */
	public Double getLabourWelfareFundConfValue();
	
	/**
	 * Populate all the cumulative fields related to WOA line item
	 * @param workOrderEstimate
	 * @return
	 */
	public WorkOrderEstimate calculateCumulativeDetailsForRE(WorkOrderEstimate workOrderEstimate);
	
	/**
	 * retruns work commenced date for a particular work order by taking id as parameter
	 * @param id
	 * @return work commenced date
	 */
	public Date getWorkCommencedDateByWOId(Long id);
	
	/**
	 * returns the WorksPackage name
	 * @param wpNumber
	 * @return works package name
	 */
	public String getWorksPackageName(String wpNumber);
	
	/**
	 * returns tender type and percentage negotiated amount rate
	 * @param negotiationNo
	 * @return
	 */
	public Object getTenderNegotiationInfo(String negotiationNo);

	/**
	 * returns WorkOrderEstimate based on the workorder id and estimate id
	 * @param workOrderId,estimateId
	 * @return WorkOrderEstimate
	 */
	public WorkOrderEstimate getWorkOrderEstimateForWOIdAndEstimateId(Long workOrderId,Long estimateId);
	
	/**
	 * This function returns list of Objects containing workOder Id and WorkOrder number.
	 * By taking estimateID as paramter.
	 * @param estimateID
	 * @return List<Object> containing workOder Id and WorkOrder number.
	 */
	public List<Object> getWorkOrderDetails(Long estimateId);
}
