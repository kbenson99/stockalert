import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;


public class Check {

	
	public static void main( String[] args) {
		
		StringBuffer sb = new StringBuffer();
		sb.append( "t");
		sb.append( "s");
		

		
		Calendar cal = Calendar.getInstance();
		
		if (cal.getTimeZone().getDisplayName().equals( "GMT+00:00") ) {
			//TimeZone z = cal.getTimeZone();
			int offset = -14400000;
			int offsetHrs = offset / 1000 / 60 / 60;
			cal.add(Calendar.HOUR_OF_DAY, (offsetHrs));			
		}


		int day = cal.get(Calendar.DAY_OF_WEEK);
		int hour = cal.get(Calendar.HOUR_OF_DAY);

				
		System.out.println( "Cal.SATURDAY = " +cal.SATURDAY);
		System.out.println( "Cal.SUNDAY = " +cal.SUNDAY);
		System.out.println( "day = " +day);
		System.out.println( "hour = " +hour);
		
		
	}
}
