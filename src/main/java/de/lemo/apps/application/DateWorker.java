package de.lemo.apps.application;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.tapestry5.json.JSONLiteral;

public interface DateWorker {

    public Integer daysBetween(Calendar startDate, Calendar endDate);

    public Integer daysBetween(Date startDate, Date endDate);

    public String getLocalizedDate(Date date, Locale locale);

    public String getLocalizedDateTime(Date date, Locale locale);

    public JSONLiteral getDatePickerParams(Locale locale);

}
