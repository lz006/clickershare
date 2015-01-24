package de.hdm.clicker.server.db;

import java.sql.*;
import java.util.Vector;

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
	private static Vector<Connection> con = new Vector<Connection>();
	private static int i = 0;
	
	/**
	 * Die URL, mit deren Hilfe die Datenbank angesprochen wird.
	 */
	private static String url = "jdbc:google:rdbms://hdm-clicker:database-usa/hdm-clicker?user=root&";
	
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
	public static Connection connection() throws RuntimeException {
		try {
			if ( con.elementAt(i) == null || con.elementAt(i).isClosed() || con.elementAt(i).isValid(0)) {
				try {
					DriverManager.registerDriver(new AppEngineDriver());
					
					con.setElementAt(DriverManager.getConnection(url), i);
				} 
				catch (SQLException e1) {
					con.setElementAt(null, i);
					throw new RuntimeException("Datenbankbankproblem con1: " + e1.getMessage());
				}
			}
		}
		catch (SQLException e1) {
			con = null;
			throw new RuntimeException("Datenbankbankproblem con2: " + e1.getMessage());
		}
		Connection c;
		try {
			c = con.elementAt(i);
			i++;
			if (con.size() <= i) {
				i = 0;
			}
		} catch (Exception e) {
			throw new RuntimeException("Datenbankbankproblem con3: " + e.getMessage());
		}
		
		return c;
	}
	
	public static void openOneOfTenConnection() throws RuntimeException {
		try {
			if (con.size() < 10) {
				try {
					DriverManager.registerDriver(new AppEngineDriver());
					con.add(DriverManager.getConnection(url));
				} 
				catch (SQLException e1) {
					con = null;
					throw new RuntimeException("Datenbankbankproblem oootc: " + e1.getMessage());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Datenbankbankproblem oootc2: " + e.getMessage());
		}
	}
}
