import java.io.File;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.commons.io.FileUtils;

import com.benson.stockalert.model.QuoteRequest;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String url_text = "http://vegastrader66.blogspot.com/";
		try
		{
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet(url_text.toString());
//            // Get the response
//            ResponseHandler<String> responseHandler = new BasicResponseHandler();
//            String response_str = client.execute(request, responseHandler);
			String response_str = FileUtils.readFileToString(new File("../web.txt"));
            QuoteRequest req = new QuoteRequest();  //( response_str);
            req.setIsUrlData(true);
            req.setQuote(response_str);
            
            System.out.println(req.getQuote());
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}		// TODO Auto-generated method stub

	}

}
