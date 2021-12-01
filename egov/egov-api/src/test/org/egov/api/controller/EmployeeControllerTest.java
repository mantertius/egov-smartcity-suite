package org.egov.api.controller;

import org.egov.commons.*;
import org.egov.eis.entity.*;
import org.egov.eis.entity.enums.EmployeeStatus;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.entity.*;
import org.egov.infra.persistence.entity.enums.UserType;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infra.workflow.entity.WorkflowType;
import org.egov.infra.workflow.service.WorkflowTypeService;
import org.egov.infstr.services.PersistenceService;
import org.egov.pgr.entity.Priority;
import org.egov.pgr.service.PriorityService;
import org.egov.pims.commons.DeptDesig;
import org.egov.pims.commons.Designation;
import org.egov.pims.commons.Position;
import org.egov.pims.service.EisUtilService;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeControllerTest {

    @Mock
    private TokenStore mockTokenStore;
    @Mock
    private PersistenceService mockEntityQueryService;
    @Mock
    private WorkflowTypeService mockWorkflowTypeService;
    @Mock
    private PositionMasterService mockPosMasterService;
    @Mock
    private SecurityUtils mockSecurityUtils;
    @Mock
    private EisUtilService mockEisService;
    @Mock
    private PriorityService mockPriorityService;

    @InjectMocks
    private EmployeeController employeeControllerUnderTest;

    @Test
    public void testGetWorkFlowTypesWithItemsCount() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Configure PriorityService.getAllPriorities(...).
        final Priority priority1 = new Priority();
        priority1.setName("name");
        priority1.setCode("code");
        priority1.setWeight(0);
        final List<Priority> priorities = Arrays.asList(priority1);
        when(mockPriorityService.getAllPriorities()).thenReturn(priorities);

        when(mockSecurityUtils.getCurrentUser()).thenReturn(new User(UserType.CITIZEN));

        // Configure PositionMasterService.getPositionsForEmployee(...).
        final Position position = new Position();
        position.setName("name");
        position.setPostOutsourced(false);
        final DeptDesig deptDesig = new DeptDesig();
        deptDesig.setId(0L);
        final Designation designation = new Designation();
        designation.setId(0L);
        designation.setName("name");
        designation.setCode("code");
        designation.setDescription("description");
        final CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail = new CChartOfAccountDetail();
        cChartOfAccountDetail.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail.setId(0L);
        chartOfAccounts.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail)));
        chartOfAccounts.setMajorCode("majorCode");
        chartOfAccounts.setMyClass(0L);
        chartOfAccounts.setGlcode("glcode");
        chartOfAccounts.setName("name");
        chartOfAccounts.setPurposeId(0L);
        chartOfAccounts.setClassification(0L);
        chartOfAccounts.setDesc("desc");
        chartOfAccounts.setFunctionReqd(false);
        chartOfAccounts.setIsActiveForPosting(false);
        designation.setChartOfAccounts(chartOfAccounts);
        deptDesig.setDesignation(designation);
        final Department department = new Department();
        department.setId(0L);
        department.setName("name");
        department.setCode("code");
        deptDesig.setDepartment(department);
        deptDesig.setSanctionedPosts(0);
        deptDesig.setOutsourcedPosts(0);
        position.setDeptDesig(deptDesig);
        final List<Position> positions = Arrays.asList(position);
        when(mockPosMasterService.getPositionsForEmployee(0L, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(positions);

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getWorkFlowTypesWithItemsCount();

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockSession).close();
    }

    @Test
    public void testGetWorkFlowTypesWithItemsCount_PriorityServiceGetAllPrioritiesReturnsNoItems() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        when(mockPriorityService.getAllPriorities()).thenReturn(Collections.emptyList());
        when(mockSecurityUtils.getCurrentUser()).thenReturn(new User(UserType.CITIZEN));

        // Configure PositionMasterService.getPositionsForEmployee(...).
        final Position position = new Position();
        position.setName("name");
        position.setPostOutsourced(false);
        final DeptDesig deptDesig = new DeptDesig();
        deptDesig.setId(0L);
        final Designation designation = new Designation();
        designation.setId(0L);
        designation.setName("name");
        designation.setCode("code");
        designation.setDescription("description");
        final CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail = new CChartOfAccountDetail();
        cChartOfAccountDetail.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail.setId(0L);
        chartOfAccounts.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail)));
        chartOfAccounts.setMajorCode("majorCode");
        chartOfAccounts.setMyClass(0L);
        chartOfAccounts.setGlcode("glcode");
        chartOfAccounts.setName("name");
        chartOfAccounts.setPurposeId(0L);
        chartOfAccounts.setClassification(0L);
        chartOfAccounts.setDesc("desc");
        chartOfAccounts.setFunctionReqd(false);
        chartOfAccounts.setIsActiveForPosting(false);
        designation.setChartOfAccounts(chartOfAccounts);
        deptDesig.setDesignation(designation);
        final Department department = new Department();
        department.setId(0L);
        department.setName("name");
        department.setCode("code");
        deptDesig.setDepartment(department);
        deptDesig.setSanctionedPosts(0);
        deptDesig.setOutsourcedPosts(0);
        position.setDeptDesig(deptDesig);
        final List<Position> positions = Arrays.asList(position);
        when(mockPosMasterService.getPositionsForEmployee(0L, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(positions);

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getWorkFlowTypesWithItemsCount();

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockSession).close();
    }

    @Test
    public void testGetWorkFlowTypesWithItemsCount_PositionMasterServiceReturnsNoItems() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Configure PriorityService.getAllPriorities(...).
        final Priority priority1 = new Priority();
        priority1.setName("name");
        priority1.setCode("code");
        priority1.setWeight(0);
        final List<Priority> priorities = Arrays.asList(priority1);
        when(mockPriorityService.getAllPriorities()).thenReturn(priorities);

        when(mockSecurityUtils.getCurrentUser()).thenReturn(new User(UserType.CITIZEN));
        when(mockPosMasterService.getPositionsForEmployee(0L, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(Collections.emptyList());

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getWorkFlowTypesWithItemsCount();

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockSession).close();
    }

    @Test
    public void testGetInboxListByWorkFlowType() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        when(mockSecurityUtils.getCurrentUser()).thenReturn(new User(UserType.CITIZEN));

        // Configure PositionMasterService.getPositionsForEmployee(...).
        final Position position = new Position();
        position.setName("name");
        position.setPostOutsourced(false);
        final DeptDesig deptDesig = new DeptDesig();
        deptDesig.setId(0L);
        final Designation designation = new Designation();
        designation.setId(0L);
        designation.setName("name");
        designation.setCode("code");
        designation.setDescription("description");
        final CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail = new CChartOfAccountDetail();
        cChartOfAccountDetail.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail.setId(0L);
        chartOfAccounts.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail)));
        chartOfAccounts.setMajorCode("majorCode");
        chartOfAccounts.setMyClass(0L);
        chartOfAccounts.setGlcode("glcode");
        chartOfAccounts.setName("name");
        chartOfAccounts.setPurposeId(0L);
        chartOfAccounts.setClassification(0L);
        chartOfAccounts.setDesc("desc");
        chartOfAccounts.setFunctionReqd(false);
        chartOfAccounts.setIsActiveForPosting(false);
        designation.setChartOfAccounts(chartOfAccounts);
        deptDesig.setDesignation(designation);
        final Department department = new Department();
        department.setId(0L);
        department.setName("name");
        department.setCode("code");
        deptDesig.setDepartment(department);
        deptDesig.setSanctionedPosts(0);
        deptDesig.setOutsourcedPosts(0);
        position.setDeptDesig(deptDesig);
        final List<Position> positions = Arrays.asList(position);
        when(mockPosMasterService.getPositionsForEmployee(0L, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(positions);

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getInboxListByWorkFlowType("workFlowType", 0, 0, "priority");

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockSession).close();
    }

    @Test
    public void testGetInboxListByWorkFlowType_PositionMasterServiceReturnsNoItems() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        when(mockSecurityUtils.getCurrentUser()).thenReturn(new User(UserType.CITIZEN));
        when(mockPosMasterService.getPositionsForEmployee(0L, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(Collections.emptyList());

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getInboxListByWorkFlowType("workFlowType", 0, 0, "priority");

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockSession).close();
    }

    @Test
    public void testLogout() {
        // Setup
        final OAuth2Authentication authentication = new OAuth2Authentication(new OAuth2Request(new HashMap<>(), "clientId", Arrays.asList(), false, new HashSet<>(Arrays.asList("value")), new HashSet<>(Arrays.asList("value")), "redirectUri", new HashSet<>(Arrays.asList("value")), new HashMap<>()), null);
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);
        when(mockTokenStore.getAccessToken(new OAuth2Authentication(new OAuth2Request(new HashMap<>(), "clientId", Arrays.asList(), false, new HashSet<>(Arrays.asList("value")), new HashSet<>(Arrays.asList("value")), "redirectUri", new HashSet<>(Arrays.asList("value")), new HashMap<>()), null))).thenReturn(null);

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.logout(authentication);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockTokenStore).removeAccessToken(any(OAuth2AccessToken.class));
    }

    @Test
    public void testGetForwardDetails() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);

        // Configure EisUtilService.getEmployeeInfoList(...).
        final EmployeeView employeeView = new EmployeeView();
        final Employee employee = new Employee();
        employee.setCode("code");
        employee.setDateOfAppointment(new DateTime(2020, 1, 1, 1, 0, 0, 0));
        employee.setDateOfRetirement(new DateTime(2020, 1, 1, 1, 0, 0, 0));
        employee.setEmployeeStatus(EmployeeStatus.EMPLOYED);
        final EmployeeType employeeType = new EmployeeType();
        final CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail = new CChartOfAccountDetail();
        cChartOfAccountDetail.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail.setId(0L);
        chartOfAccounts.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail)));
        chartOfAccounts.setMajorCode("majorCode");
        chartOfAccounts.setMyClass(0L);
        chartOfAccounts.setGlcode("glcode");
        chartOfAccounts.setName("name");
        chartOfAccounts.setPurposeId(0L);
        chartOfAccounts.setClassification(0L);
        chartOfAccounts.setDesc("desc");
        chartOfAccounts.setFunctionReqd(false);
        chartOfAccounts.setIsActiveForPosting(false);
        employeeType.setChartOfAccounts(chartOfAccounts);
        employeeType.setName("name");
        employeeType.setId(0L);
        employee.setEmployeeType(employeeType);
        final Assignment assignment = new Assignment();
        assignment.setId(0L);
        final HeadOfDepartments headOfDepartments = new HeadOfDepartments();
        headOfDepartments.setAssignment(new Assignment());
        final Department hod = new Department();
        hod.setId(0L);
        hod.setName("name");
        hod.setCode("code");
        headOfDepartments.setHod(hod);
        headOfDepartments.setId(0L);
        assignment.setHodList(Arrays.asList(headOfDepartments));
        final Designation designation = new Designation();
        designation.setId(0L);
        designation.setName("name");
        designation.setCode("code");
        designation.setDescription("description");
        final CChartOfAccounts chartOfAccounts1 = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail1 = new CChartOfAccountDetail();
        cChartOfAccountDetail1.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail1.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail1.setId(0L);
        chartOfAccounts1.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail1)));
        chartOfAccounts1.setMajorCode("majorCode");
        chartOfAccounts1.setMyClass(0L);
        chartOfAccounts1.setGlcode("glcode");
        chartOfAccounts1.setName("name");
        chartOfAccounts1.setPurposeId(0L);
        chartOfAccounts1.setClassification(0L);
        chartOfAccounts1.setDesc("desc");
        chartOfAccounts1.setFunctionReqd(false);
        chartOfAccounts1.setIsActiveForPosting(false);
        designation.setChartOfAccounts(chartOfAccounts1);
        assignment.setDesignation(designation);
        assignment.setFunctionary(new Functionary(0, new BigDecimal("0.00"), "name", new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(), false));
        final CFunction function = new CFunction();
        function.setId(0L);
        function.setName("name");
        function.setCode("code");
        function.setType("type");
        function.setLlevel(0);
        function.setIsActive(false);
        function.setIsNotLeaf(false);
        function.setParentId(new CFunction());
        assignment.setFunction(function);
        final Fund fund = new Fund();
        fund.setId(0);
        fund.setName("name");
        fund.setCode("code");
        fund.setIdentifier('a');
        fund.setLlevel(new BigDecimal("0.00"));
        fund.setParentId(new Fund());
        fund.setIsnotleaf(false);
        fund.setIsactive(false);
        fund.setCreatedby(new User(UserType.CITIZEN));
        assignment.setFund(fund);
        employee.setAssignments(Arrays.asList(assignment));
        employeeView.setEmployee(employee);
        final List<EmployeeView> employeeViews = Arrays.asList(employeeView);
        when(mockEisService.getEmployeeInfoList(new HashMap<>())).thenReturn(employeeViews);

        // Configure EisUtilService.getAllDesignationByDept(...).
        final Designation designation1 = new Designation();
        designation1.setId(0L);
        designation1.setName("name");
        designation1.setCode("code");
        designation1.setDescription("description");
        final CChartOfAccounts chartOfAccounts2 = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail2 = new CChartOfAccountDetail();
        cChartOfAccountDetail2.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail2.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail2.setId(0L);
        chartOfAccounts2.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail2)));
        chartOfAccounts2.setMajorCode("majorCode");
        chartOfAccounts2.setMyClass(0L);
        chartOfAccounts2.setGlcode("glcode");
        chartOfAccounts2.setName("name");
        chartOfAccounts2.setPurposeId(0L);
        chartOfAccounts2.setClassification(0L);
        chartOfAccounts2.setDesc("desc");
        chartOfAccounts2.setFunctionReqd(false);
        chartOfAccounts2.setIsActiveForPosting(false);
        designation1.setChartOfAccounts(chartOfAccounts2);
        final List<Designation> designations = Arrays.asList(designation1);
        when(mockEisService.getAllDesignationByDept(0, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(designations);

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getForwardDetails(0, 0);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetForwardDetails_EisUtilServiceGetEmployeeInfoListReturnsNoItems() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);
        when(mockEisService.getEmployeeInfoList(new HashMap<>())).thenReturn(Collections.emptyList());

        // Configure EisUtilService.getAllDesignationByDept(...).
        final Designation designation = new Designation();
        designation.setId(0L);
        designation.setName("name");
        designation.setCode("code");
        designation.setDescription("description");
        final CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail = new CChartOfAccountDetail();
        cChartOfAccountDetail.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail.setId(0L);
        chartOfAccounts.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail)));
        chartOfAccounts.setMajorCode("majorCode");
        chartOfAccounts.setMyClass(0L);
        chartOfAccounts.setGlcode("glcode");
        chartOfAccounts.setName("name");
        chartOfAccounts.setPurposeId(0L);
        chartOfAccounts.setClassification(0L);
        chartOfAccounts.setDesc("desc");
        chartOfAccounts.setFunctionReqd(false);
        chartOfAccounts.setIsActiveForPosting(false);
        designation.setChartOfAccounts(chartOfAccounts);
        final List<Designation> designations = Arrays.asList(designation);
        when(mockEisService.getAllDesignationByDept(0, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(designations);

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getForwardDetails(0, 0);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetForwardDetails_EisUtilServiceGetAllDesignationByDeptReturnsNoItems() {
        // Setup
        final ResponseEntity<String> expectedResult = new ResponseEntity<>("body", HttpStatus.OK);

        // Configure EisUtilService.getEmployeeInfoList(...).
        final EmployeeView employeeView = new EmployeeView();
        final Employee employee = new Employee();
        employee.setCode("code");
        employee.setDateOfAppointment(new DateTime(2020, 1, 1, 1, 0, 0, 0));
        employee.setDateOfRetirement(new DateTime(2020, 1, 1, 1, 0, 0, 0));
        employee.setEmployeeStatus(EmployeeStatus.EMPLOYED);
        final EmployeeType employeeType = new EmployeeType();
        final CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail = new CChartOfAccountDetail();
        cChartOfAccountDetail.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail.setId(0L);
        chartOfAccounts.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail)));
        chartOfAccounts.setMajorCode("majorCode");
        chartOfAccounts.setMyClass(0L);
        chartOfAccounts.setGlcode("glcode");
        chartOfAccounts.setName("name");
        chartOfAccounts.setPurposeId(0L);
        chartOfAccounts.setClassification(0L);
        chartOfAccounts.setDesc("desc");
        chartOfAccounts.setFunctionReqd(false);
        chartOfAccounts.setIsActiveForPosting(false);
        employeeType.setChartOfAccounts(chartOfAccounts);
        employeeType.setName("name");
        employeeType.setId(0L);
        employee.setEmployeeType(employeeType);
        final Assignment assignment = new Assignment();
        assignment.setId(0L);
        final HeadOfDepartments headOfDepartments = new HeadOfDepartments();
        headOfDepartments.setAssignment(new Assignment());
        final Department hod = new Department();
        hod.setId(0L);
        hod.setName("name");
        hod.setCode("code");
        headOfDepartments.setHod(hod);
        headOfDepartments.setId(0L);
        assignment.setHodList(Arrays.asList(headOfDepartments));
        final Designation designation = new Designation();
        designation.setId(0L);
        designation.setName("name");
        designation.setCode("code");
        designation.setDescription("description");
        final CChartOfAccounts chartOfAccounts1 = new CChartOfAccounts();
        final CChartOfAccountDetail cChartOfAccountDetail1 = new CChartOfAccountDetail();
        cChartOfAccountDetail1.setGlCodeId(new CChartOfAccounts());
        cChartOfAccountDetail1.setDetailTypeId(new Accountdetailtype("name", "description", "tablename", "columnname", "attributename", new BigDecimal("0.00"), false));
        cChartOfAccountDetail1.setId(0L);
        chartOfAccounts1.setChartOfAccountDetails(new HashSet<>(Arrays.asList(cChartOfAccountDetail1)));
        chartOfAccounts1.setMajorCode("majorCode");
        chartOfAccounts1.setMyClass(0L);
        chartOfAccounts1.setGlcode("glcode");
        chartOfAccounts1.setName("name");
        chartOfAccounts1.setPurposeId(0L);
        chartOfAccounts1.setClassification(0L);
        chartOfAccounts1.setDesc("desc");
        chartOfAccounts1.setFunctionReqd(false);
        chartOfAccounts1.setIsActiveForPosting(false);
        designation.setChartOfAccounts(chartOfAccounts1);
        assignment.setDesignation(designation);
        assignment.setFunctionary(new Functionary(0, new BigDecimal("0.00"), "name", new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(), false));
        final CFunction function = new CFunction();
        function.setId(0L);
        function.setName("name");
        function.setCode("code");
        function.setType("type");
        function.setLlevel(0);
        function.setIsActive(false);
        function.setIsNotLeaf(false);
        function.setParentId(new CFunction());
        assignment.setFunction(function);
        final Fund fund = new Fund();
        fund.setId(0);
        fund.setName("name");
        fund.setCode("code");
        fund.setIdentifier('a');
        fund.setLlevel(new BigDecimal("0.00"));
        fund.setParentId(new Fund());
        fund.setIsnotleaf(false);
        fund.setIsactive(false);
        fund.setCreatedby(new User(UserType.CITIZEN));
        assignment.setFund(fund);
        employee.setAssignments(Arrays.asList(assignment));
        employeeView.setEmployee(employee);
        final List<EmployeeView> employeeViews = Arrays.asList(employeeView);
        when(mockEisService.getEmployeeInfoList(new HashMap<>())).thenReturn(employeeViews);

        when(mockEisService.getAllDesignationByDept(0, new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(Collections.emptyList());

        // Run the test
        final ResponseEntity<String> result = employeeControllerUnderTest.getForwardDetails(0, 0);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetWorkflowItemsByUserAndWFType() throws Exception {
        // Setup
        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Run the test
        final List<StateAware> result = employeeControllerUnderTest.getWorkflowItemsByUserAndWFType(0L, Arrays.asList(0L), "workFlowType", 0, 0, "priority");

        // Verify the results
        verify(mockSession).close();
    }

    @Test(expected = ClassNotFoundException.class)
    public void testGetWorkflowItemsByUserAndWFType_ThrowsClassNotFoundException() throws Exception {
        // Setup
        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Run the test
        employeeControllerUnderTest.getWorkflowItemsByUserAndWFType(0L, Arrays.asList(0L), "workFlowType", 0, 0, "priority");
    }

    @Test
    public void testGetWorkflowItemsCountByWFType() throws Exception {
        // Setup
        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Run the test
        final Number result = employeeControllerUnderTest.getWorkflowItemsCountByWFType(0L, Arrays.asList(0L), "workFlowType", "priority");

        // Verify the results
        verify(mockSession).close();
    }

    @Test(expected = ClassNotFoundException.class)
    public void testGetWorkflowItemsCountByWFType_ThrowsClassNotFoundException() throws Exception {
        // Setup
        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Run the test
        employeeControllerUnderTest.getWorkflowItemsCountByWFType(0L, Arrays.asList(0L), "workFlowType", "priority");
    }

    @Test
    public void testGetWorkflowTypesWithCount() throws Exception {
        // Setup
        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Configure PriorityService.getAllPriorities(...).
        final Priority priority1 = new Priority();
        priority1.setName("name");
        priority1.setCode("code");
        priority1.setWeight(0);
        final List<Priority> priorities = Arrays.asList(priority1);
        when(mockPriorityService.getAllPriorities()).thenReturn(priorities);

        // Run the test
        final List<HashMap<String, Object>> result = employeeControllerUnderTest.getWorkflowTypesWithCount(0L, Arrays.asList(0L));

        // Verify the results
        verify(mockSession).close();
    }

    @Test
    public void testGetWorkflowTypesWithCount_PriorityServiceGetAllPrioritiesReturnsNoItems() throws Exception {
        // Setup
        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        when(mockPriorityService.getAllPriorities()).thenReturn(Collections.emptyList());

        // Run the test
        final List<HashMap<String, Object>> result = employeeControllerUnderTest.getWorkflowTypesWithCount(0L, Arrays.asList(0L));

        // Verify the results
        assertEquals(Collections.emptyList(), result);
        verify(mockSession).close();
    }

    @Test(expected = ClassNotFoundException.class)
    public void testGetWorkflowTypesWithCount_ThrowsClassNotFoundException() throws Exception {
        // Setup
        // Configure PersistenceService.getSession(...).
        final Session mockSession = mock(Session.class);
        when(mockEntityQueryService.getSession()).thenReturn(mockSession);

        // Configure PriorityService.getPriorityByCode(...).
        final Priority priority = new Priority();
        priority.setName("name");
        priority.setCode("code");
        priority.setWeight(0);
        when(mockPriorityService.getPriorityByCode("code")).thenReturn(priority);

        // Configure WorkflowTypeService.getEnabledWorkflowTypeByType(...).
        final WorkflowType workflowType = new WorkflowType();
        workflowType.setId(0L);
        final Module module = new Module();
        module.setName("name");
        module.setEnabled(false);
        module.setParentModule(new Module());
        module.setDisplayName("displayName");
        module.setOrderNumber(0);
        final Action action = new Action();
        action.setId(0L);
        action.setName("name");
        action.setUrl("url");
        action.setQueryParams("queryParams");
        final Role role = new Role();
        role.setId(0L);
        role.setName("name");
        role.setDescription("description");
        role.setInternal(false);
        action.setRoles(new HashSet<>(Arrays.asList(role)));
        action.setParentModule(new Module());
        action.setOrderNumber(0);
        action.setDisplayName("displayName");
        action.setEnabled(false);
        action.setContextRoot("contextRoot");
        module.setActions(new HashSet<>(Arrays.asList(action)));
        module.setContextRoot("contextRoot");
        workflowType.setModule(module);
        workflowType.setType("type");
        workflowType.setDisplayName("displayName");
        workflowType.setLink("link");
        workflowType.setTypeFQN("typeFQN");
        workflowType.setEnabled(false);
        workflowType.setGrouped(false);
        when(mockWorkflowTypeService.getEnabledWorkflowTypeByType("type")).thenReturn(workflowType);

        // Configure PriorityService.getAllPriorities(...).
        final Priority priority1 = new Priority();
        priority1.setName("name");
        priority1.setCode("code");
        priority1.setWeight(0);
        final List<Priority> priorities = Arrays.asList(priority1);
        when(mockPriorityService.getAllPriorities()).thenReturn(priorities);

        // Run the test
        employeeControllerUnderTest.getWorkflowTypesWithCount(0L, Arrays.asList(0L));
    }
}
