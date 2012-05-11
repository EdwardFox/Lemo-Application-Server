/**
 * 
 */
package de.lemo.apps.application;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * @author johndoe
 *
 */
public class DateWorkerImpl implements DateWorker {
	

	public Integer daysBetween(Calendar startDate, Calendar endDate) {
  	  Calendar date = (Calendar) startDate.clone();
  	  Integer daysBetween = 0;
  	  while (date.before(endDate)) {
  	    date.add(Calendar.DAY_OF_MONTH, 1);
  	    daysBetween++;
  	  }
  	  return daysBetween;
	}
	
	
	public Integer daysBetween(Date startDate, Date endDate) {
	  	  
		Calendar _startDate = Calendar.getInstance();
		Calendar _endDate = (Calendar) _startDate.clone(); 
		_endDate.setTime(endDate);
		_startDate.setTime(startDate);
	  	Integer daysBetween = 0;
	  	while (_startDate.before(_endDate)) {
	  	    _startDate.add(Calendar.DAY_OF_MONTH, 1);
	  	    daysBetween++;
	  	}
	  	return daysBetween;
	}
	
	/**
	 * Gibt das Datum in der aktuell vom Nutzer gewaehlten Locale Einstellung aus.
	 * @param inputDate
	 * @return Ein Objekt vom Typ String
	 */
	public String getLocalizedDate(Date date, Locale currentLocale){
		SimpleDateFormat df_date = new SimpleDateFormat( "MMM dd, yyyy", currentLocale );
		return df_date.format(date);
	}
	
	/**
	 * Gibt das Datum und die Uhrzeit in der aktuell vom Nutzer gewaehlten Locale Einstellung aus.
	 * @param inputDate
	 * @return Ein Objekt vom Typ String
	 */
	public String getLocalizedDateTime(Date date, Locale currentLocale){
		SimpleDateFormat df_date = new SimpleDateFormat( "MMM dd, yyyy (hh:mm)", currentLocale );
		return df_date.format(date);
	}

}