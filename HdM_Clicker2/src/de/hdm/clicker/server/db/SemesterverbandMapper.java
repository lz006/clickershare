package de.hdm.clicker.server.db;

import java.sql.*;
import java.util.Vector;

import de.hdm.clicker.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Semesterverband</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see DozentMapper
 * @see LehrveranstaltungMapper
 * @see BelegungMapper
 * @see RaumMapper
 * @see StudiengangMapper
 * @see ZeitslotMapper
 * 
 * @author Thies (implement: Zimmermann, Klatt, Roth)
 * @version 1.0
 */
public class SemesterverbandMapper {
	
	/**
	 * Die Klasse SemesterverbandMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static SemesterverbandMapper semesterverbandMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected SemesterverbandMapper(){
		
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>SemesterverbandMapper.semesterverbandMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>SemesterverbandMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> SemesterverbandMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>SemesterverbandMapper</code>-Objekt.
	 */
	public static SemesterverbandMapper semesterverbandMapper() {
	    if (semesterverbandMapper == null) {
	    	semesterverbandMapper = new SemesterverbandMapper();
	    }

	    return semesterverbandMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Semesterverbänden anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	keys - Primärschlüsselattribut(e) (->DB)
	 * 			loop - boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Semesterverbänden, die den Primärschlüsselattributen entsprechen
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Semesterverband> findByKey(Vector<Integer> keys, Boolean loop) throws RuntimeException {
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
		Vector<Semesterverband> semesterverbaende = new Vector<Semesterverband>();
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Semesterverband WHERE ID IN (" + ids.toString() + ") ORDER BY Jahrgang";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Semesterverband-Vectors"
			while(rs.next()){
				Semesterverband semesterverband = new Semesterverband();
				
				StudiengangMapper sMapper = StudiengangMapper.studiengangMapper();
				Vector<Integer> vI = new Vector<Integer>();
				
				semesterverband.setId(rs.getInt("ID"));
				semesterverband.setAnzahlStudenten(rs.getInt("AnzahlStudenten"));
				semesterverband.setJahrgang(rs.getString("Jahrgang"));
				vI.add(rs.getInt("StudiengangID"));
				semesterverband.setStudiengang(sMapper.findByKey(vI, false).elementAt(0));
				semesterverbaende.add(semesterverband);  
	          }
			
			
			// Einfügen der zugehörigen Belegungen in die Semesterverbände des "Semesterverband-Vectors"			
			if (loop == true) {
				for (int i = 0; i < semesterverbaende.size(); i++) {
					sql = "SELECT BelegungID FROM Semesterverbandszugehörigkeit_ZWT WHERE SemesterverbandID = "+ semesterverbaende.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					BelegungMapper bMapper = BelegungMapper.belegungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("BelegungID")); 
							
					}
					if (vi.size() > 0) {
						semesterverbaende.elementAt(i).setBelegungen(bMapper.findByKey(vi, false));
					}
				}
			}
		
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());				
		}		
		return semesterverbaende;
	}
	
	/**
	 * Methode um alle Semesterverbände anhand eines Studiengang-Objekts aus der DB auszulesen
	 * 
	 * @param	sg - Studiengang-Objekt aufgrund dessen die Semesterverbände ausgelesen werden sollen
	 * 			loop - boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken) 			
	 * @return	Vector mit Semesterverbänden
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Semesterverband> findByStudiengang(Studiengang sg, Boolean loop) throws RuntimeException {
					
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Semesterverband> semesterverbaende = new Vector<Semesterverband>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Semesterverband WHERE StudiengangID = " + sg.getId();
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Semesterverband-Vectors"
			while(rs.next()){
				Semesterverband semesterverband = new Semesterverband();
				
				
				
				StudiengangMapper sMapper = StudiengangMapper.studiengangMapper();
				Vector<Integer> vI = new Vector<Integer>();
				
				semesterverband.setId(rs.getInt("ID"));
				semesterverband.setAnzahlStudenten(rs.getInt("AnzahlStudenten"));
				semesterverband.setJahrgang(rs.getString("Jahrgang"));
				vI.add(rs.getInt("StudiengangID"));
				semesterverband.setStudiengang(sMapper.findByKey(vI, false).elementAt(0));
				semesterverbaende.add(semesterverband);  
	          }
			
			
			// Einfügen der zugehörigen Belegungen in die Semesterverbände des "Semesterverband-Vectors"
			
			if (loop == true) {
				for (int i = 0; i < semesterverbaende.size(); i++) {
					sql = "SELECT BelegungID FROM Semesterverbandszugehörigkeit_ZWT WHERE SemesterverbandID = "+ semesterverbaende.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					BelegungMapper bMapper = BelegungMapper.belegungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("BelegungID")); 
							
						}
					if (vi.size() > 0) {
						semesterverbaende.elementAt(i).setBelegungen(bMapper.findByKey(vi, false));
					}
				}
			}
		
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());				
		}
		
		return semesterverbaende;
	}
	
	/**
	 * Methode um alle Semesterverbände aus der DB auszulesen
	 * 
	 * @param	loop - boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Semesterverbände
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Semesterverband> findAll(Boolean loop)  throws RuntimeException {
		
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Semesterverband> semesterverbaende = new Vector<Semesterverband>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Semesterverband ORDER BY Jahrgang";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Semesterverband-Vectors"
			while(rs.next()){
				Semesterverband semesterverband = new Semesterverband();
				
				StudiengangMapper sMapper = StudiengangMapper.studiengangMapper();
				Vector<Integer> vI = new Vector<Integer>();
				
				semesterverband.setId(rs.getInt("ID"));
				semesterverband.setAnzahlStudenten(rs.getInt("AnzahlStudenten"));
				semesterverband.setJahrgang(rs.getString("Jahrgang"));
				vI.add(rs.getInt("StudiengangID"));
				semesterverband.setStudiengang(sMapper.findByKey(vI, false).elementAt(0));
				semesterverbaende.add(semesterverband);  
	          }
			
			
			// Einfügen der zugehörigen Belegungen in die Semesterverbände des "Semesterverband-Vectors"			
			if (loop == true) {
				for (int i = 0; i < semesterverbaende.size(); i++) {
					sql = "SELECT BelegungID FROM Semesterverbandszugehörigkeit_ZWT WHERE SemesterverbandID = "+ semesterverbaende.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					BelegungMapper bMapper = BelegungMapper.belegungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("BelegungID")); 
							
						}
					if (vi.size() > 0) {
						semesterverbaende.elementAt(i).setBelegungen(bMapper.findByKey(vi, false));
					}					
				}
					
			}
					
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());				
		}		
		return semesterverbaende;		
	}
	
	/**
	 * Methode um eine Semesterverband in der DB zu aktualisieren
	 * 
	 * @param	semesterverband - Objekt welches aktualisiert werden soll 			
	 * @return	Semesterverband-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Semesterverband update(Semesterverband semesterverband) throws RuntimeException {
		
		// Einholen einer DB-Verbindung
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Semesterverband-Entität in der DB
		
		try{
			// Ausführen des SQL-Statements
			Statement stmt = con.createStatement();
			String sql = "UPDATE Semesterverband SET AnzahlStudenten='"+semesterverband.getAnzahlStudenten()+"', Jahrgang='"+semesterverband.getJahrgang()+"', StudiengangID='"+semesterverband.getStudiengang().getId()+"' WHERE ID="+semesterverband.getId()+";";
			stmt.executeUpdate(sql);
			
			// Löschen der "Semesterverbandszugehörigkeit" (die m zu n Beziehung zwischen Semesterverband und Belegungen)			
			sql = "DELETE FROM Semesterverbandszugehörigkeit_ZWT WHERE SemesterverbandID = '"+semesterverband.getId()+"';";
			stmt.executeUpdate(sql);
			
			// Aktualisierung der "Semesterverbandszugehörigkeit" (die m zu n Beziehung zwischen Dozent und Belegungen)			
			if (semesterverband.getBelegungen() != null) {
				for (int i = 0; i < semesterverband.getBelegungen().size(); i++){
					sql = "INSERT INTO Semesterverbandszugehörigkeit_ZWT (`SemesterverbandID`, `BelegungID`) VALUES ('"+semesterverband.getId()+"', '"+semesterverband.getBelegungen().elementAt(i).getId()+"');";
					stmt.executeUpdate(sql);
					}
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());			
		}
		
		return semesterverband;
	}
	
	/**
	 * Methode um einen Semesterverband aus der DB zu löschen
	 * 
	 * @param	semesterverband - Objekt welches gelöscht werden soll
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public void delete(Semesterverband semesterverband) throws RuntimeException {
		
		// Einholen einer DB-Verbindung
		Connection con = DBConnection.connection();
		try {
			// Ausführen des SQL-Statements
			Statement stmt = con.createStatement();
			
			// Löschen des Semesterverbands-Entität
			String sql = "DELETE FROM Semesterverband WHERE ID = '"+semesterverband.getId()+"';";
			stmt.executeUpdate(sql);											
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
		}		
	}
	
	/**
	 * Methode um eine neuen Semesterverband in die DB zu schreiben
	 * 
	 * @param	semesterverband - Objekt welcher neu hinzukommt			
	 * @return	Semesterverband-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Semesterverband insertIntoDB(Semesterverband semesterverband) throws RuntimeException {
		
		// Einholen einer DB-Verbindung
		Connection con = DBConnection.connection();
		ResultSet rs;
		
		try{
			// Ausführen des SQL-Statements
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Semesterverband (`AnzahlStudenten`, `Jahrgang`, `StudiengangID`) VALUES ('"+semesterverband.getAnzahlStudenten()+"', '"+semesterverband.getJahrgang()+"', '"+semesterverband.getStudiengang().getId()+"');";
			stmt.executeUpdate(sql);
			
			// Auslesen der nach einfügen eines neuen Semesterverbandes in DB entstandenen "größten" ID
			sql = "SELECT MAX(ID) AS maxid FROM Semesterverband;";
			rs = stmt.executeQuery(sql);
			
			// Setzen der ID dem hier aktuellen Semesterverband-Objekt
			while(rs.next()){
				semesterverband.setId(rs.getInt("maxid"));
			}
			
			// Setzen der Semesterverbandszugehörigkeit (die m zu n Beziehung zwischen Semesterverband und Belegung)
			if(semesterverband.getBelegungen() != null) {
				for ( int i = 0; i < semesterverband.getBelegungen().size(); i++) {
					sql = "INSERT INTO Semesterverbandszugehörigkeit_ZWT (`SemesterverbandID`, `BelegungID`) VALUES ('"+semesterverband.getId()+"', '"+semesterverband.getBelegungen().elementAt(i).getId()+"');";
					stmt.executeUpdate(sql);
				}
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
		}
		
		return semesterverband;
	}
}
