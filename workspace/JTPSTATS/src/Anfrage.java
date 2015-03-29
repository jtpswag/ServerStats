import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;


public class Anfrage {
	private HttpURLConnection http;
	private URL url;
	private GregorianCalendar start;
	private GregorianCalendar ende;
	private long diff;
	private int httpCode;
	
	public Anfrage(URL url){
		
		try {
			this.url = url;
			this.http = (HttpURLConnection) this.url.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void ausfuehren() {
		//Startzeit
		this.start = new GregorianCalendar();
		try {
			this.http.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.http.disconnect();
		//Endzeit
		this.ende = new GregorianCalendar();
		try {
			//http Response Code
			this.httpCode = this.http.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Differenz
		this.diff = this.ende.getTimeInMillis()-this.start.getTimeInMillis();
	}
	
	public String toCSV(){
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:S");
		String ret = fmt.format(this.start.getTime()) + ";";
		ret = ret + fmt.format(this.ende.getTime()) + ";";
		ret = ret + this.diff + ";";
		ret = ret + this.httpCode;
		return ret;
		
	}
}
