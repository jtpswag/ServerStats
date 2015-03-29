import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

	public static void main(String[] args){
		if (args[0].equals("-ws")){
			if (args.length == 1) {
				System.out.println("> Fehler: Wo soll die Standartkonfiguration denn hingeschrieben werden?");
				System.exit(1);
			} else{
				System.out.print("Die Standartkonfiguration wird nach " + args[1] + " geschrieben...");
				String conf = "#Konfiguration für die Anfrage\n"
						+ "# #: Kommentar\n"
						+ "\n"
						+ "#zu ueberpruefende urls (nur http!)\n"
						+ "url=http://test.jugendtheaterprojekt.de/\n"
						+ "url=http://google.de/\n"
						+ "\n"
						+ "#Wie lange soll zwischen den Anfragen gewartet werden\n"
						+ "wait=30\n"
						+ "\n"
						+ "#Wo sollen die csv dateien hingeschrieben werden ($HOME ist das homeverzeichnis)\n"
						+ "ort=$HOME\n";
				try {
					BufferedWriter bf = new BufferedWriter(new FileWriter(args[1]));
					bf.write(conf);
					bf.flush();
					bf.close();
					System.out.print(" fertig!");
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
		if (args.length > 0){
			new Cli(new File(args[0])).start();
		} else {
			new Cli(new File("anfrage.conf")).start();
		}
	}

}
