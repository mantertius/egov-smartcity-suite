/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.mrs.application.reports.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.web.utils.WebUtils;
import org.egov.mrs.application.MarriageConstants;
import org.egov.mrs.application.reports.repository.MarriageRegistrationReportsRepository;
import org.egov.mrs.domain.entity.MarriageCertificate;
import org.egov.mrs.domain.entity.MarriageRegistration;
import org.egov.mrs.domain.entity.ReIssue;
import org.egov.mrs.domain.entity.SearchModel;
import org.egov.mrs.domain.entity.SearchResult;
import org.egov.mrs.domain.enums.MaritalStatus;
import org.egov.mrs.entity.es.MarriageRegistrationIndex;
import org.egov.mrs.masters.entity.MarriageRegistrationUnit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MarriageRegistrationReportsService {
    
    private Map<String, Object> reportParams = null;
    private ReportRequest reportInput = null;
    private ReportOutput reportOutput = null;
    
    private static final String TO_DATE = "toDate";

    private static final String FROM_DATE = "fromDate";

    private static final String HUSBAND = "husband";

    private static final String MARRIAGE_REGISTRATION = "marriageRegistration";

    private static final String UNION = " union ";

    final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private ReportService reportService;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Autowired
    private MarriageRegistrationReportsRepository marriageRegistrationReportsRepository;
    
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    public Date resetFromDateTimeStamp(final Date date){
        Calendar cal1 = Calendar.getInstance();  
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }
    public Date resetToDateTimeStamp(final Date date){
        Calendar cal1 = Calendar.getInstance();  
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 23);
        cal1.set(Calendar.MINUTE, 59);
        cal1.set(Calendar.SECOND, 59);
        cal1.set(Calendar.MILLISECOND, 999);
        return cal1.getTime();
    }
    public Date getMonthStartday(final String monthyear) {
        Date monthStartDate = new Date();
        if (monthyear != null) {
            final String[] month_year = monthyear.split("/");
            final Calendar calnew = Calendar.getInstance();
            calnew.set(Calendar.MONTH, Integer.parseInt(month_year[0]) - 1);
            calnew.set(Calendar.YEAR, Integer.parseInt(month_year[1]));
            calnew.set(Calendar.HOUR_OF_DAY, 0);
            calnew.set(Calendar.MINUTE, 0);
            calnew.set(Calendar.SECOND, 0);
            calnew.set(Calendar.DAY_OF_MONTH,
                    calnew.getActualMinimum(Calendar.DAY_OF_MONTH));
            monthStartDate = calnew.getTime();

        }
        return monthStartDate;
    }

    public Date getMonthEndday(final String monthyear) {
        Date monthEndDate = new Date();
        if (monthyear != null) {
            final String[] month_year = monthyear.split("/");
            final Calendar calnew = Calendar.getInstance();
            calnew.set(Calendar.MONTH, Integer.parseInt(month_year[0]) - 1);
            calnew.set(Calendar.YEAR, Integer.parseInt(month_year[1]));
            calnew.set(Calendar.HOUR_OF_DAY, 23);
            calnew.set(Calendar.MINUTE, 59);
            calnew.set(Calendar.SECOND, 59);
            calnew.set(Calendar.MILLISECOND, 999);
            calnew.set(Calendar.DAY_OF_MONTH,
                    calnew.getActualMaximum(Calendar.DAY_OF_MONTH));
            monthEndDate = calnew.getTime();

        }
        return monthEndDate;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Object[]> searchMarriageRegistrationsForCertificateReport(final MarriageCertificate certificate)
            throws ParseException {

        final Map<String, String> params = new HashMap<>();

        final StringBuilder queryStrForRegistration = new StringBuilder(500);
        queryStrForRegistration
                .append(
                "Select reg.registrationno,reg.dateofmarriage,reg.applicationdate,reg.rejectionreason,cert.certificateno,cert.certificatetype,cert.certificatedate, brndy.name,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.id");
        queryStrForRegistration
                .append(" from egmrs_registration reg, egmrs_certificate cert, eg_boundary brndy,egw_status st");
        queryStrForRegistration
                .append(
                " where reg.zone = brndy.id and reg.status = st.id and st.code in('REGISTERED')  and reg.id = cert.registration and cert.reissue is null ");
        if (certificate.getRegistration().getZone() != null) {
            queryStrForRegistration.append(" and reg.zone=to_number(:zone,'999999')");
            params.put("zone", String.valueOf(certificate.getRegistration().getZone().getId()));
        }

        if (certificate.getCertificateType() != null && !"ALL".equals(certificate.getCertificateType())) {
            queryStrForRegistration.append(" and cert.certificatetype=:certificatetype");
            params.put("certificatetype", certificate.getCertificateType());
        } else if (certificate.getCertificateType() != null && "ALL".equals(certificate.getCertificateType()))
            queryStrForRegistration.append(" and cert.certificatetype in('REGISTRATION','REISSUE','REJECTION')");

        if (certificate.getFromDate() != null) {
            queryStrForRegistration
                    .append(
                    " and cert.certificatedate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss')");
            params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(certificate.getFromDate())));
        }
        
        if (certificate.getToDate() != null) {
            queryStrForRegistration
                    .append(
                    " and cert.certificatedate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
            params.put(TO_DATE,  sf.format(resetToDateTimeStamp(certificate.getToDate())));
        }

        if (certificate.getRegistration().getRegistrationNo() != null) {
            queryStrForRegistration.append(" and reg.registrationno=:registrationNo");
            params.put("registrationNo", certificate.getRegistration().getRegistrationNo());
        }

        final StringBuilder queryStrForReissue = new StringBuilder(500);
        queryStrForReissue
                .append(
                "Select reg.registrationno,reg.dateofmarriage,reg.applicationdate,reg.rejectionreason,cert.certificateno,cert.certificatetype,cert.certificatedate, brndy.name,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.id");
        queryStrForReissue.append(
                " from egmrs_registration reg,egmrs_reissue reis, egmrs_certificate cert, eg_boundary brndy,egw_status st ");
        queryStrForReissue
                .append(
                "where reg.zone = brndy.id and reg.id=reis.registration and reis.status = st.id and st.code in('CERTIFICATEREISSUED','REJECTION')  and reis.id = cert.reissue and cert.registration is null");
        if (certificate.getRegistration().getZone() != null) {
            queryStrForReissue.append(" and reg.zone=to_number(:zone,'999999')");
            params.put("zone", String.valueOf(certificate.getRegistration().getZone().getId()));
        }

        if (certificate.getCertificateType() != null && !"ALL".equals(certificate.getCertificateType())) {
            queryStrForReissue.append(" and cert.certificatetype=:certificatetype");
            params.put("certificatetype", certificate.getCertificateType());
        } else if (certificate.getCertificateType() != null && "ALL".equals(certificate.getCertificateType()))
            queryStrForReissue.append(" and cert.certificatetype in('REGISTRATION','REISSUE','REJECTION')");

        
        if (certificate.getFromDate() != null) {
            queryStrForReissue
                    .append(
                    " and cert.certificatedate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss')");
            params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(certificate.getFromDate())));
        }
        
        if (certificate.getToDate() != null) {
            queryStrForReissue
                    .append(
                    " and cert.certificatedate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
            params.put(TO_DATE,  sf.format(resetToDateTimeStamp(certificate.getToDate())));
        }

        if (certificate.getRegistration().getRegistrationNo() != null) {
            queryStrForReissue.append(" and reg.registrationno=:registrationNo");
            params.put("registrationNo", certificate.getRegistration().getRegistrationNo());
        }

        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegistration.toString());

        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReissue.toString());

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
        for (final String param : params.keySet())
            query.setParameter(param, params.get(param));
        return query.list();

    }

    public String[] searchRegistrationOfHusbandAgeWise(final int year) throws ParseException {

        return marriageRegistrationReportsRepository.getHusbandCountAgeWise(year);
    }

    public String[] searchRegistrationOfWifeAgeWise(final int year) throws ParseException {

        return marriageRegistrationReportsRepository.getWifeCountAgeWise(year);
    }

    public String[] searchRegistrationActWise(final MarriageRegistration registration, final int year) throws ParseException {

        return marriageRegistrationReportsRepository.searchMarriageRegistrationsByYearAndAct(year, registration.getMarriageAct().getId());
    }

    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getAgewiseDetails(final String age, final String applicant, final int year)
            throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class,
                MARRIAGE_REGISTRATION);
        final String[] values = age.split("-");

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        final Date fromDate = formatter.parse(year + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(year + "/" + 12 + "/" + 31);
        if (age != null && HUSBAND.equals(applicant)) {
            criteria.createAlias("marriageRegistration.husband", HUSBAND).add(Restrictions
                    .between("husband.ageInYearsAsOnMarriage", Integer.valueOf(values[0]), Integer.valueOf(values[1])));
            if(fromDate != null) {
            criteria.add(Restrictions.ge("marriageRegistration.applicationDate",fromDate));
            }
            if(toDate != null) {
                criteria.add(Restrictions.le("marriageRegistration.applicationDate",toDate));
            }
        } else {
            criteria.createAlias("marriageRegistration.wife", "wife").add(Restrictions
                    .between("wife.ageInYearsAsOnMarriage", Integer.valueOf(values[0]), Integer.valueOf(values[1])));
            if(fromDate != null) {
                criteria.add(Restrictions.ge("marriageRegistration.applicationDate",fromDate));
            }
            if(toDate != null) {
                criteria.add(Restrictions.le("marriageRegistration.applicationDate",toDate));
            }
        }
        criteria.createAlias("marriageRegistration.status", "status").add(Restrictions.in("status.code",
                new String[] { MarriageRegistration.RegistrationStatus.APPROVED.toString() }));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchStatusAtTimeOfMarriage(final MarriageRegistration registration)
            throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class, MARRIAGE_REGISTRATION)
                .createAlias("marriageRegistration.status", "status");
        if (registration.getHusband().getMaritalStatus() != null)
            criteria.createAlias("marriageRegistration.husband", HUSBAND)
                    .add(Restrictions.eq("husband.maritalStatus", registration.getHusband().getMaritalStatus()));
        criteria.createAlias("marriageRegistration.wife", "wife")
                .add(Restrictions.eq("wife.maritalStatus", registration.getHusband().getMaritalStatus()));

        criteria.add(Restrictions.in("status.code",
                new String[] { MarriageRegistration.RegistrationStatus.REGISTERED.toString() }));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<String[]> getHusbandCountByMaritalStatus(final Date fromDate,final Date toDate,String maritalStatus,String applicanType) throws ParseException {
        final Map<String, String> params = new HashMap<>();
        final StringBuilder queryStrForHusbandCount = new StringBuilder(500);
        queryStrForHusbandCount
                .append(
                "select app.relationstatus,to_char(app.createddate,'Mon'),count(*) from egmrs_applicant as app ,egmrs_registration as reg where reg.husband = app.id  ");
       if(maritalStatus != null){
           queryStrForHusbandCount.append(" and app.relationstatus=:maritalStatus");
           params.put("maritalStatus", maritalStatus);
       }
       if(fromDate != null){
           queryStrForHusbandCount
           .append(
           " and app.createddate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss') ");
           params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(fromDate)));
       }
       
       if(toDate != null){
           queryStrForHusbandCount
           .append(
           " and app.createddate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
           params.put(TO_DATE,  sf.format(resetToDateTimeStamp(toDate)));
       }
       
       
       
       queryStrForHusbandCount.append(" group by app.relationstatus, to_char(app.createddate,'Mon') order by to_char(app.createddate,'Mon') desc");
       final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryStrForHusbandCount.toString());
       for (final String param : params.keySet())
           query.setParameter(param, params.get(param)); 
       return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<String[]> getWifeCountByMaritalStatus(final Date fromDate,final Date toDate,String maritalStatus,String applicanType) throws ParseException {

        final Map<String, String> params = new HashMap<>();
        final StringBuilder queryStrForWifeCount = new StringBuilder(500);
        queryStrForWifeCount
                .append(
                "select app.relationstatus,to_char(app.createddate,'Mon'),count(*) from egmrs_applicant as app ,egmrs_registration as reg where reg.wife = app.id  ");
       if(maritalStatus != null){
           queryStrForWifeCount.append(" and app.relationstatus=:maritalStatus");
           params.put("maritalStatus", maritalStatus);
       }
       if(fromDate != null){
           queryStrForWifeCount
           .append(
           " and app.createddate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss')");
           params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(fromDate)));
       }
       
       if(toDate != null){
           queryStrForWifeCount
           .append(
           " and app.createddate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
           params.put(TO_DATE, sf.format(resetToDateTimeStamp(toDate)));
       }
       
       queryStrForWifeCount.append(" group by app.relationstatus, to_char(app.createddate,'Mon') order by to_char(app.createddate,'Mon') desc");
       final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryStrForWifeCount.toString());
       for (final String param : params.keySet())
           query.setParameter(param, params.get(param)); 
       return query.list();
    
    }

    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getByMaritalStatusDetails(final String applicant,
            final String maritalStatus,final Date fromDate,final Date toDate) throws ParseException {
         Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class,
                MARRIAGE_REGISTRATION);

        if (maritalStatus != null && HUSBAND.equals(applicant)) {
            criteria = criteria.createAlias("marriageRegistration.husband", HUSBAND);
            if(fromDate != null){
            criteria.add(Restrictions.ge("husband.createdDate", resetFromDateTimeStamp(fromDate)));
            }
            
            if(toDate != null){
                criteria.add(Restrictions.le("husband.createdDate", resetToDateTimeStamp(toDate)));
             }
            
            criteria.add(Restrictions.eq("husband.maritalStatus", MaritalStatus.valueOf(maritalStatus)));
        } else {
            criteria =  criteria.createAlias("marriageRegistration.wife", "wife");
            if(fromDate != null) {
            criteria.add(Restrictions.ge("wife.createdDate", resetFromDateTimeStamp(fromDate)));
            }
            if(toDate != null) {
                criteria.add(Restrictions.le("wife.createdDate",resetToDateTimeStamp(toDate)));
            }
            if(maritalStatus != null) {
            criteria.add(Restrictions.eq("wife.maritalStatus", MaritalStatus.valueOf(maritalStatus)));
            }
        }

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationBydate(
            final MarriageRegistration registration) throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION);

        if (null != registration.getMarriageRegistrationUnit()
                && registration.getMarriageRegistrationUnit().getId() != null)
            criteria.add(Restrictions.eq("marriageRegistrationUnit.id",
                    registration.getMarriageRegistrationUnit().getId()));
        if (null != registration.getZone()
                && registration.getZone().getId() != null)
            criteria.add(Restrictions.eq("zone.id", registration.getZone()
                    .getId()));
        if (null != registration.getStatus()
                && registration.getStatus().getCode() != null)
            criteria.createAlias("marriageRegistration.status", "status").add(
                    Restrictions.eq("status.code", registration.getStatus()
                            .getCode()));
        if (registration.getFromDate() != null )
            criteria.add(Restrictions.ge(
                    "marriageRegistration.applicationDate",
                    resetFromDateTimeStamp(registration.getFromDate())));
        if (registration.getToDate() != null )
            criteria.add(Restrictions.le(
                    "marriageRegistration.applicationDate",
                    resetToDateTimeStamp(registration.getToDate()) ));
        criteria.addOrder(Order.desc("marriageRegistration.applicationDate"));
        if (registration.getFromDate() != null)
        {

            final Calendar cal = Calendar.getInstance();
            final Date todate = cal.getTime();
            criteria.add(Restrictions.between(
                    "marriageRegistration.applicationDate",
                    registration.getFromDate(), todate!=null?todate:new Date()));
        }
        if (registration.getToDate() == null)
        {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2009);
            final Date fromdate = cal.getTime();

            criteria.add(Restrictions.between(
                    "marriageRegistration.applicationDate",
                    fromdate, registration.getToDate() !=null ? DateUtils.addDays(registration.getToDate(), 1) : new Date()));
        }

        return criteria.list();
    }
    
@SuppressWarnings("unchecked")
public List<String[]> getCountOfApplications(final MarriageRegistration registration){
        
    final Map<String, String> params = new HashMap<>();
    final StringBuilder queryStrForRegCount = new StringBuilder(500);
    queryStrForRegCount
            .append("(select regunit.name,count(*),to_char(applicationdate,'Mon'),'registration' from egmrs_registration reg,egmrs_registrationunit regunit,egw_status st ");
    queryStrForRegCount.append("where reg.registrationunit=regunit.id and reg.status = st.id and st.code='REGISTERED' ");
   if(registration.getMonth_year() != null){
       queryStrForRegCount.append(" and applicationdate between to_timestamp(:fromdate,'yyyy-MM-dd HH24:mi:ss') and to_timestamp(:todate,'YYYY-MM-DD HH24:MI:SS') ");
       params.put("fromdate",  sf.format(getMonthStartday(registration.getMonth_year())));
       params.put("todate", sf.format(getMonthEndday(registration.getMonth_year())));
   }
   if(registration.getMarriageRegistrationUnit().getId() != null){
       queryStrForRegCount
       .append(" and registrationunit=to_number(:registrationunit,'999999')");
       params.put("registrationunit", registration.getMarriageRegistrationUnit().getId().toString());
   }
   
   queryStrForRegCount.append("group by regunit.name,to_char(applicationdate,'Mon') order by regunit.name)");
   final StringBuilder queryStrForReissueCount = new StringBuilder(500);
   queryStrForReissueCount
           .append("(select regunit.name,count(*),to_char(applicationdate,'Mon'),'reissue' from egmrs_reissue rei,egmrs_registrationunit regunit,egw_status st");
   queryStrForReissueCount.append(" where rei.registrationunit=regunit.id and rei.status = st.id and st.code='CERTIFICATEREISSUED' ");
  if(registration.getMonth_year() != null){
      queryStrForReissueCount.append(" and applicationdate between to_timestamp(:fromdate,'yyyy-MM-dd HH24:mi:ss') and to_timestamp(:todate,'YYYY-MM-DD HH24:MI:SS') ");
      params.put("fromdate",  sf.format(getMonthStartday(registration.getMonth_year())));
      params.put("todate", sf.format(getMonthEndday(registration.getMonth_year())));
  }
  if(registration.getMarriageRegistrationUnit().getId() != null){
      queryStrForReissueCount
      .append(" and registrationunit=to_number(:registrationunit,'999999')");
      params.put("registrationunit", registration.getMarriageRegistrationUnit().getId().toString());
  }
 
 queryStrForReissueCount.append("group by regunit.name,to_char(applicationdate,'Mon') order by regunit.name)");
  
  final StringBuilder aggregateQueryStr = new StringBuilder();
  aggregateQueryStr.append(queryStrForRegCount.toString());

  aggregateQueryStr.append(UNION);
  aggregateQueryStr.append(queryStrForReissueCount.toString());
  
   final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
   for (final String param : params.keySet())
       query.setParameter(param, params.get(param)); 
   return query.list();
    }


    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationBymonth(
            final String month,final String registrationUnit) throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION).createAlias("marriageRegistration.status", "status");;
        if (month != null)
            criteria.add(Restrictions.between(
                    "marriageRegistration.applicationDate",
                    getMonthStartday(month),
                    getMonthEndday(month)));

        if (registrationUnit != null)
            criteria.createAlias(MARRIAGE_REGISTRATION+".marriageRegistrationUnit", "regunit").add(Restrictions.eq("regunit.name",
                    registrationUnit));
        criteria.add(Restrictions.in("status.code",
                new String[] { MarriageRegistration.RegistrationStatus.REGISTERED.toString() }));
        return criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<ReIssue> searchReissueBymonth(
            final String month,final String registrationUnit) throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                ReIssue.class, "reissue").createAlias("reissue.status", "status");;
        if (month != null)
            criteria.add(Restrictions.between(
                    "reissue.applicationDate",
                    getMonthStartday(month),
                    getMonthEndday(month)));

        if (registrationUnit != null)
            criteria.createAlias("reissue.marriageRegistrationUnit", "regunit").add(Restrictions.eq("regunit.name",
                    registrationUnit));
        criteria.add(Restrictions.in("status.code",
                new String[] {"CERTIFICATEREISSUED"}));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationByreligion(
            final MarriageRegistration registration, final int year) throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION)
                .createAlias("marriageRegistration.status", "status");
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        final Date fromDate = formatter.parse(year + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(year + "/" + 12 + "/" + 31);
        if (null != registration.getHusband().getReligion()
                && registration.getHusband().getReligion().getId() != null)
            criteria.createAlias("marriageRegistration.husband", HUSBAND)
                    .add(Restrictions.eq("husband.religion.id", registration
                            .getHusband().getReligion().getId()));
        if (null != registration.getWife().getReligion()
                && registration.getWife().getReligion().getId() != null)
            criteria.createAlias("marriageRegistration.wife", "wife").add(
                    Restrictions.eq("wife.religion.id", registration.getWife()
                            .getReligion().getId()));
        if (null != fromDate)
            criteria.add(Restrictions.ge(
                    "marriageRegistration.applicationDate", resetFromDateTimeStamp(fromDate)));
        if (null != toDate)
            criteria.add(Restrictions.le(
                    "marriageRegistration.applicationDate",resetToDateTimeStamp(toDate)));

        /*criteria.add(Restrictions.in("status.code",
                new String[] { MarriageRegistration.RegistrationStatus.APPROVED
                        .toString() }));*/
        return criteria.list();
    }

    public String[] searchRegistrationMrActWise(final int year) {
        return marriageRegistrationReportsRepository.searchMarriageRegistrationsByYear(year);

    }

    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getActwiseDetails(final int year, final String act)
            throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION).createAlias("marriageRegistration.status", "status");

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        final Date fromDate = formatter.parse(year + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(year + "/" + 12 + "/" + 31);
        if (act != null)
            criteria.createAlias("marriageRegistration.marriageAct",
                    "marriageAct")
                    .add(Restrictions.eq("marriageAct.name", act));
        if (fromDate != null)
            criteria.add(Restrictions.ge(
                    "marriageRegistration.applicationDate",resetFromDateTimeStamp(fromDate)));
        
        if (toDate != null)
            criteria.add(Restrictions.le(
                    "marriageRegistration.applicationDate",resetToDateTimeStamp(toDate)));

        criteria.add(Restrictions.in("status.code",
                new String[] { MarriageRegistration.RegistrationStatus.APPROVED.toString() }));
        return criteria.list();

    }
    
    
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getmonthWiseActDetails(final int year,
            final int month, final Long actid) throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION).createAlias("marriageRegistration.status", "status");
        final String month_year = month + "/" + year;
        if (actid != null)
            criteria.createAlias("marriageRegistration.marriageAct",
                    "marriageAct")
                    .add(Restrictions.eq("marriageAct.id", actid));
        if (month_year != null)
            criteria.add(Restrictions.between("marriageRegistration.applicationDate", getMonthStartday(month_year),
                    getMonthEndday(month_year)));
        criteria.add(Restrictions.in("status.code",
                new String[] { MarriageRegistration.RegistrationStatus.APPROVED.toString() }));
        return criteria.list();
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Object[]> getAgeingRegDetails(final String day,final int year)
            throws ParseException {
        final String[] values = day.split("-");

        final StringBuilder queryStrForRegistration = new StringBuilder(500);
        final Map<String, Double> params = new HashMap<>();

        queryStrForRegistration
                .append(
                        "Select reg.applicationno,reg.registrationno,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.dateofmarriage,reg.applicationdate,reg.placeofmarriage, brndy.name,st.code,'Marriage Registration',state.owner_pos,state.nextaction");
        queryStrForRegistration
                .append(" from egmrs_registration reg,egmrs_applicant app, eg_boundary brndy,egw_status st,eg_wf_states state");
        queryStrForRegistration
                .append(
                        " where reg.state_id = state.id and reg.zone = brndy.id and reg.status = st.id and st.code not in ('REGISTERED','CANCELLED') and EXTRACT(EPOCH FROM date_trunc('day',(now()-reg.applicationdate)))/60/60/24 between :fromdays and :todays ");
        params.put("fromdays", Double.valueOf(values[0]));
        params.put("todays", Double.valueOf(values[1]));

        final StringBuilder queryStrForReissue = new StringBuilder(500);
        queryStrForReissue
                .append(
                        "Select rei.applicationno,reg.registrationno,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.dateofmarriage,rei.applicationdate,reg.placeofmarriage,brndy.name,st.code,'Reissue',state1.owner_pos,state1.nextaction as action1");
        queryStrForReissue
                .append(" from egmrs_reissue rei,egmrs_registration reg, egmrs_applicant app, eg_boundary brndy,egw_status st,eg_wf_states state1");
        queryStrForReissue
                .append(
                        " where rei.state_id = state1.id and rei.registration=reg.id and rei.zone = brndy.id and rei.status = st.id and st.code not in ('CERTIFICATEREISSUED','CANCELLED') and EXTRACT(EPOCH FROM date_trunc('day',(now()-rei.applicationdate)))/60/60/24 between :fromdays and :todays ");
        params.put("fromdays", Double.valueOf(values[0]));
        params.put("todays", Double.valueOf(values[1]));

        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegistration.toString());

        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReissue.toString());

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
        for (final String param : params.keySet())
            query.setParameter(param, params.get(param));
        return query.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<String[]> searchRegistrationbyDays(final int year) throws ParseException {

        final Map<String, Integer> params = new HashMap<>();
        final StringBuilder queryStrForRegAgeingDetails = new StringBuilder(500);
        queryStrForRegAgeingDetails
                .append(
                        "(Select EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24, count(*),st.code ");
        queryStrForRegAgeingDetails
                .append(" from egmrs_registration reg,egw_status st");
        queryStrForRegAgeingDetails
                .append(
                        " where status = st.id and st.code not in ('REGISTERED','CANCELLED') and extract(year from applicationdate)=:year group by st.code,EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24 order by EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24) ");
        params.put("year", year);

        final StringBuilder queryStrForReIssueAgeingDetails = new StringBuilder(500);
        queryStrForReIssueAgeingDetails
                .append(
                        "(Select EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24, count(*),st.code ");
        queryStrForReIssueAgeingDetails
                .append(" from egmrs_reissue,egw_status st ");
        queryStrForReIssueAgeingDetails
                .append(
                        " where status = st.id and st.code not in ('CERTIFICATEREISSUED','CANCELLED') and extract(year from applicationdate)=:year group by st.code,EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24 order by EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24) ");
        params.put("year", year);

        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegAgeingDetails.toString());

        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReIssueAgeingDetails.toString());

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
        for (final String param : params.keySet())
            query.setParameter(param, params.get(param));
        return query.list();

    }
    
    @SuppressWarnings("unchecked")
    public List<String[]> getCountOfApplnsStatusWise(final String status,final Date fromDate,final Date toDate,final MarriageRegistrationUnit registrationUnit ){
       
        final Map<String, String> params = new HashMap<>();
        final StringBuilder queryStrForRegCount = new StringBuilder(500);
        queryStrForRegCount
                .append("select regunit.name,st.code,count(*) from egmrs_registration reg,egmrs_registrationunit regunit,egw_status st ");
        queryStrForRegCount.append("where reg.registrationunit=regunit.id and reg.status = st.id  ");
       if(fromDate != null){
           queryStrForRegCount.append(" and applicationdate >= to_timestamp(:fromdate,'yyyy-MM-dd HH24:mi:ss')");
           params.put("fromdate",  sf.format(resetFromDateTimeStamp(fromDate)));
       }
       if(toDate != null){
           queryStrForRegCount.append(" and applicationdate <=to_timestamp(:todate,'YYYY-MM-DD HH24:MI:SS')");
           params.put("todate",sf.format(resetToDateTimeStamp(toDate)));
       }
       if(status != null && "ALL".equalsIgnoreCase(status)){
           queryStrForRegCount
           .append(" and st.code in ('CREATED','APPROVED','REGISTERED','REJECTED','CANCELLED')");
       }else if(status != null){
           queryStrForRegCount
           .append(" and st.code=:status");
           params.put("status", status);
       }
       
       if(registrationUnit.getId() != null){
           queryStrForRegCount
           .append(" and  registrationunit=to_number(:regunitid,'999999')");
           params.put("regunitid", registrationUnit.getId().toString());
       }
       queryStrForRegCount.append(" group by regunit.name,st.code order by regunit.name desc");

       final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryStrForRegCount.toString());
       for (final String param : params.keySet())
           query.setParameter(param, params.get(param)); 
       return query.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationByStatusForReport(final String registrationUnit, final String status,
            final Date fromDate,final Date toDate)
            throws ParseException {

        final Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class, "marriageRegistration")
                .createAlias("marriageRegistration.status", "status");
        if (fromDate != null)
            criteria.add(Restrictions.ge("marriageRegistration.applicationDate", resetFromDateTimeStamp(fromDate)));
        if (fromDate != null)
            criteria.add(Restrictions.le("marriageRegistration.applicationDate",resetToDateTimeStamp(toDate)));
        if (registrationUnit != null )
            criteria.createAlias("marriageRegistration.marriageRegistrationUnit", "marriageRegistrationUnit").add(Restrictions.eq("marriageRegistrationUnit.name", registrationUnit.replaceAll("[^a-zA-Z0-9]", " ")));
        if (status != null && !"ALL".equalsIgnoreCase(status)){
            criteria.add(Restrictions.in("status.code", new String[] { status }));
        } else {
            criteria.add(Restrictions.in("status.code", new String[] {"CREATED","APPROVED","REGISTERED","REJECTED","CANCELLED"}));
        }
        return criteria.list();

    }
    
    public List<SearchResult> getUlbWiseReligionDetails(final SearchModel searchRequest) throws ParseException{
        
        final SearchResponse ulbWiseResponse = findAllReligionByUlbName(searchRequest,
                getQueryFilter(searchRequest));
        
        final List<SearchResult> responseDetailsList = new ArrayList<>();
        Terms ulbs = ulbWiseResponse.getAggregations().get("groupByUlbName");
        
        for (Terms.Bucket ulb : ulbs.getBuckets()) {
            long countOthers = 0;
            long total=0;
            SearchResult searchResult = new SearchResult();
            searchResult.setUlbName(ulb.getKeyAsString());     
            Terms religions = ulb.getAggregations().get("groupByReligion");
            for (Terms.Bucket religion : religions.getBuckets()) {
                if("Christian".equals(religion.getKeyAsString())) {
                    total=total+religion.getDocCount();
                    searchResult.setChristian(religion.getDocCount());
                }else if("Hindu".equals(religion.getKeyAsString())) {
                    total=total+religion.getDocCount();
                    searchResult.setHindu(religion.getDocCount());               
                }else if("Muslim".equals(religion.getKeyAsString())) {
                    total=total+religion.getDocCount();
                    searchResult.setMuslim(religion.getDocCount());
                }else {
                    total=total+religion.getDocCount();
                    countOthers = countOthers+religion.getDocCount();
                }
            }
            searchResult.setOthers(countOthers);
            searchResult.setTotal(total);
            responseDetailsList.add(searchResult);
        }
        return responseDetailsList;
    }
    public BoolQueryBuilder getQueryFilter(final SearchModel searchRequest) throws ParseException{
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery("ulbName", searchRequest.getUlbName()));
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        final Date fromDate = formatter.parse(searchRequest.getYear() + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(searchRequest.getYear() + "/" + 12 + "/" + 31);
        if (fromDate != null && toDate != null){
         boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("registrationDate")
                .from(fromDate)
                .to(toDate));
        }
        boolQuery = boolQuery.filter(QueryBuilders.matchQuery("applicationStatus", "Registered"));
        return boolQuery;
    }
    
    public List<MarriageRegistrationIndex> getSearchResultByBoolQuery(final BoolQueryBuilder boolQuery, final FieldSortBuilder sort) {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("marriageregistration").withQuery(boolQuery)
                .withSort(sort).build();
        return elasticsearchTemplate.queryForList(searchQuery, MarriageRegistrationIndex.class);
    }
    
    public SearchResponse findAllReligionByUlbName(SearchModel searchRequest,
                                                   BoolQueryBuilder query) {

        return elasticsearchTemplate.getClient().prepareSearch("marriageregistration")
                .setQuery(query).setSize(0)
                .addAggregation(getCountWithGrouping("groupByUlbName", "ulbName", 120)
                        .subAggregation(getCountWithGrouping("groupByReligion", "husbandReligion", 30)))
                .execute().actionGet();
    }
    
    public static AggregationBuilder<?> getCountWithGrouping(String aggregationName, String fieldName, int size){
        return AggregationBuilders.terms(aggregationName).field(fieldName).size(size);
} 
    
    public ResponseEntity<byte[]> generateReligionWiseReport(final int year,final List<SearchResult> searchResponse,
            final HttpSession session, final HttpServletRequest request) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        reportOutput = new ReportOutput();
        final String cityName = request.getSession().getAttribute("citymunicipalityname").toString();
        final String url = WebUtils.extractRequestDomainURL(request, false);
        final String cityLogo = url.concat(MarriageConstants.IMAGE_CONTEXT_PATH);
        reportOutput = generateReportOutputForReligionWiseReport(year,searchResponse, cityName, cityLogo);
           
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("content-disposition", "inline;filename=WorkOrderNotice.pdf");
        return new ResponseEntity<byte[]>(reportOutput.getReportOutputData(), headers, HttpStatus.CREATED);
    }

    
    public ReportOutput generateReportOutputForReligionWiseReport(final int year,final List<SearchResult> searchResponse,
            String cityName,
            String logoPath) {
        
        reportParams = new HashMap<>();
        reportParams.put("cityName", cityName);
        reportParams.put("logoPath", logoPath);
        reportParams.put("year", year);
        reportParams.put("remarks", "");
        reportParams.put("searchResponse",searchResponse);
        reportInput = new ReportRequest("printreligionwisereport", searchResponse, reportParams);
        return reportService.createReport(reportInput);
    }
    
}
