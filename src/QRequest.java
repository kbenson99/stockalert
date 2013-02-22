import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class QRequest {

	
	public static void main( String[] args) {
		String quote = "2/13 Watchlist: $ACW, $ACHN, $GGS, $IBCP, $MENT, $NAT, $STP, $ZGNX. http://vextrades.blogspot.com/ ";
		String delims = "[ ]+";
		String[] tokens = quote.split(delims);
		String value ="";

		for (String token : tokens)
		{			
			if (token.charAt(0) == '$' && quote.indexOf("$") != -1)
			{
				value = token.substring(1, token.length());
			}
			else if (quote.indexOf("$") == -1)
			{
				value = token;		
			}
			
			value = StringUtils.strip(value, "'$,.\"/;:[]*&^%#@)(!-_=+|<>?");
			Pattern p = Pattern.compile("[^a-zA-Z]");
			boolean hasSpecialChar = p.matcher(value).find();
			if (!hasSpecialChar)
			{
				if (value.length() > 0)
				{
					System.out.println( value);		
				}			
			}			
		}
		
		
	}
}
