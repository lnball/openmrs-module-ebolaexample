package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.pharmacy.DoseHistory;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduledDoseHistoryFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @SpringBean PharmacyService pharmacyService,
                           @FragmentParam(value = "toDate", required = false) Date toDate,
                           FragmentModel model) {
        Date fromDate;
        if (toDate == null) {
            toDate = new Date();
        }
        toDate = DateUtil.getEndOfDay(toDate);
        fromDate = DateUtil.getStartOfDay(DateUtil.adjustDate(toDate, -6, DurationUnit.DAYS));
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        List<Date> dates = new ArrayList<Date>();
        for (int i = 0; DateUtil.adjustDate(fromDate, i, DurationUnit.DAYS).before(toDate); ++i) {
            dates.add(DateUtil.adjustDate(fromDate, i, DurationUnit.DAYS));
        }
        model.addAttribute("dates", dates);

        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        Date prevDate = DateUtil.getStartOfDay(DateUtil.adjustDate(toDate, -7, DurationUnit.DAYS));
        Date nextDate = DateUtil.getStartOfDay(DateUtil.adjustDate(toDate, 7, DurationUnit.DAYS));
        if (nextDate.after(new Date())) {
            // no navigating into the future
            nextDate = null;
        }

        model.addAttribute("prevDate", ymd.format(prevDate));
        model.addAttribute("nextDate", nextDate == null ? null : ymd.format(nextDate));

        DoseHistory doseHistory = pharmacyService.getScheduledDosesByPatientAndDateRange(patient.getPatient(), fromDate, toDate, false);
        model.addAttribute("doseHistory", doseHistory);
    }

}
