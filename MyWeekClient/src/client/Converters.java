/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Laxcor
 */
public class Converters {
    /**
     * Converts milliseconds to a String HH:MM
     * @param millis
     * @return 
     */
	public static String MillisecondsToHHMM(long millis) {
		String hhmmss = String.format("%02d:%02d", 
		TimeUnit.MILLISECONDS.toHours(millis),
		TimeUnit.MILLISECONDS.toMinutes(millis) - 
		TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
		return hhmmss;
	}
    
    public long dateAndTimeToMilliseconds(int year, int month, int day, int hour, int minutes){
    	Calendar c = Calendar.getInstance();
    	c.set(year, (month-1), day, hour, minutes, 0);
    	
    	return c.getTimeInMillis(); 
    	
    }
    public static String dayStringFormat(long msecs) {
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(new Date(msecs));

        int dow = cal.get(Calendar.DAY_OF_WEEK);

        switch (dow) {
        case Calendar.MONDAY:
            return "Monday";
        case Calendar.TUESDAY:
            return "Tuesday";
        case Calendar.WEDNESDAY:
            return "Wednesday";
        case Calendar.THURSDAY:
            return "Thursday";
        case Calendar.FRIDAY:
            return "Friday";
        case Calendar.SATURDAY:
            return "Saturday";
        case Calendar.SUNDAY:
            return "Sunday";
        }
    return "Unknown";
  }
}
