import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;


public class AnfrageThread extends Thread {
	
	private File datei;
	private URL url;
	
	public AnfrageThread(String datei, URL url){
		this.datei = new File(datei);
		this.url = url;
	}
	
	public void run(){
		while (true){
			Anfrage anf = new Anfrage(this.url);
			anf.ausfuehren();
			//in datei schreiben
			BufferedWriter bw = null;
			try {
			    bw = new BufferedWriter(new FileWriter(this.datei, true));
			    bw.write(anf.toCSV());
			    bw.newLine();
			    bw.flush();
			} catch (IOException ioe) {
			    ioe.printStackTrace();
			} finally { // always close the file
			    if (bw != null) {
			        try {
			            bw.close();
			        } catch (IOException ioe2) {
			            // just ignore it
			        }
			    }
			}
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
