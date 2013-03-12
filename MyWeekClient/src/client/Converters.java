/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.Calendar;
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
    	Calendar c = c = Calendar.getInstance();
    	c.set(year, (month-1), day, hour, minutes, 0);
    	
    	return c.getTimeInMillis();
    	
    }
}
