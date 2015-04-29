package org.egov.works.web.actions.measurementbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.egov.commons.EgwStatus;
import org.egov.commons.service.CommonsService;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.workflow.service.WorkflowService;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.services.Page;
import org.egov.infstr.utils.DateUtils;
import org.egov.pims.model.Assignment;
import org.egov.pims.model.EmployeeView;
import org.egov.pims.model.PersonalInformation;
import org.egov.pims.service.EisUtilService;
import org.egov.pims.service.EmployeeService;
import org.egov.pims.service.PersonalInformationService;
import org.egov.web.actions.BaseFormAction;
import org.egov.web.utils.EgovPaginatedList;
import org.egov.works.models.masters.Contractor;
import org.egov.works.models.measurementbook.MBDetails;
import org.egov.works.models.measurementbook.MBHeader;
import org.egov.works.models.revisionEstimate.RevisionType;
import org.egov.works.models.tender.TenderEstimate;
import org.egov.works.models.workorder.WorkOrder;
import org.egov.works.models.workorder.WorkOrderActivity;
import org.egov.works.models.workorder.WorkOrderEstimate;
import org.egov.works.services.MeasurementBookService;
import org.egov.works.services.MeasurementBookWFService;
import org.egov.works.services.WorkOrderService;
import org.egov.works.services.WorksService;
import org.egov.works.services.impl.MeasurementBookServiceImpl;
import org.egov.works.services.impl.WorkOrderServiceImpl;
import org.egov.works.utils.DateConversionUtil;
import org.egov.works.utils.WorksConstants;
import org.egov.works.web.actions.estimate.AjaxEstimateAction;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;

@ParentPackage("egov")
public class MeasurementBookAction extends BaseFormAction{
	
	private static final Logger logger = Logger.getLogger(MeasurementBookAction.class);
	//private static final String CANCEL_ACTION = "cancel";
	// private static final String SAVE_ACTION = "save";
	private static final String VERIFY = "verify";
	private static final String SUBMITTED = "submitted";
	private static final String ACTIVITY_SEARCH = "activitySearch";
	private static final String MB_SEARCH = "mbSearch";
	private static final String DATEFORMAT="dd-MMM-yyyy";
	
	private MBHeader mbHeader = new MBHeader();
	private List<MBDetails> mbDetails = new LinkedList<MBDetails>();
	private String messageKey;
	private Long id;
	@Autowired
        private EmployeeService employeeService;
        @Autowired
        private UserService userService;
	private EmployeeView mbPreparedByView;
	private MeasurementBookService measurementBookService;
	private WorksService worksService;
	private WorkOrderService workOrderService;
	private List<MBDetails> actionMbDetailValues = new LinkedList<MBDetails>();
	private double quantityFactor;
        private List<WorkOrderActivity> activityList; // for search page
        private List<MBHeader> mbList; // for search page
        private List<WorkOrderEstimate> workOrderEstimateList= new ArrayList<WorkOrderEstimate>();
        private String 	workorderNo;
        private Long workOrderId;
        private String mode;
        private String mborderNumberRequired;
        private String workName;
        private String projectCode;
    //-----------------------Activity Search ----------------------------------
	private String 	activityCode;
	private String  activityDesc;
	//-------------------------------------------------------------------
	
	//-----------------------MB Search ----------------------------------
	private Long contractorId;
	private Date  fromDate;
	private Date  toDate;
	private String mbRefNo;
	private String mbPages;
	private String mbStatus;
	//-------------------------------------------------------------------
	
	//-----------------------Manual Workflow ----------------------------
	private Long departmentId; 
	private Integer designationId; 
	private String approverComments;
	private Integer approverUserId;
	//-------------------------------------------------------------------
	
	// -------------- on for workflow service
	private MeasurementBookWFService measurementBookWFService;
	private WorkflowService<MBHeader> workflowService;	
	//private static final String SOURCE_SEARCH = "search";
	//private static final String SOURCE_INBOX = "inbox";	
	private static final String SAVE_ACTION = "save";
	//private static final Object REJECT_ACTION = "reject";	
	private final static String APPROVED = "APPROVED";
	private static final String SOURCE_INBOX="inbox";
	
	private String sourcepage="";
	private String dispEmployeeName;
	private String dispDesignation;
	private Long estimateId;
	private PersonalInformationService personalInformationService;
	private EisUtilService eisService;
	
	private static final String ACTION_NAME="actionName";
	@Autowired
        private CommonsService commonsService;
	private String activitySearchMode;
	private boolean isLegacyMB;
	private static final String NON_TENDERED = "nonTendered";
	private static final String LUMP_SUM = "lumpSum";
	private static final String SEARCH = "search";
	private static final String INBOX = "inbox";
	private String cancellationReason;
	private String cancelRemarks;
	private Long mbId;
	private String estimateNo;
	private Integer execDeptid;
	private Integer page=1;
	private Integer pageSize=30;
	private EgovPaginatedList pagedResults;
	private Integer defaultPreparedById;
	private String defaultDesgination;
	private String isRCEstimate;
	private static final String YES = "yes";
	private Date workCommencedDate ;
	private Date latestMBDate ;
	private Long woId;

	public MeasurementBookAction() {
		addRelatedEntity("workOrder", WorkOrder.class);
	}
    
    @Override
	public void prepare() {
		AjaxMeasurementBookAction ajaxMBAction = new AjaxMeasurementBookAction();
		ajaxMBAction.setPersistenceService(getPersistenceService());
		ajaxMBAction.setEmployeeService(employeeService);
		ajaxMBAction.setPersonalInformationService(personalInformationService);
		if (id != null ) {
			mbHeader= measurementBookService.findById(id, false); 
			if(mbHeader!=null && mbHeader.getMbPreparedBy()!=null){
				 mbPreparedByView = (EmployeeView) getPersistenceService().find("from EmployeeView where id = ?", mbHeader.getMbPreparedBy().getIdPersonalInformation());
				 workOrderEstimateList.add(mbHeader.getWorkOrderEstimate());
			}
		}else if(workOrderId!=null){
		workOrderEstimateList.addAll(getPersistenceService().
						findAllByNamedQuery("getWorkOrderEstimateByWorkOrderId", workOrderId));
			mbHeader.setWorkOrder(workOrderService.findById(workOrderId, false));
		}
		if(workOrderEstimateList.isEmpty())
			addDropdownData("workOrderEstimateList", Collections.EMPTY_LIST);
		else
			addDropdownData("workOrderEstimateList",measurementBookService.getWorkOrderEstimatesForMB(workOrderEstimateList));
			
		super.prepare();
		setupDropdownDataExcluding("workOrder");
		populatePreparedByList(ajaxMBAction, mbHeader.getWorkOrder() != null);
		setMBPreparedBy(getIdPersonalInformationFromParams());
		addDropdownData("executingDepartmentList", getPersistenceService().findAllBy("from DepartmentImpl"));
		if(getLatestAssignmentForCurrentLoginUser()!=null) {
			departmentId = getLatestAssignmentForCurrentLoginUser().getDeptId().getId();
		}
	
		populateQuantityFactor();
		if("cancelMB".equals(sourcepage)) {
			setMbStatus(MBHeader.MeasurementBookStatus.APPROVED.toString());
		}
		getWrkCommndAndLatestMBDates();
	}
        
    private void getWrkCommndAndLatestMBDates(){
    	if(id==null || (mbHeader.getEgwStatus()!=null && (mbHeader.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.NEW) || mbHeader.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.REJECTED)))){
    		if(id==null){
    			woId = workOrderId;
    		} else {
    			woId = mbHeader.getWorkOrder().getId();
    		}
    		if (woId != null)
    			workCommencedDate =  measurementBookService.getWorkCommencedDate(woId);
    		if(workOrderEstimateList.size() == 1){
    			Long estId;
    			if(id == null){
    				estId = workOrderEstimateList.get(0).getEstimate().getId();
    			} else {
    				estId = mbHeader.getWorkOrderEstimate().getEstimate().getId();
    			}
    			if(estId != null)
    				latestMBDate  = measurementBookService.getLastMBCreatedDate(woId,estId);
    		}
    	}
    }
    
    public Assignment getLatestAssignmentForCurrentLoginUser() {
		PersonalInformation personalInformation=null;
		Long loggedInUserId = worksService.getCurrentLoggedInUserId();
		if(loggedInUserId != null) {
			personalInformation = employeeService.getEmpForUserId(loggedInUserId);		    
		}
		Assignment assignment=null;
		if(personalInformation!=null){
			assignment=employeeService.getLatestAssignmentForEmployee(personalInformation.getIdPersonalInformation());			
		}
		return assignment; 
	}
    	
    protected void populateQuantityFactor(){
    	String configVal = worksService.getWorksConfigValue("MAXEXTRALINEITEMPERCENTAGE");
    	try{
    		quantityFactor = Double.valueOf(configVal);
    	}catch(Exception e){
    		logger.error("Exception in populateQuantityFactor()>>>"+e.getMessage());
    		quantityFactor = 0.0d;    		
    	}
    }
    
    protected MBHeader calculateMBdetails(MBHeader mbHeader,boolean isPersistedObject){
    	return measurementBookService.calculateMBDetails(mbHeader,isPersistedObject);
    }
    
	protected void populatePreparedByList(AjaxMeasurementBookAction ajaxMBAction, boolean executingDeptPopulated){
		if (executingDeptPopulated) {
			if(mode!=null && mode.equalsIgnoreCase(SEARCH))
			{
				ajaxMBAction.setExecutingDepartment(workOrderEstimateList.get(0).getEstimate().getExecutingDepartment().getId());
				ajaxMBAction.usersInExecutingDepartment();
				addDropdownData("preparedByList", ajaxMBAction.getUsersInExecutingDepartment());
			}
			else if(sourcepage!=null && sourcepage.equalsIgnoreCase(INBOX) && !canUserModify() 
					&& !(mbHeader!=null && mbHeader.getEgwStatus().getCode().equalsIgnoreCase("REJECTED")  ))
			{
				ajaxMBAction.setExecutingDepartment(workOrderEstimateList.get(0).getEstimate().getExecutingDepartment().getId());
				ajaxMBAction.usersInExecutingDepartment();
				addDropdownData("preparedByList", ajaxMBAction.getUsersInExecutingDepartment());
			}
			else if(id==null || mbHeader == null || mbHeader.getId()==null || canUserModify() 
					|| (mbHeader!=null && mbHeader.getEgwStatus().getCode().equalsIgnoreCase("REJECTED")  ) )
			{
				AjaxEstimateAction ajaxEstimateAction = new AjaxEstimateAction();
				ajaxEstimateAction.setPersistenceService(getPersistenceService());
				ajaxEstimateAction.setEmployeeService(employeeService);
				ajaxEstimateAction.setPersonalInformationService(personalInformationService);
				ajaxEstimateAction.setEisService(eisService);
				ajaxEstimateAction.setExecutingDepartment(workOrderEstimateList.get(0).getEstimate().getExecutingDepartment().getId());
				String loggedInUserEmployeeCode = null;
				if(mbHeader!=null && mbHeader.getMbPreparedBy()!=null)
					loggedInUserEmployeeCode = mbHeader.getMbPreparedBy().getEmployeeCode();
				else
				{
					Assignment latestAssignment = getLatestAssignmentForCurrentLoginUser();
					if(latestAssignment != null) 
						loggedInUserEmployeeCode = latestAssignment.getEmployee().getCode();
				}
				ajaxEstimateAction.setEmployeeCode(loggedInUserEmployeeCode);
				ajaxEstimateAction.usersInExecutingDepartment();
				if(ajaxEstimateAction.getUsersInExecutingDepartment()!=null && ajaxEstimateAction.getUsersInExecutingDepartment().size()==1)
				{
					defaultPreparedById =((List<EmployeeView>) ajaxEstimateAction.getUsersInExecutingDepartment()).get(0).getId();
					defaultDesgination = ((List<EmployeeView>) ajaxEstimateAction.getUsersInExecutingDepartment()).get(0).getDesigId().getDesignationName();
				}
				addDropdownData("preparedByList", ajaxEstimateAction.getUsersInExecutingDepartment());
			}
			else
			{
				ajaxMBAction.setExecutingDepartment(workOrderEstimateList.get(0).getEstimate().getExecutingDepartment().getId());
				ajaxMBAction.usersInExecutingDepartment();
				addDropdownData("preparedByList", ajaxMBAction.getUsersInExecutingDepartment());
			}
		}else 
			addDropdownData("preparedByList", Collections.emptyList());
	}
	
	protected Integer getIdPersonalInformationFromParams() {
		String[] ids = parameters.get("mbPreparedBy");
		if (ids != null && ids.length > 0) {
			parameters.remove("mbPreparedBy");
			String id = ids[0];
			if (id != null && id.length() > 0) {
				return Integer.parseInt(id);
			}
		}
		return null;
	}
	
	protected void setMBPreparedBy(Integer idPersonalInformation) {
		if (validMBPreparedBy(idPersonalInformation)) {
			 mbHeader.setMbPreparedBy(employeeService.getEmloyeeById(idPersonalInformation));
			 mbPreparedByView = (EmployeeView) getPersistenceService().find("from EmployeeView where id = ?", idPersonalInformation);
		 }		 
	 }
	
	 protected boolean validMBPreparedBy(Integer idPersonalInformation) {
		 if (idPersonalInformation != null && idPersonalInformation > 0) {
			 return true;
		 }
		 return false;
	 }
	
	 public String loadSerachForActivity(){
		 logger.info("Loading search page for Activity............");
		 return ACTIVITY_SEARCH;
	 }
	 
	 public String searchActivitiesForMB(){
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		if(workorderNo != null && !"".equalsIgnoreCase(workorderNo))
			criteriaMap.put(WorkOrderServiceImpl.WORKORDER_NO, workorderNo);
		if(estimateId != null)
			criteriaMap.put(WorkOrderServiceImpl.WORKORDER_ESTIMATE_ID, estimateId);
		if(activityCode != null && !"".equalsIgnoreCase(activityCode))
			criteriaMap.put(WorkOrderServiceImpl.ACTIVITY_CODE, activityCode);
		if(activityDesc != null && !"".equalsIgnoreCase(activityDesc))
			criteriaMap.put(WorkOrderServiceImpl.ACTIVITY_DESC, activityDesc);
		if(workOrderId != null)
			criteriaMap.put(WorkOrderServiceImpl.WORKORDER_ID, workOrderId);
		if(StringUtils.isNotBlank(activitySearchMode) &&  (activitySearchMode.equalsIgnoreCase(NON_TENDERED) ))
		{
			criteriaMap.put(WorkOrderServiceImpl.REVISION_TYPE, RevisionType.NON_TENDERED_ITEM.toString());
			activityList = workOrderService.searchWOActivitiesFromRevEstimates(criteriaMap);
		} 
		else if (StringUtils.isNotBlank(activitySearchMode) &&  (activitySearchMode.equalsIgnoreCase(LUMP_SUM) ))
		{
			criteriaMap.put(WorkOrderServiceImpl.REVISION_TYPE, RevisionType.LUMP_SUM_ITEM.toString());
			activityList = workOrderService.searchWOActivitiesFromRevEstimates(criteriaMap);
		}
		else
		{	
			activityList = workOrderService.searchWOActivities(criteriaMap);
		}	
		
		return ACTIVITY_SEARCH;
	 }
	 
	 public String loadSerachForMB(){
		 logger.debug("Loading search page for MB............");
		 return MB_SEARCH;
	 }
	 
	 public String loadSearchForMB(){
		 logger.debug("Loading search page for MB............");
		 return MB_SEARCH;
	 }
	 
	 public String searchMB(){
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		List<Object> paramList = new ArrayList<Object>();
		List<String> qryObj=new ArrayList<String>();
		Object[] params;
		if(workorderNo != null && !"".equalsIgnoreCase(workorderNo))
			criteriaMap.put(MeasurementBookServiceImpl.WORKORDER_NO, workorderNo);
		
		if(contractorId != null && contractorId != -1)
			criteriaMap.put(MeasurementBookServiceImpl.CONTRACTOR_ID, contractorId);
		
		if(fromDate!=null && toDate!=null && !DateUtils.compareDates(getToDate(),getFromDate()))
			addFieldError("enddate",getText("greaterthan.endDate.fromDate"));
		
		if(toDate!=null && !DateUtils.compareDates(new Date(),getToDate()))
			addFieldError("enddate",getText("greaterthan.endDate.currentdate"));		
		
		if(fromDate!=null && toDate==null){
			criteriaMap.put("FROM_DATE",new Date(DateUtils.getFormattedDate(getFromDate(),DATEFORMAT)));
		}else if(toDate!=null && fromDate==null){
			 criteriaMap.put("TO_DATE",new Date(DateUtils.getFormattedDate(getToDate(),DATEFORMAT)));
		}else if(fromDate!=null && toDate!=null && getFieldErrors().isEmpty()){
			criteriaMap.put("FROM_DATE", new Date(DateUtils.getFormattedDate(getFromDate(),DATEFORMAT)));
		    criteriaMap.put("TO_DATE",new Date(DateUtils.getFormattedDate(getToDate(),DATEFORMAT)));
		}		
		
		if(mbRefNo != null && !"".equalsIgnoreCase(mbRefNo))
			criteriaMap.put(MeasurementBookServiceImpl.MB_REF_NO, mbRefNo);
		if(mbPages != null && !"".equalsIgnoreCase(mbPages))
			criteriaMap.put(MeasurementBookServiceImpl.MB_PAGE_NO, mbPages);
		if(mbStatus != null && !"".equalsIgnoreCase(mbStatus) && !"-1".equals(mbStatus))
			criteriaMap.put(MeasurementBookServiceImpl.STATUS, mbStatus);
/*		if(mbStatus != null && "-1".equals(mbStatus))
			criteriaMap.put(MeasurementBookServiceImpl.STATUS, mbStatus);*/
		if(execDeptid != null && execDeptid != -1)
			criteriaMap.put(MeasurementBookServiceImpl.DEPT_ID, execDeptid);
		if(estimateNo != null && !"".equalsIgnoreCase(estimateNo))
			criteriaMap.put(MeasurementBookServiceImpl.EST_NO, estimateNo);		
		
		qryObj= measurementBookService.searchMB(criteriaMap, paramList);
		//mbList = measurementBookService.searchMB(criteriaMap, paramList);
		
		Page resPage;
		Long count;
		
		String qry=qryObj.get(0);
		if(paramList.isEmpty()){
			params=null;
			Query qry1=persistenceService.getSession().createQuery(qry);
			count = (Long)persistenceService.find(qryObj.get(1));
			resPage=new Page(qry1,page,pageSize);  
			 //resPage= persistenceService.findPageBy(qry,page,pageSize,params);  
		 }else{
			 params = new Object[paramList.size()];
				params = paramList.toArray(params);
			  count = (Long)persistenceService.find(qryObj.get(1),params);
				 resPage= persistenceService.findPageBy(qry,page,pageSize,params);  
		 }
		pagedResults = new EgovPaginatedList(resPage, count.intValue());
		mbList=	(pagedResults!=null?pagedResults.getList():null);
		
		if(!mbList.isEmpty())
        	mbList = getPositionAndUser(mbList);
		
		pagedResults.setList(mbList);
		return MB_SEARCH;
	 }
	 
	 protected List<MBHeader> getPositionAndUser(List<MBHeader> results){
			List<MBHeader> mbHeaderList = new ArrayList<MBHeader>();
			for(MBHeader mbh :results){
				if(!mbh.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.APPROVED) && !mbh.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.CANCELLED_STATUS)){
					PersonalInformation emp = employeeService.getEmployeeforPosition(mbh.getCurrentState().getOwnerPosition());
					if(emp!=null && StringUtils.isNotBlank(emp.getEmployeeName()))
						mbh.setOwner(emp.getEmployeeName());
				}
				mbHeaderList.add(mbh);
				String actions = worksService.getWorksConfigValue("MB_SHOW_ACTIONS");
				if(StringUtils.isNotBlank(actions)){
					mbh.getMbActions().addAll(Arrays.asList(actions.split(",")));
				}
			}	
			return mbHeaderList;
		}
	 
	 public Map<String,Object> getContractorForApprovedWorkOrder() {
			Map<String,Object> contractorsWithWOList = new HashMap<String, Object>();		
			if(workOrderService.getContractorsWithWO()!=null) {
				for(Contractor contractor :workOrderService.getContractorsWithWO()){ 
					contractorsWithWOList.put(contractor.getId()+"", contractor.getCode()+" - "+contractor.getName());
				}			
			}
			return contractorsWithWOList; 
		} 
	 
	 
	 public List<EgwStatus> getMbStatusList() {
		 return persistenceService.findAllBy("from EgwStatus s where moduletype=? and code<>'NEW' order by orderId",MBHeader.class.getSimpleName());
	}
	 
	public List<Contractor> getContractorList() {
		 return workOrderService.getAllContractorForWorkOrder();		
	}
	 
	public String newform(){
		return NEW;
	}
	
	public String edit(){
		if(SOURCE_INBOX.equalsIgnoreCase(sourcepage)){
			User user=userService.getUserById(worksService.getCurrentLoggedInUserId());
			boolean isValidUser=worksService.validateWorkflowForUser(mbHeader,user);
			if(isValidUser){
					throw new EGOVRuntimeException("Error: Invalid Owner - No permission to view this page.");
			}
		}
		else if(StringUtils.isEmpty(sourcepage)){
			sourcepage="search";
		}

		mbHeader = calculateMBdetails(mbHeader,true);
		return NEW;
	}
	
	public String execute(){
		return SUCCESS;
	}
	/*
	public String cancel(){
		return SUCCESS;
	}
	*/
	
	private boolean canUserModify()
	{
		boolean result = false ;
		String designWhoCanModify =null;
		designWhoCanModify = getMBWorkflowModifyDesignation();
		String currentDesgination = null;
		if(mbHeader.getCurrentState()!=null && mbHeader.getCurrentState().getOwnerPosition()!=null
				&& mbHeader.getCurrentState().getOwnerPosition().getDeptDesigId()!=null 
				&& mbHeader.getCurrentState().getOwnerPosition().getDeptDesigId().getDesigId()!=null
				&& mbHeader.getCurrentState().getOwnerPosition().getDeptDesigId().getDesigId().getDesignationName()!=null)
			currentDesgination = mbHeader.getCurrentState().getOwnerPosition().getDeptDesigId().getDesigId().getDesignationName();
		if(designWhoCanModify !=null &&  currentDesgination!=null && designWhoCanModify.equalsIgnoreCase(currentDesgination))
			result = true;
		return result;
	}
	
	private void setApprovedQtyAndPrevCumlVal()
	{
		AjaxMeasurementBookAction ajaxMBAction = new AjaxMeasurementBookAction();
		ajaxMBAction.setPersistenceService(getPersistenceService());
		ajaxMBAction.setEmployeeService(employeeService);
		ajaxMBAction.setPersonalInformationService(personalInformationService);
		ajaxMBAction.setMeasurementBookService(measurementBookService);
		
		Long woActId = null;
		Long mbHeaderId = mbHeader.getId()==null?null:mbHeader.getId();
		ajaxMBAction.setMbHeaderId(mbHeaderId);
		for(MBDetails mbDetails: mbHeader.getMbDetails()) {
			if(mbDetails != null) {
				woActId =  mbDetails.getWorkOrderActivity().getId();
				ajaxMBAction.setWoActivityId(woActId);
				ajaxMBAction.activityDetails();
				mbDetails.setTotalEstQuantity( ajaxMBAction.getTotalEstQuantity());
				mbDetails.setPrevCumlvQuantity( ajaxMBAction.getPrevCulmEntry());
			}
		}
	}
	
	public String save(){
		if(mbHeader.getEgwStatus()==null ||  WorksConstants.REJECTED.equalsIgnoreCase(mbHeader.getEgwStatus().getCode())
				||  NEW.equalsIgnoreCase(mbHeader.getEgwStatus().getCode()) || canUserModify()){
			mbHeader.getMbDetails().clear();
		}
		mbHeader.setIsLegacyMB(isLegacyMB);
		String actionName = "";
		if (parameters.get(ACTION_NAME) != null && parameters.get(ACTION_NAME)[0] != null) 
			actionName = parameters.get(ACTION_NAME)[0];
		if(mbHeader.getEgwStatus()==null ||  WorksConstants.REJECTED.equalsIgnoreCase(mbHeader.getEgwStatus().getCode())
				||  NEW.equalsIgnoreCase(mbHeader.getEgwStatus().getCode()) || canUserModify()){
			populateActivities();
		}
		WorkOrderEstimate woe = (WorkOrderEstimate)persistenceService.findByNamedQuery("getWorkOrderEstimateByEstAndWO", estimateId,workOrderId);
		mbHeader.setWorkOrderEstimate(woe);
		
		if(id==null || (mbHeader.getEgwStatus()!=null && (mbHeader.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.NEW) || mbHeader.getEgwStatus().getCode().equalsIgnoreCase(WorksConstants.REJECTED)))){
			if(workCommencedDate!= null && workCommencedDate.after(mbHeader.getMbDate())){
				throw new ValidationException(Arrays.asList(new ValidationError("mb.lessThan.wrk.cmmncd.date",getText("mb.lessThan.wrk.cmmncd.date")+" "+new SimpleDateFormat("dd/MM/yyyy").format(workCommencedDate)+". "+getText("pls.enter.valid.date"))));
			}
			if(latestMBDate != null && latestMBDate.after(mbHeader.getMbDate())){
				throw new ValidationException(Arrays.asList(new ValidationError("mb.lessThan.latest.mbdate.date",getText("mb.lessThan.latest.mbdate.date")+" "+new SimpleDateFormat("dd/MM/yyyy").format(latestMBDate)+". "+getText("pls.enter.valid.date"))));
			}
		}
		try{
			if(mbHeader.getEgwStatus()==null ||  WorksConstants.REJECTED.equalsIgnoreCase(mbHeader.getEgwStatus().getCode())
					||  NEW.equalsIgnoreCase(mbHeader.getEgwStatus().getCode()) || canUserModify()){
					validateMBAmount(woe);
			}
		}
		catch(ValidationException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			logger.debug("Error while validation of mb and wo amount"+e);
		}

		
		if(actionName.equalsIgnoreCase(MBHeader.Actions.SUBMIT_FOR_APPROVAL.toString())
				&& mbHeader.getMbDetails().isEmpty()) {
			setApprovedQtyAndPrevCumlVal();
			throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.item.mandatory","measurementbook.item.mandatory")));
		}
	
		/*if(!actionName.equalsIgnoreCase(MBHeader.Actions.APPROVAL.toString())
				&& workOrderService.isMBInApprovalPendingForWO(workorderNo))
			throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.approvalpending",
			"measurementbook.approvalpending")));*/
		
		//if(measurementBookService.approvalLimitCrossed(mbHeader)){
		if(mbHeader.getEgwStatus()==null ||  WorksConstants.REJECTED.equalsIgnoreCase(mbHeader.getEgwStatus().getCode())
				||  NEW.equalsIgnoreCase(mbHeader.getEgwStatus().getCode()) || canUserModify()){
			for(MBDetails details:mbHeader.getMbDetails()){
				Boolean limitStatus =  measurementBookService.approvalLimitCrossed(details);
				if(limitStatus==null)
				{
					Double percentage = worksService.getConfigval()+100;
					setApprovedQtyAndPrevCumlVal();
					if(isRCEstimate.equalsIgnoreCase(YES)){
						throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.currMBEntry.quantityFactor.rcEstimate.complete.error",getText("measurementbook.currMBEntry.quantityFactor.rcEstimate.complete.error",new String[]{percentage.toString()}))));
					}
					else{
						throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.currMBEntry.quantityFactor.complete.error",getText("measurementbook.currMBEntry.quantityFactor.complete.error",new String[]{percentage.toString()}))));
					}
				}
				else
				{
					if(limitStatus)
					{
						  if(!StringUtils.isNotBlank(details.getOrderNumber())){
							  setApprovedQtyAndPrevCumlVal();  
							  throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.currMBEntry.enter.order.no",getText("measurementbook.currMBEntry.enter.order.no"))));
						  }
						  if(details.getMbdetailsDate()==null){
							  setApprovedQtyAndPrevCumlVal();
							  throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.currMBEntry.enter.order.dt",getText("measurementbook.currMBEntry.enter.order.dt"))));
					      }
						  else if(details.getMbdetailsDate()!=null && DateConversionUtil.isBeforeByDate(new Date(),details.getMbdetailsDate())){
							  setApprovedQtyAndPrevCumlVal();
							  throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.currMBEntry.order.date.error",getText("measurementbook.currMBEntry.order.date.error"))));
					      }
					}
				}
		   }
		}
			
		//}
			
		if(SAVE_ACTION.equals(actionName) && mbHeader.getEgwStatus()==null) {
			mbHeader.setEgwStatus(commonsService.getStatusByModuleAndCode("MBHeader","NEW"));
		}	
			
		mbHeader = measurementBookService.persist(mbHeader);
		if (!actionName.isEmpty()) {			
			mbHeader = (MBHeader) workflowService.transition(actionName, mbHeader,approverComments);
		}		
		if(mbHeader.getEgwStatus()==null ||  WorksConstants.REJECTED.equalsIgnoreCase(mbHeader.getEgwStatus().getCode())
				||  NEW.equalsIgnoreCase(mbHeader.getEgwStatus().getCode()) || canUserModify()){
			mbHeader = calculateMBdetails(mbHeader,true);
		}	
		if(mbHeader.getEgwStatus()!=null && mbHeader.getEgwStatus().getCode()!=null 
				&&  (MBHeader.MeasurementBookStatus.APPROVED.toString()).equalsIgnoreCase(mbHeader.getEgwStatus().getCode())){
			messageKey="measurementbook.approved";
		}
		else{
			messageKey="measurementbook.save.success";
		}
		addActionMessage(getText(messageKey,messageKey));
		getDesignation(mbHeader);
		
		if(SAVE_ACTION.equals(actionName)){
			sourcepage="inbox";
		}

		return SAVE_ACTION.equals(actionName)?EDIT:SUCCESS;	
		
		//addActionMessage(getText("measurementbook.save.success"));
		//return EDIT;
	}
	
	private void validateMBAmount(WorkOrderEstimate workOrderEstimate){
		double negoPerc=0;
		String tenderType="";
		BigDecimal totalMBAmount = BigDecimal.ZERO;
		BigDecimal currMBTotal = BigDecimal.ZERO;
		BigDecimal allMBsTotal = BigDecimal.ZERO;
		Double woEstimateAmount=0D;
		Double totalWOAmount=0D;
		TenderEstimate tenderEstimate;
		Object[] obj = (Object[]) persistenceService.find("select tr.percNegotiatedAmountRate,tr.tenderEstimate,tr.tenderNegotiatedValue from TenderResponse tr where tr.egwStatus.code = 'APPROVED' and tr.negotiationNumber = ? ", workOrderEstimate.getWorkOrder().getNegotiationNumber());
                
                tenderEstimate = (TenderEstimate) obj[1];
                tenderType = tenderEstimate.getTenderType();
                if(StringUtils.isNotBlank(tenderType) && tenderType.equalsIgnoreCase(WorksConstants.PERC_TENDER)){
                        negoPerc = (Double) obj[0];
                }
                else{
                        double negotiationValue = (Double) obj[2];
                        negoPerc = (negotiationValue/ tenderEstimate.getWorksPackage().getTotalAmount());
                }
                
                totalMBAmount = measurementBookService.getTotalMBAmountForPrevMBs(workOrderEstimate,negoPerc,tenderType);
                currMBTotal = getAmountsForCurrentMB(mbHeader.getMbDetails(),negoPerc,tenderType);
                allMBsTotal = totalMBAmount.add(currMBTotal);
                
                Double approvedRevisionWOAmount =  (Double) persistenceService.find(" select sum(woe.workOrder.workOrderAmount) from WorkOrderEstimate woe where woe.workOrder.parent is not null and woe.workOrder.egwStatus.code='APPROVED' and woe.estimate.parent.id=? ",estimateId);
                if(StringUtils.isNotBlank(tenderType) && tenderType.equalsIgnoreCase(WorksConstants.PERC_TENDER)){
                    Double estimateAmt = (Double) persistenceService.find("select woe.estimate.workValue.value from WorkOrderEstimate woe where woe.workOrder.parent is null and woe.workOrder.egwStatus.code='APPROVED' and woe.estimate.id=? ",estimateId);
                    woEstimateAmount = estimateAmt+((estimateAmt*negoPerc)/100);
                }
                else{
                    for(WorkOrderActivity woa : workOrderEstimate.getWorkOrderActivities()){
                            woEstimateAmount = woEstimateAmount+woa.getApprovedAmount();
                    }
                }
                totalWOAmount = approvedRevisionWOAmount==null?(woEstimateAmount):(approvedRevisionWOAmount + woEstimateAmount);
      
		if(allMBsTotal.doubleValue()>totalWOAmount)
		{
			setApprovedQtyAndPrevCumlVal();
			throw new ValidationException(Arrays.asList(new ValidationError("measurementbook.workOrder.amount.exceeded","measurementbook.workOrder.amount.exceeded")));
			
		}
	}
	
	private BigDecimal getAmountsForCurrentMB(List<MBDetails> mbDetailsList,double negoPerc, String tenderType){
	
		BigDecimal currentMBTenderedAmt = BigDecimal.ZERO;
		BigDecimal currMBAmount = BigDecimal.ZERO; 
		BigDecimal tenderedMBAmount = BigDecimal.ZERO;
		BigDecimal currMBTotal = BigDecimal.ZERO;
		
		if(tenderType.equalsIgnoreCase(WorksConstants.PERC_TENDER)){
			for(MBDetails mbd : mbDetailsList ){
				if(mbd.getWorkOrderActivity().getActivity().getRevisionType()==null){
					currentMBTenderedAmt=currentMBTenderedAmt.add(BigDecimal.valueOf(mbd.getAmount()));
				}
			}
			currMBAmount=mbHeader.getTotalMBAmount();
		
			//applying percentage on tendered items
			if(currentMBTenderedAmt!=null){
				tenderedMBAmount = currentMBTenderedAmt.add(currentMBTenderedAmt.multiply(BigDecimal.valueOf(negoPerc/100)));
			}
			// adding tendered amount with the non tendered items amount, to get the total mb amount
			currMBTotal = tenderedMBAmount.add(currMBAmount.subtract(currentMBTenderedAmt));
		}
		else{
			currMBTotal = mbHeader.getTotalMBAmount();
		}
		
		return currMBTotal.setScale(2, RoundingMode.HALF_UP);
	}
	
	protected void populateActivities() {	
		 for(MBDetails mbDetails: actionMbDetailValues) {
			 if(mbDetails != null) {
				 mbDetails.setMbHeader(mbHeader);
				 if(mbDetails.getWorkOrderActivity().getActivity().getId()==null) 
					 mbDetails.setWorkOrderActivity((WorkOrderActivity)getPersistenceService().find("from WorkOrderActivity where id=?", mbDetails.getWorkOrderActivity().getId()));
				 mbHeader.addMbDetails(mbDetails);
			 }
		 }		
	 }
		
	//workflow for reject mb
	/** reject  */	 
	public String reject(){
		String actionName = "";
		if (parameters.get(ACTION_NAME) != null && parameters.get(ACTION_NAME)[0] != null) 
			actionName = parameters.get(ACTION_NAME)[0];
		
		if(mbHeader!=null && mbHeader.getId()!=null && !actionName.isEmpty()){	
			//calling workflow api
			workflowService.transition(actionName,mbHeader,approverComments);		
			mbHeader=measurementBookService.persist(mbHeader);				
			getDesignation(mbHeader);
		}
		messageKey="measurementbook.reject";		
		return SUCCESS;
	}
	
	//workflow for cancel mb		 
	public String cancel(){		
		if(mbHeader!=null && mbHeader.getEgBillregister()!=null 
				&& mbHeader.getEgBillregister().getStatus()!=null 
				&& !mbHeader.getEgBillregister().getStatus().getCode().equalsIgnoreCase("CANCELLED")) {
		  	messageKey="measurementbook.cancel.failure";	
		  	addActionError(getText(messageKey));
			return EDIT;
		}
				
		String actionName = "";
		if (parameters.get(ACTION_NAME) != null && parameters.get(ACTION_NAME)[0] != null) 
			actionName = parameters.get(ACTION_NAME)[0];
		
		if(mbHeader!=null && mbHeader.getId()!=null && !actionName.isEmpty()){			
			workflowService.transition(actionName,mbHeader,approverComments);		
			mbHeader=measurementBookService.persist(mbHeader);		
			getDesignation(mbHeader);
		}
		messageKey="measurementbook.cancel";		
		return SUCCESS;
	}
	
	protected void getDesignation(MBHeader mbHeader){	
		/* start for customizing workflow message display */
		if(mbHeader.getEgwStatus()!= null 
				&& !(WorksConstants.NEW).equalsIgnoreCase(mbHeader.getEgwStatus().getCode())) {
			String result = worksService.getEmpNameDesignation(mbHeader.getState().getOwnerPosition(), mbHeader.getState().getCreatedDate().toDate());
			if(result != null && !"@".equalsIgnoreCase(result)) {
				String empName = result.substring(0,result.lastIndexOf('@'));
				String designation =result.substring(result.lastIndexOf('@')+1,result.length());
				setDispEmployeeName(empName);
				setDispDesignation(designation);			
			}
		}
		/* end */	
	}	
	
	public String verify(){
		// TODO save and show details for submit
		return VERIFY;
	}
	
	public String submit(){
		// TODO make status approval pending for all MBS  
		return SUBMITTED;
	}
	
	public Object getModel() {
		return mbHeader;
	}
	
	public String cancelApprovedMB() {  
		MBHeader mbHeader= measurementBookService.findById(mbId, false);
		String cancellationText = null;
		mbHeader.setEgwStatus(commonsService.getStatusByModuleAndCode("MBHeader",MBHeader.MeasurementBookStatus.CANCELLED.toString()));
		
		PersonalInformation prsnlInfo=employeeService.getEmpForUserId(worksService.getCurrentLoggedInUserId());			
		String empName="";
		if(prsnlInfo.getEmployeeFirstName()!=null)
			empName=prsnlInfo.getEmployeeFirstName();
		if(prsnlInfo.getEmployeeLastName()!=null)
			empName=empName.concat(" ").concat(prsnlInfo.getEmployeeLastName()); 
		if(cancelRemarks != null  && StringUtils.isNotBlank(cancelRemarks))
			cancellationText=cancellationReason+" : "+cancelRemarks+". MB Cancelled by: "+empName;
		else
			cancellationText=cancellationReason+". MB Cancelled by: "+empName;
		
		//TODO - The setter methods of variables in State.java are protected. Need to alternative way to solve this issue.
		//Set the status and workflow state to cancelled
		/*State oldEndState = mbHeader.getCurrentState();
		Position owner = prsnlInfo.getAssignment(new Date()).getPosition();
		oldEndState.setCreatedBy(prsnlInfo.getUserMaster());
		oldEndState.setModifiedBy(prsnlInfo.getUserMaster());
		oldEndState.setCreatedDate(new Date());
		oldEndState.setModifiedDate(new Date());
		oldEndState.setOwner(owner);
		oldEndState.setValue(WorksConstants.CANCELLED_STATUS);
		oldEndState.setText1(cancellationText);
		
		mbHeader.changeState("END", owner, null);*/
		
		mbRefNo=mbHeader.getMbRefNo();
		messageKey=mbRefNo+" : "+getText("mb.cancel.success.message"); 
		return SUCCESS;
	}
	
	public void setModel(MBHeader mbHeader){
		this.mbHeader = mbHeader;		
	}

	public List<MBDetails> getMbDetails() {
		return mbDetails;
	}

	public void setMbDetails(List<MBDetails> mbDetails) {
		this.mbDetails = mbDetails;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public EmployeeView getMbPreparedByView() {
		return mbPreparedByView;
	}

	public void setMbPreparedByView(EmployeeView mbPreparedByView) {
		this.mbPreparedByView = mbPreparedByView;
	}

	public void setMeasurementBookService(
			MeasurementBookService measurementBookService) {
		this.measurementBookService = measurementBookService;
	}

	public void setActionMbDetailValues(List<MBDetails> actionMbDetailValues) {
		this.actionMbDetailValues = actionMbDetailValues;
	}

	public List<WorkOrderActivity> getActivityList() {
		return activityList;
	}

	public List<MBDetails> getActionMbDetailValues() {
		return actionMbDetailValues;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getActivityDesc() {
		return activityDesc;
	}

	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}

	public String getWorkorderNo() {
		return workorderNo;
	}

	public void setWorkorderNo(String workorderNo) {
		this.workorderNo = workorderNo;
	}

	public void setWorkOrderService(WorkOrderService workOrderService) {
		this.workOrderService = workOrderService;
	}

	public double getQuantityFactor() {
		return quantityFactor;
	}

	public void setQuantityFactor(double quantityFactor) {
		this.quantityFactor = quantityFactor;
	}

	public List<MBHeader> getMbList() {
		return mbList;
	}

	public void setActivityList(List<WorkOrderActivity> activityList) {
		this.activityList = activityList;
	}

	public Long getContractorId() {
		return contractorId;
	}

	public void setContractorId(Long contractorId) {
		this.contractorId = contractorId;
	}

	public String getMbRefNo() {
		return mbRefNo;
	}

	public void setMbRefNo(String mbRefNo) {
		this.mbRefNo = mbRefNo;
	}

	public String getMbPages() {
		return mbPages;
	}

	public void setMbPages(String mbPages) {
		this.mbPages = mbPages;
	}

	public String getMbStatus() {
		return mbStatus;
	}

	public void setMbStatus(String mbStatus) {
		this.mbStatus = mbStatus;
	}
	// on jan 13 th workflow related
	public List<org.egov.infstr.workflow.Action> getValidActions(){
		return workflowService.getValidActions(mbHeader);
	}

	public void setMeasurementBookWorkflowService(WorkflowService<MBHeader> workflow) {
		this.workflowService = workflow;
	}
	
	
	public MeasurementBookWFService getMeasurementBookWFService() {
		return measurementBookWFService;
	}

	public void setMeasurementBookWFService(
			MeasurementBookWFService measurementBookWFService) {
		this.measurementBookWFService = measurementBookWFService;
	}
	public String getSourcepage() {
		return sourcepage;
	}

	public void setSourcepage(String sourcepage) {
		this.sourcepage = sourcepage;
	}
	
	public String getDispEmployeeName() {
		return dispEmployeeName;
	}

	public void setDispEmployeeName(String dispEmployeeName) {
		this.dispEmployeeName = dispEmployeeName;
	}

	public String getDispDesignation() {
		return dispDesignation;
	}

	public void setDispDesignation(String dispDesignation) {
		this.dispDesignation = dispDesignation;
	}	
	
	public void setWorksService(WorksService worksService) {
		this.worksService = worksService;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getDesignationId() {
		return designationId;
	}

	public void setDesignationId(Integer designationId) {
		this.designationId = designationId;
	}

	public String getApproverComments() {
		return approverComments;
	}

	public void setApproverComments(String approverComments) {
		this.approverComments = approverComments;
	}

	public Integer getApproverUserId() {
		return approverUserId;
	}

	public void setApproverUserId(Integer approverUserId) {
		this.approverUserId = approverUserId;
	}

	public String getMborderNumberRequired() {
		mborderNumberRequired= worksService.getWorksConfigValue("ORDER_NUMBER_REQUIRED");
		return mborderNumberRequired;
	}

	public String getWorkOrderEstimateRequired() {
		return  worksService.getWorksConfigValue("WORKORDER_ESTIMATE_REQUIRED");
	}
	
	public String getMBWorkflowModifyDesignation() {
		return  worksService.getWorksConfigValue("MB_WORKFLOW_MODIFY_DESIG");
	}
	
	public void setMborderNumberRequired(String mborderNumberRequired) {
		this.mborderNumberRequired = mborderNumberRequired;
	}

	public Long getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(Long workOrderId) {
		this.workOrderId = workOrderId;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

	public Long getEstimateId() {
		return estimateId;
	}

	public void setEstimateId(Long estimateId) {
		this.estimateId = estimateId;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<WorkOrderEstimate> getWorkOrderEstimateList() {
		return workOrderEstimateList;
	}

	public void setWorkOrderEstimateList(
			List<WorkOrderEstimate> workOrderEstimateList) {
		this.workOrderEstimateList = workOrderEstimateList;
	}

	public void setPersonalInformationService(
			PersonalInformationService personalInformationService) {
		this.personalInformationService = personalInformationService;
	}
	// end workflow related

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CommonsService getCommonsService() {
		return commonsService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	public String getActivitySearchMode() {
		return activitySearchMode;
	}

	public void setActivitySearchMode(String activitySearchMode) {
		this.activitySearchMode = activitySearchMode;
	}
	
	public boolean getIsLegacyMB() {
		return isLegacyMB;
	}

	public void setIsLegacyMB(boolean isLegacyMB) {
			this.isLegacyMB = isLegacyMB;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public Long getMbId() {
		return mbId;
	}

	public void setMbId(Long mbId) {
		this.mbId = mbId;
	}

	public String getEstimateNo() {
		return estimateNo;
	}

	public void setEstimateNo(String estimateNo) {
		this.estimateNo = estimateNo;
	}

	public Integer getExecDeptid() {
		return execDeptid;
	}

	public void setExecDeptid(Integer execDeptid) {
		this.execDeptid = execDeptid;
	}

	public String getCancelRemarks() {
		return cancelRemarks;
	}

	public void setCancelRemarks(String cancelRemarks) {
		this.cancelRemarks = cancelRemarks;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public EgovPaginatedList getPagedResults() {
		return pagedResults;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setPagedResults(EgovPaginatedList pagedResults) {
		this.pagedResults = pagedResults;
	}

	public void setEisService(EisUtilService eisService) {
		this.eisService = eisService;
	}

	public Integer getDefaultPreparedById() {
		return defaultPreparedById;
	}

	public void setDefaultPreparedById(Integer defaultPreparedById) {
		this.defaultPreparedById = defaultPreparedById;
	}

	public String getDefaultDesgination() {
		return defaultDesgination;
	}
	
	public String getIsRCEstimate() {
		return isRCEstimate;
	}

	public void setIsRCEstimate(String isRCEstimate) {
		this.isRCEstimate = isRCEstimate;
	}
	public Date getWorkCommencedDate() {
		return workCommencedDate;
	}

	public void setWorkCommencedDate(Date workCommencedDate) {
		this.workCommencedDate = workCommencedDate;
	}

	public Date getLatestMBDate() {
		return latestMBDate;
	}

	public void setLatestMBDate(Date latestMBDate) {
		this.latestMBDate = latestMBDate;
	}

	public Long getWoId() {
		return woId;
	}

	public void setWoId(Long woId) {
		this.woId = woId;
	}

}