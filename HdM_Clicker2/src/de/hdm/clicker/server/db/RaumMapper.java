package de.hdm.clicker.server.db;

import java.sql.*;
import java.util.Vector;

import de.hdm.clicker.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Raum</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see DozentMapper
 * @see LehrveranstaltungMapper
 * @see BelegungMapper
 * @see SemesterverbandMapper
 * @see StudiengangMapper
 * @see ZeitslotMapper
 * 
 * @author Thies (implement: Zimmermann, Klatt, Roth)
 * @version 1.0
 */
public class RaumMapper {

	/**
	 * Die Klasse RaumMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static RaumMapper raumMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected RaumMapper(){
		
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>RaumMapper.raumMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>RaumMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> RaumMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>RaumMapper</code>-Objekt.
	 */
	public static RaumMapper raumMapper() {
	    if (raumMapper == null) {
	      raumMapper = new RaumMapper();
	    }

	    return raumMapper;
	   }
	
	/**
	 * Methode um einen Raum in der DB zu aktualisieren
	 * 
	 * @param	raum - Objekt welches aktualisiert werden soll 			
	 * @return	Raum-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Raum update(Raum raum) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Raum-Entität in der DB		
		try{
			Statement stmt = con.createStatement();
			
			// Ausführung des SQL-Statements
			String sql = "UPDATE Raum SET Kapazitaet='"+raum.getKapazitaet()+"', Bezeichnung='"+raum.getBezeichnung()+"' WHERE ID="+raum.getId()+";";
			stmt.executeUpdate(sql);
		}			
		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());	
		}
		
		return raum;
	}

	/**
	 * Methode um einen Raum in der DB zu aktualisieren
	 * 
	 * @param	raum - Objekt welches aktualisiert werden soll 			
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public void delete(Raum raum) throws RuntimeException {
			
		Connection con = DBConnection.connection();
		try {
			Statement stmt = con.createStatement();
			
			// Ausführung des SQL-Statements
			String sql = "DELETE FROM Raum WHERE ID = '"+raum.getId()+"';";
			stmt.executeUpdate(sql);
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
		}
	}
	
	/**
	 * Methode um einen neuen Raum in die DB zu schreiben
	 * 
	 * @param	raum - Objekt welcher neu hinzukommt			
	 * @return	Raum-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Raum insertIntoDB (Raum raum) throws RuntimeException {
		
		Connection con = DBConnection.connection();
		ResultSet rs;
		
		try{
			// Ausführen des SQL-Statements
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Raum (`ID`, `Kapazitaet`, `Bezeichnung`) VALUES ('" + raum.getId() + "', '" + raum.getKapazitaet() + "', '"+raum.getBezeichnung()+"');";
			stmt.executeUpdate(sql);
			
			// Auslesen der nach einfügen eines neuen Dozenten in DB entstandenen "größten" ID
			sql = "SELECT MAX(ID) AS maxid FROM Raum;";
			rs = stmt.executeQuery(sql);
			
			// Setzen der ID dem hier aktuellen Semesterverband-Objekt
			while(rs.next()){
				raum.setId(rs.getInt("maxid"));
			}		
		}		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
		}
		
		return raum;
		
	}
	
	/**
	 * Methode um eine beliebige Anzahl an Räumen anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	keys - Primärschlüsselattribut(e) (->DB)
	 * @return	Vector mit Räumen, die den Primärschlüsselattributen entsprechen
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Raum> findByKey(Vector<Integer> keys) throws RuntimeException {
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
		Vector<Raum> raeume = new Vector<Raum>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Raum WHERE ID IN (" + ids.toString() + ") ORDER BY Bezeichnung";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Raum-Vectors"
			while(rs.next()){
	            Raum raum = new Raum();
	            raum.setId(rs.getInt("ID"));
	            raum.setKapazitaet(rs.getInt("Kapazitaet"));
	            raum.setBezeichnung(rs.getString("Bezeichnung"));
	            raeume.add(raum);  
	          }
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());				
		}
		return raeume;
	}
	
	/**
	 * Methode um alle Räume aus der DB auszulesen
	 * 
	 * @return	Vector mit Räume
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Raum> findAll() throws RuntimeException {
		
		//Einholen einer DB-Verbindung				
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Raum> raeume = new Vector<Raum>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Raum ORDER BY Bezeichnung";
			rs = stmt.executeQuery(sql);
			
			// Erstellung des "Raum-Vectors"
			while(rs.next()){
	            Raum raum = new Raum();
	            raum.setId(rs.getInt("ID"));
	            raum.setKapazitaet(rs.getInt("Kapazitaet"));
	            raum.setBezeichnung(rs.getString("Bezeichnung"));
	            raeume.add(raum);  
	          }
			
			}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());				
			}
		return raeume;
	}
	
}
