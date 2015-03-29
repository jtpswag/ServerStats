import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class Cli extends Thread {

	private File conf;
	private Console con;
	private EinstellungsParser einst;
	private URL url;

	public Cli(File conf){
		this.con = System.console();
		this.conf = conf;
	}

	/**
	 * Liest die Datei in eine ArrayList ein. In der ArrayList sind die einzelnen
	 * Zeilen als String[] Arrays. An erster Stelle der String[] Arrays steht das Anfangsdatum,
	 * an zweiter die Endzeit, an dritter die Differenz (also wie lange die Anfrage gedauert hat) 
	 * in Millisekunden.
	 * @return ArrayList<String[]> mit dem Inhalt der Datei
	 */
	private ArrayList<String[]> lesen(){
		if (this.conf == null){
			return null;
		}
		else {
			try {
				BufferedReader in = new BufferedReader(new FileReader(this.einst.getFilePrefix() + this.url.getHost() + ".csv"));
				String zeile = null;
				ArrayList<String[]> teile = new ArrayList<String[]>();
				while ((zeile = in.readLine()) != null) {
					teile.add(zeile.split(";"));
				}
				return teile;
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * Zählt die Zeilen in der CSV Datei
	 * @return die Anzahl der Anfragen
	 */
	private int anzAnfragen(){
		return this.lesen().size();
	}

	/**
	 * Zählt wie oft ein bestimmer HTTP-Status Code in der Datei steht.
	 * @param httpCode der zu zählende HTTP-Status Code
	 * @return wie oft httpCode vorkommt
	 */
	private int countCode(int httpCode){
		int anz = 0;
		String httpCodeS = Integer.toString(httpCode);
		for (String[] st : this.lesen()){
			if (httpCodeS.equals(st[3])){
				anz ++;
			}
		}
		return anz;
	}

	/**
	 * Gibt die Häufigkeit eines bestimmten HTTP-Status Codes in Prozent zurück.
	 * @param httpCode Der zu berechnende HTTP-Status Code
	 * @return die Häufigkeit in Prozent
	 */
	private float proz(int httpCode){
		return (this.countCode(httpCode)*100)/this.anzAnfragen();
	}

	private String help(){
		String ret = "Verfügbare Kommandos:\n";
		ret = ret + "ges:               Die Anzahl aller Abfragen\n";
		ret = ret + "count <HTTP-Code>: Wie oft ein bestimmter HTTP-Code zurückkam\n";
		ret = ret + "% <HTTP-Code>:     Die Häufigkeit eines bestimmten HTTP-Codes in Prozent\n";
		ret = ret + "help:              Diese Hilfe anzeigen";
		return ret;

	}

	private String rtfm(){
		String ret = "Fehler: Fehlender Parameter\n";
		ret = ret + "rtfm!";
		return ret;
	}

	private float durchSchnZeit(){
		int ges = 0;
		for (String[] st : this.lesen()){
			ges = ges + Integer.parseInt(st[2]);
		}
		return ges/this.anzAnfragen();
	}
	
	private int minZeit(){
		int zeit = this.maxZeit();
		for (String[] st : this.lesen()){
			if (Integer.parseInt(st[2]) < zeit){
				zeit = Integer.parseInt(st[2]);
			}
		}
		return zeit;
	}

	private int maxZeit(){
		int zeit = 0;
		for (String[] st : this.lesen()){
			if (Integer.parseInt(st[2]) > zeit){
				zeit = Integer.parseInt(st[2]);
			}
		}
		return zeit;
	}
	
	public void run(){
		//Für die Einstellungen schauen, ob ein File übergeben wurde, ansonsten das Standart File
		//nehmen
		if (this.conf != null){
			try {
				this.einst = new EinstellungsParser(this.conf);
				for (URL url : this.einst.getUrls()){
					new AnfrageThread(this.einst.getFilePrefix() + url.getHost() + ".csv", url).start();
				}
			} catch (FileNotFoundException e) {
				System.out.println("> Fehler: Die angegebene Konfigurationsdatei gibts nich.");
				System.exit(1);
			}
		}
		System.out.println("> Anfragen gestartet!");
		System.out.println("> help für Übersicht");
		System.out.print("> ");
		Scanner sc = new Scanner(con.reader());
		String cmd = null;
		while ((cmd = sc.nextLine()) != null){
			String[] cmds = cmd.split(" ");
			switch (cmds[0]){
			case "%": 
				if (cmds.length > 1){
					System.out.println(this.proz(Integer.parseInt(cmds[1])));
				} else {
					System.out.println(this.rtfm());
				}
				break;
			case "count":
				if (cmds.length > 1){
					System.out.println(this.countCode(Integer.parseInt(cmds[1])));
				} else {
					System.out.println(this.rtfm());
				}
				break;
			case "ges":
				System.out.println(this.anzAnfragen());
				break;
			case "help":
				System.out.println(this.help());
				break;
			case "time":
				if (cmds.length > 1){
					switch (cmds[1]){
					case "avg":
						System.out.println(this.durchSchnZeit());
						break;
					case "max":
						System.out.println(this.maxZeit());
						break;
					case "min":
						System.out.println(this.minZeit());
						break;
					default:
						System.out.println("Fehler: Unbekantes Argument");
						System.out.println("rtfm!");
					}
				} else {
					System.out.println(this.rtfm());
				}
				break;
			case "use":
				if (cmds.length > 1){
					try {
						this.url = new URL(cmds[1]);
						System.out.println("benutze " + cmds[1]);
					} catch (MalformedURLException e) {
						System.out.println("Fehler: Falsches URL-Format");
						e.printStackTrace();
					}
					
				} else {
					System.out.println(this.rtfm());
				}
				break;

			default:
				System.out.println("Unbekanntes Argument");
				System.out.println(this.help());
			}
			System.out.print("> ");
		}
	}
}
