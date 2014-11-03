package de.hdm.clicker.server.db;

import java.sql.*;
import java.util.Vector;

import de.hdm.clicker.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Lehrveranstaltung</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see DozentMapper
 * @see LehrveranstaltungMapper
 * @see BelegungMapper
 * @see RaumMapper
 * @see SemesterverbandMapper
 * @see StudiengangMapper
 * @author Thies (implement: Zimmermann, Klatt, Roth)
 * @version 1.0
 */
public class ZeitslotMapper {

	/**
	 * Die Klasse ZeitslotMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static ZeitslotMapper zeitslotMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected ZeitslotMapper(){
		
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>ZeitslotMapper.zeitslotMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>ZeitslotMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> ZeitslotMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>ZeitslotMapper</code>-Objekt.
	 */
	public static ZeitslotMapper zeitslotMapper() {
	    if (zeitslotMapper == null) {
	    	zeitslotMapper = new ZeitslotMapper();
	    }

	    return zeitslotMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Zeitslots anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	keys - Primärschlüsselattribut(e) (->DB)
	 * @return	Vector mit Zeitslots, die den Primärschlüsselattributen entsprechen
	 */
	public Vector<Zeitslot> findByKey(Vector<Integer> keys) throws RuntimeException {
		StringBuffer ids = new StringBuffer();
		
		//Erstellung des dynamischen Teils des SQL-Querys		
		if (keys.size() > 1) {
			for (int i = 0; i < keys.size()-1; i++) {
				ids.append(keys.elementAt(i));	
				ids.append(",");
			}
		}			
		ids.append(keys.elementAt(keys.size()-1));	
		
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Zeitslot> zeitslots = new Vector<Zeitslot>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Zeitslot WHERE ID IN (" + ids.toString() + ")";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Zeitslot-Vectors"
			while(rs.next()){
				Zeitslot zeitslot = new Zeitslot();
				zeitslot.setId(rs.getInt("ID"));
				zeitslot.setAnfangszeit(rs.getInt("Anfangszeit"));
				zeitslot.setEndzeit(rs.getInt("Endzeit"));
				zeitslot.setWochentag(rs.getString("Wochentag"));
				zeitslots.add(zeitslot);  
	          }

		}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());				
			}
		return zeitslots;
	}
	
	/**
	 * Methode um alle Zeitslots aus der DB auszulesen
	 *
	 * @return	Vector mit Lehrveranstaltungen
	 */	
	public Vector<Zeitslot> findAll() throws RuntimeException {
				
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Zeitslot> zeitslots = new Vector<Zeitslot>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Zeitslot";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Zeitslots-Vectors"
			while(rs.next()){
				Zeitslot zeitslot = new Zeitslot();
				zeitslot.setId(rs.getInt("ID"));
				zeitslot.setAnfangszeit(rs.getInt("Anfangszeit"));
				zeitslot.setEndzeit(rs.getInt("Endzeit"));
				zeitslot.setWochentag(rs.getString("Wochentag"));
				zeitslots.add(zeitslot);  
	          }		
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());				
		}
		return zeitslots;
	}

}
