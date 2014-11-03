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
 * @see SemesterverbandMapper
 * @see ZeitslotMapper
 * @author Thies (implement: Zimmermann, Klatt, Roth)
 * @version 1.0
 */
public class StudiengangMapper {
	
	/**
	 * Die Klasse StudiengangMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static StudiengangMapper studiengangMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected StudiengangMapper(){
		
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>StudiengangMapper.studiengangMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>StudiengangMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> StudiengangMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>StudiengangMapper</code>-Objekt.
	 */
	public static StudiengangMapper studiengangMapper() {
	    if (studiengangMapper == null) {
	    	studiengangMapper = new StudiengangMapper();
	    }

	    return studiengangMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Studiengängen anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	keys - Primärschlüsselattribut(e) (->DB)
	 * 			loop - boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Studiengängen, die den Primärschlüsselattributen entsprechen
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Studiengang> findByKey(Vector<Integer> keys, Boolean loop) throws RuntimeException {
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
		Vector<Studiengang> studiengaenge = new Vector<Studiengang>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Studiengang WHERE ID IN (" + ids.toString() + ") ORDER BY Bezeichnung";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Studiengang-Vectors"
			while(rs.next()){
				Studiengang studiengang = new Studiengang();
				studiengang.setId(rs.getInt("ID"));
				studiengang.setBezeichnung(rs.getString("Bezeichnung"));
				studiengang.setKuerzel(rs.getString("Kuerzel"));
				studiengaenge.add(studiengang);  
	          }
			
			
			// Einfügen der zugehörigen Semesterverbände in die einzelenen Studiengänge des "Studiengang-Vectors"		
			if (loop == true) {
				for (int i = 0; i < studiengaenge.size(); i++) {
					sql = "SELECT ID FROM Semesterverband WHERE StudiengangID = "+ studiengaenge.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					SemesterverbandMapper svMapper = SemesterverbandMapper.semesterverbandMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("ID")); 
							
						}
					if (vi.size() > 0) {
						studiengaenge.elementAt(i).setSemesterverbaende(svMapper.findByKey(vi, false));
					}					
				}
			}
			
			// Einfügen der zugehörigen Lehrveranstaltungen in die einzelenen Studiengänge des "Studiengang-Vectors"		
			if (loop == true) {
				for (int i = 0; i < studiengaenge.size(); i++) {
					sql = "SELECT LehrveranstaltungID FROM Studiengangzuordnung_ZWT WHERE StudiengangID = "+ studiengaenge.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					LehrveranstaltungMapper lMapper = LehrveranstaltungMapper.lehrveranstaltungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("LehrveranstaltungID")); 
							
						}
					if (vi.size() > 0) {
						studiengaenge.elementAt(i).setLehrveranstaltungen(lMapper.findByKey(vi, false));
					}						
				}
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem sgm fbk: " + e1.getMessage());				
		}
		
		return studiengaenge;
	}
	
	/**
	 * Methode um alle Studiengänge aus der DB auszulesen
	 * 
	 * @param	loop - boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Studiengänge
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Studiengang> findAll(Boolean loop) throws RuntimeException {
        			
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Studiengang> studiengaenge = new Vector<Studiengang>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Studiengang ORDER BY Bezeichnung";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Studiengang-Vectors"
			while(rs.next()){
				Studiengang studiengang = new Studiengang();
				studiengang.setId(rs.getInt("ID"));
				studiengang.setBezeichnung(rs.getString("Bezeichnung"));
				studiengang.setKuerzel(rs.getString("Kuerzel"));
				studiengaenge.add(studiengang);  
	          }
			
			
			// Einfügen der zugehörigen Semesterverbände in die einzelenen Studiengänge des "Studiengang-Vectors"
			if (loop == true) {
				for (int i = 0; i < studiengaenge.size(); i++) {
					sql = "SELECT ID FROM Semesterverband WHERE StudiengangID = "+ studiengaenge.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					SemesterverbandMapper svMapper = SemesterverbandMapper.semesterverbandMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("ID")); 
							
						}
					if (vi.size() > 0) {
						studiengaenge.elementAt(i).setSemesterverbaende(svMapper.findByKey(vi, false));
					}					
				}
			}
			
			// Einfügen der zugehörigen Lehrveranstaltungen in die einzelenen Studiengänge des "Studiengang-Vectors"		
			if (loop == true) {
				for (int i = 0; i < studiengaenge.size(); i++) {
					sql = "SELECT LehrveranstaltungID FROM Studiengangzuordnung_ZWT WHERE StudiengangID = "+ studiengaenge.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					LehrveranstaltungMapper lMapper = LehrveranstaltungMapper.lehrveranstaltungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("LehrveranstaltungID")); 
						
						}
					if (vi.size() > 0) {
						studiengaenge.elementAt(i).setLehrveranstaltungen(lMapper.findByKey(vi, false));
					}						
				}
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem sgm fa: " + e1.getMessage());				
		}
		
		return studiengaenge;
	}
	
	/**
	 * Methode um eine Studiengang in der DB zu aktualisieren
	 * 
	 * @param	studiengang - Objekt welches aktualisiert werden soll 			
	 * @return	Studiengang-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Studiengang update(Studiengang studiengang) throws RuntimeException {
		
		//Einholen einer DB-Verbindung
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Dozent-Entität in der DB
		
		try{
			// Ausführen des SQL-Statements
			Statement stmt = con.createStatement();
			String sql = "UPDATE Studiengang SET Bezeichnung='"+studiengang.getBezeichnung()+"', Kuerzel='"+studiengang.getKuerzel()+"' WHERE ID="+studiengang.getId()+";";
			stmt.executeUpdate(sql);
			
			// Löschen der "Studiengangzuordnung" (die m zu n Beziehung zwischen Studiengang und Lehrveranstaltung)							
			sql = "DELETE FROM Studiengangzuordnung_ZWT WHERE StudiengangID = '"+studiengang.getId()+"';";
			stmt.executeUpdate(sql);
					
			// Aktualisierung der "Studiengangzuordnung" (die m zu n Beziehung zwischen Studiengang und Lehrveranstaltung)			
			if (studiengang.getLehrveranstaltungen() != null) {
				for (int i = 0; i < studiengang.getLehrveranstaltungen().size(); i++){
					sql = "INSERT INTO Studiengangzuordnung_ZWT (`StudiengangID`, `LehrveranstaltungID`) VALUES ('"+studiengang.getId()+"', '"+studiengang.getLehrveranstaltungen().elementAt(i).getId()+"');";
					stmt.executeUpdate(sql);
					}
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());		
		}
		
		return studiengang;
	}
	
	/**
	 * Methode um eine Studiengang aus der DB zu löschen
	 * 
	 * @param	studiengang - Objekt welches gelöscht werden soll
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public void delete(Studiengang studiengang) throws RuntimeException {
		/*
		 *  Ein Studiengang kann nur gelöscht werden, wenn er durch keine Lehrveranstaltungen und 
		 *  Semesterverbände mehr referenziert wird
		 */
		if (studiengang.getLehrveranstaltungen() == null && studiengang.getSemesterverbaende() == null ) {
			
			//Einholen einer DB-Verbindung
			Connection con = DBConnection.connection();
			try {
				// Ausführen des SQL-Statements
				Statement stmt = con.createStatement();
												
				String sql = "DELETE FROM Studiengang WHERE ID = '"+studiengang.getId()+"';";
				stmt.executeUpdate(sql);						
			}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
			}
		}
	}
	
	/**
	 * Methode um einen neuen Studiengang in die DB zu schreiben
	 * 
	 * @param	studiengang - Objekt welcher neu hinzukommt			
	 * @return	Studiengang-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Studiengang insertIntoDB(Studiengang studiengang) throws RuntimeException {
		
		//Einholen einer DB-Verbindung
		Connection con = DBConnection.connection();
		ResultSet rs;
						
		try{
			// Ausführen des SQL-Statements
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Studiengang (`Bezeichnung`, `Kuerzel`) VALUES ('"+studiengang.getBezeichnung()+"', '"+studiengang.getKuerzel()+"');";
			stmt.executeUpdate(sql);
			
			// Auslesen der nach einfügen eines neuen Dozenten in DB entstandenen "größten" ID 
			sql = "SELECT MAX(ID) AS maxid FROM Studiengang;";
			rs = stmt.executeQuery(sql);
			
			// Setzen der ID dem hier aktuellen Semesterverband-Objekt
			while(rs.next()){
				studiengang.setId(rs.getInt("maxid"));
			}
			
			if(studiengang.getLehrveranstaltungen() != null) {
				for ( int i = 0; i < studiengang.getLehrveranstaltungen().size(); i++) {
					sql = "INSERT INTO Studiengangzuordnung_ZWT (`StudiengangID`, `LehrveranstaltungID`) VALUES ('"+studiengang.getId()+"', '"+studiengang.getLehrveranstaltungen().elementAt(i).getId()+"');";
					stmt.executeUpdate(sql);
				}
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
		}
		
		return studiengang;
	}

}
