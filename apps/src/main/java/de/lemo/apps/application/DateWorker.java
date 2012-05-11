package de.lemo.apps.application;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public interface DateWorker {
	
	public Integer daysBetween(Calendar startDate, Calendar endDate);
	
	public Integer daysBetween(Date startDate, Date endDate);
	
	public String getLocalizedDate(Date date, Locale currentLocale);
	
	public String getLocalizedDateTime(Date date, Locale currentLocale);
	

}