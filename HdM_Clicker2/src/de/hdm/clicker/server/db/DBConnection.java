package de.hdm.clicker.server.db;

import java.sql.*;

import com.google.appengine.api.rdbms.AppEngineDriver;

/**
 * Verwalten einer Verbindung zur Datenbank.<p>
 * <b>Vorteil:</b> Sehr einfacher Verbindungsaufbau zur Datenbank.<p>
 * <b>Nachteil:</b> Durch die Singleton-Eigenschaft der Klasse kann nur auf eine
 * fest vorgegebene Datenbank zugegriffen werden.<p>
 * 
 * @author Thies
 */
public class DBConnection {

	/**
	 * Die Klasse DBConnection wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.<p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 * 
	 * @see BelegungMapper.belegungMapper()
	 * @see DozentMapper.dozentMapper()
	 * @see LehrveranstaltungMapper.LehrveranstaltungMapper()
	 * @see RaumMapper.raumtMapper()
	 * @see SemesterverbandMapper.semesterverbandMapper()
	 * @see StudiengangMapper.studiengangMapper()
	 * @see ZeitslotMapper.zeitslotMapper()
	 */
	private static Connection con = null;
	
	/**
	 * Die URL, mit deren Hilfe die Datenbank angesprochen wird.
	 */
	//private static String url = "jdbc:mysql://dd33022.kasserver.com:3306/d01c2319";
	private static String url = "jdbc:google:rdbms://hdm-clicker:database/clicker_database?user=root&";
	/*
	 *  Es kam in der Vergangenheit immer wieder vor, dass man die auskommentierte "URL" 
	 *  für den Hosted-Modus verwenden musste (...Erklärungen haben wir vergeblich gesucht)
	 */
	//"jdbc:google:rdbms://titanium-spider-370:stundenplantool/stundenplantooltest" + "," +  "u"  + "," + "stundenplan.tool";
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch 
	 * <code>DBConnection.connection()</code>. Sie stellt die 
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>DBConnection</code> existiert.<p>
	 * 
	 * <b>Fazit:</b> DBConnection sollte nicht mittels <code>new</code> 
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.<p>
	 * 
	 * <b>Nachteil:</b> Bei Zusammenbruch der Verbindung zur Datenbank - dies kann
	 * z.B. durch ein unbeabsichtigtes Herunterfahren der Datenbank ausgelöst 
	 * werden - wird keine neue Verbindung aufgebaut, so dass die in einem solchen
	 * Fall die gesamte Software neu zu starten ist. 
	 * 
	 * @return DAS <code>DBConncetion</code>-Objekt.
	 */
	public static Connection connection() {
		// Wenn es bisher keine Conncetion zur DB gab, ... 
		try {
			if ( con == null || con.isClosed() ) {
				try {
					// Ersteinmal muss der passende DB-Treiber geladen werden
					DriverManager.registerDriver(new AppEngineDriver());
					/*
					 * Dann erst kann uns der DriverManager eine Verbindung mit den oben
					 * in der Variable url angegebenen Verbindungsinformationen aufbauen.
					 * 
					 * Diese Verbindung wird dann in der statischen Variable con 
					 * abgespeichert und fortan verwendet.
					 */
					con = DriverManager.getConnection(url);
					//con = DriverManager.getConnection(url, "d01c2319", "hdm2014!XOXO");
				} 
				catch (SQLException e1) {
					con = null;
					e1.printStackTrace();
				}
			}
		}
		catch (SQLException e1) {
			con = null;
			e1.printStackTrace();
		}
		
		// Zurückgegeben der Verbindung
		return con;
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch 
	 * <code>DBConnection.closeConnection()</code>. Sie löst
	 * eine bestehende Verbindung zur Datenbank auf.
	 * 
	 * @throws	RuntimeException - beim "kappen" der DB-
	 * 			Verbindung kann ein Fehler entstehen,
	 * 			welcher mittelbar an die aufrufende Methode
	 * 			weitergeleitet wird
	 */
	public static void closeConnection() throws RuntimeException {
		try {
			if (con != null && (!con.isClosed())) {
				try {
					con.close();
					con = null;
				}
				catch (SQLException e1) {
					throw new RuntimeException("Fehler beim Trennen der DB-Verbindung aufgetreten: " + e1.getMessage());
				}
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Fehler beim Trennen der DB-Verbindung aufgetreten: " + e1.getMessage());
		}
	}
}
