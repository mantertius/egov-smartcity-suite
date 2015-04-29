package org.egov.works.web.actions.milestone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.web.actions.BaseFormAction;
import org.egov.works.models.estimate.AbstractEstimate;
import org.egov.works.models.estimate.ProjectCode;
import org.egov.works.models.masters.MilestoneTemplate;
import org.egov.works.models.milestone.Milestone;
import org.egov.works.models.workorder.WorkOrder;
import org.egov.works.utils.WorksConstants;

public class AjaxMilestoneAction extends BaseFormAction {
	
	private MilestoneTemplate milestoneTemplate=new MilestoneTemplate();
    private static final String MILESTONECHECK = "milestoneCheck";
    private static final String SEARCH_RESULTS = "searchResults";
    private static final String ACTIVITIES="activities";
    private int status;
    private String code;
    private long workTypeId;
    private long subTypeId;
    private String query;
    private Long workOrderEstimateId;
    private boolean milestoneexistsOrNot;
    private boolean woWorkCommenced;
    private static final String OBJECT_TYPE = "WorkOrder";
    private List<Milestone> workOrdEstList = new LinkedList<Milestone>();
    private String workOrderEstimates;
    private List<ProjectCode> projectCodeList=new LinkedList<ProjectCode>();
    private List<WorkOrder> workOrderList = new LinkedList<WorkOrder>();
	private List<AbstractEstimate> estimateList = new LinkedList<AbstractEstimate>();

    public Object getModel() {
        // TODO Auto-generated method stub
        return milestoneTemplate;
    }
    
	public String searchAjax(){
		return SEARCH_RESULTS;
	}
	
	public Collection<MilestoneTemplate> getMilestoneTemplateList(){
		String strquery="";
		ArrayList<Object> params=new ArrayList<Object>();
		if(workTypeId>0){
			strquery="from MilestoneTemplate mt where upper(mt.code) like '%'||?||'%'"+" and mt.workType.id=?";
			params.add(query.toUpperCase());
			params.add(workTypeId);
		}
		if(subTypeId>0){
			strquery+=" and mt.subType.id=?";
			params.add(subTypeId);
		}
		return getPersistenceService().findAllBy(strquery,params.toArray());
	}
	public String findByCode(){
		milestoneTemplate=(MilestoneTemplate) getPersistenceService().find("from MilestoneTemplate where upper(code)=?", code.toUpperCase());
		
		return ACTIVITIES;
	}
	public String checkMilestone(){
		getMilestoneCheck();
        return MILESTONECHECK;
    }

    public void getMilestoneCheck() {
    	milestoneexistsOrNot = false;
    	woWorkCommenced=false;
    	Long milestoneId=null;
    	if(workOrderEstimateId!=null){
    		if(getPersistenceService().find("from Milestone where workOrderEstimate.id=? and egwStatus.code<>?", workOrderEstimateId,"CANCELLED")!=null){
    			milestoneId=(Long) getPersistenceService().find("select m.id from Milestone m where m.workOrderEstimate.id=? and egwStatus.code<>?", workOrderEstimateId,"CANCELLED");
    		}
    	}
    	if(milestoneId!=null){
    		milestoneexistsOrNot=true;
    		if(getPersistenceService().find("from WorkOrderEstimate as woe where woe.workOrder.egwStatus.code=?  and woe.id=? and woe.workOrder.id in ( select stat.objectId from " +
    				" SetStatus stat where stat.egwStatus.code= ? and stat.id = (select max(stat1.id) from SetStatus stat1 where stat1.objectType='"+OBJECT_TYPE+"' and woe.workOrder.id=stat1.objectId))","APPROVED",workOrderEstimateId,"Work commenced")!=null){
    			woWorkCommenced=true;
    		}
    	}
    	else{
    		milestoneexistsOrNot=false;
    		woWorkCommenced=false;
    	}
    }
    
	public String searchProjectCodeForMileStone(){
		if(!StringUtils.isEmpty(query))
		{
			String strquery="";
			ArrayList<Object> params=new ArrayList<Object>();
			strquery="select distinct(ms.workOrderEstimate.estimate.projectCode) from Milestone ms where upper(ms.workOrderEstimate.estimate.projectCode.code) like '%'||?||'%'"+" and ms.workOrderEstimate.estimate.egwStatus.code=? and ms.egwStatus.code=?";
			params.add(query.toUpperCase());
			params.add(WorksConstants.ADMIN_SANCTIONED_STATUS);
			params.add(WorksConstants.APPROVED);
			projectCodeList=getPersistenceService().findAllBy(strquery,params.toArray());
		}	
		return "projectCodeSearchResults";
	}
	
	public String searchEstimateForMileStone(){
		if(!StringUtils.isEmpty(query))
		{
			String strquery="";
			ArrayList<Object> params=new ArrayList<Object>();
			strquery="select ms.workOrderEstimate.estimate from Milestone ms where upper(ms.workOrderEstimate.estimate.estimateNumber) like '%'||?||'%'"+" and ms.workOrderEstimate.estimate.egwStatus.code=? and ms.egwStatus.code=? ";
			params.add(query.toUpperCase());
			params.add(WorksConstants.ADMIN_SANCTIONED_STATUS);
			params.add(WorksConstants.APPROVED);
			estimateList=getPersistenceService().findAllBy(strquery,params.toArray());
		}	
		return "estimateSearchResults";
	}
	
	public String searchWorkOrdNumForMileStone() {
		if(!StringUtils.isEmpty(query))
		{
			String strquery="";
			ArrayList<Object> params=new ArrayList<Object>();
			strquery="select distinct(ms.workOrderEstimate.workOrder) from Milestone ms where upper(ms.workOrderEstimate.workOrder.workOrderNumber) like '%'||?||'%'"+" and ms.workOrderEstimate.workOrder.egwStatus.code=? and ms.egwStatus.code=?";
			params.add(query.toUpperCase());
			params.add(WorksConstants.APPROVED);
			params.add(WorksConstants.APPROVED);
			workOrderList=getPersistenceService().findAllBy(strquery,params.toArray());
		}	
		return "workOrderNoSearchResults";
	}
	
	public MilestoneTemplate getMilestoneTemplate() {
		return milestoneTemplate;
	}

	public void setMilestoneTemplate(MilestoneTemplate milestoneTemplate) {
		this.milestoneTemplate = milestoneTemplate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getWorkTypeId() {
		return workTypeId;
	}

	public void setWorkTypeId(long workTypeId) {
		this.workTypeId = workTypeId;
	}

	public long getSubTypeId() {
		return subTypeId;
	}

	public void setSubTypeId(long subTypeId) {
		this.subTypeId = subTypeId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Long getWorkOrderEstimateId() {
		return workOrderEstimateId;
	}

	public void setWorkOrderEstimateId(Long workOrderEstimateId) {
		this.workOrderEstimateId = workOrderEstimateId;
	}

	public boolean isMilestoneexistsOrNot() {
		return milestoneexistsOrNot;
	}

	public void setMilestoneexistsOrNot(boolean milestoneexistsOrNot) {
		this.milestoneexistsOrNot = milestoneexistsOrNot;
	}

	public boolean isWoWorkCommenced() {
		return woWorkCommenced;
	}

	public void setWoWorkCommenced(boolean woWorkCommenced) {
		this.woWorkCommenced = woWorkCommenced;
	}

	public List<Milestone> getWorkOrdEstList() {
		return workOrdEstList;
	}

	public void setWorkOrdEstList(List<Milestone> workOrdEstList) {
		this.workOrdEstList = workOrdEstList;
	}

	public String getWorkOrderEstimates() {
		return workOrderEstimates;
	}

	public void setWorkOrderEstimates(String workOrderEstimates) {
		this.workOrderEstimates = workOrderEstimates;
	}

	public List<ProjectCode> getProjectCodeList() {
		return projectCodeList;
	}

	public void setProjectCodeList(List<ProjectCode> projectCodeList) {
		this.projectCodeList = projectCodeList;
	}

	public List<WorkOrder> getWorkOrderList() {
		return workOrderList;
	}

	public void setWorkOrderList(List<WorkOrder> workOrderList) {
		this.workOrderList = workOrderList;
	}

	public List<AbstractEstimate> getEstimateList() {
		return estimateList;
	}

	public void setEstimateList(List<AbstractEstimate> estimateList) {
		this.estimateList = estimateList;
	}


}
