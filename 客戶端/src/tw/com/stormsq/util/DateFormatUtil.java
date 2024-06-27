package tw.com.stormsq.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	
	public static String getDateTime(Date datetime)
	{
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		return df.format(datetime);
	}
}
