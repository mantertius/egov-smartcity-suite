/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.works.web.controller.letterofacceptance;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.security.utils.SecurityUtils;
import org.egov.works.letterofacceptance.service.LetterOfAcceptanceNumberGenerator;
import org.egov.works.letterofacceptance.service.LetterOfAcceptanceService;
import org.egov.works.lineestimate.entity.LineEstimateDetails;
import org.egov.works.lineestimate.service.LineEstimateService;
import org.egov.works.models.workorder.WorkOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/letterofacceptance")
public class CreateLetterOfAcceptanceController {

    @Autowired
    private LineEstimateService lineEstimateService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private LetterOfAcceptanceService letterOfAcceptanceService;
    @Autowired
    private LetterOfAcceptanceNumberGenerator letterOfAcceptanceNumberGenerator;

    @RequestMapping(value = "/newform", method = RequestMethod.GET)
    public String showNewForm(@ModelAttribute("workOrder") final WorkOrder workOrder,
            final Model model, final HttpServletRequest request) {
        final String estimateNumber = request.getParameter("estimateNumber");
        final LineEstimateDetails lineEstimateDetails = lineEstimateService.findByEstimateNumber(estimateNumber);
        setDropDownValues(model, lineEstimateDetails);
        workOrder.setWorkOrderDate(new Date());
        model.addAttribute("lineEstimateDetails", lineEstimateDetails);
        model.addAttribute("workOrder", workOrder);
        model.addAttribute("loggedInUser", securityUtils.getCurrentUser().getName());
        return "createLetterOfAcceptance-form";
    }

    private void setDropDownValues(final Model model, final LineEstimateDetails lineEstimateDetails) {
        model.addAttribute("engineerInchargeList",
                letterOfAcceptanceService.getEngineerInchargeList(
                        lineEstimateDetails.getLineEstimate().getExecutingDepartment().getId(),
                        letterOfAcceptanceService.getEngineerInchargeDesignationId()));
    }

    @RequestMapping(value = "/loa-save", method = RequestMethod.POST)
    public String create(@ModelAttribute("workOrder") final WorkOrder workOrder,
            final Model model, final BindingResult resultBinder) {
        final LineEstimateDetails lineEstimateDetails = lineEstimateService.findByEstimateNumber(workOrder.getEstimateNumber());

        // TODO:Fixme - workOrder.getEstimateNumber() - Replace with WIN from line estimate
        workOrder.setWorkOrderNumber(
                letterOfAcceptanceNumberGenerator.generateLetterOfAcceptanceNumber(workOrder.getEstimateNumber()));

        if (resultBinder.hasErrors()) {
            setDropDownValues(model, lineEstimateDetails);
            return "createLetterOfAcceptance-form";
        } else {
            final WorkOrder savedWorkOrder = letterOfAcceptanceService.create(workOrder);
            return "redirect:/letterofacceptance/loa-success?loaNumber=" + savedWorkOrder.getWorkOrderNumber();
        }
    }

    @RequestMapping(value = "/loa-success", method = RequestMethod.GET)
    public String showLetterOfAcceptanceSuccessPage(@RequestParam("loaNumber") final String loaNumber, final Model model) {
        final WorkOrder workOrder = letterOfAcceptanceService.getWorkOrderByWorkOrderNumber(loaNumber);
        model.addAttribute("workOrder", workOrder);
        return "letterofacceptance-success";
    }

}