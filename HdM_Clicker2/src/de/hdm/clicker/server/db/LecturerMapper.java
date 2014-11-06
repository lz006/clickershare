package de.hdm.clicker.server.db;


import java.sql.*;
import java.util.Vector;

import de.hdm.clicker.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Lecturer</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see QuizMapper
 * @see CategoryMapper
 * @see QuestionMapper
 * @see ResultsMapper
 * 
 * @author Zimmermann, Roth, Zanella
 * @version 1.0
 */
public class LecturerMapper {
	
	/**
	 * Die Klasse LecturerMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static LecturerMapper lecturerMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected LecturerMapper(){
		
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>LecturerMapper.lecturerMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>LecturerMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> LecturerMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>LecturerMapper</code>-Objekt.
	 */
	public static LecturerMapper lecturerMapper() {
	    if (lecturerMapper == null) {
	      lecturerMapper = new LecturerMapper();
	    }

	    return lecturerMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Lecturer anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	keys - Primärschlüsselattribut(e) (->DB)
	 * @return	Vector mit Lecturer, die den Primärschlüsselattributen entsprechen
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Lecturer> findByKey(Vector<Integer> keys) throws RuntimeException {
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
		Vector<Lecturer> lecturers = new Vector<Lecturer>();
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Lecturer WHERE ID IN (" + ids.toString() + ") ORDER BY name";
			rs = stmt.executeQuery(sql);
			
			// Erstellung des "Lecturer-Vectors"
			while(rs.next()){
				Lecturer lecturer = new Lecturer();
				lecturer.setId(rs.getInt("ID"));
				lecturer.setUser(rs.getString("user"));
				lecturer.setPassword(rs.getString("password"));
				lecturer.setFirstName(rs.getString("firstname"));
				lecturer.setName(rs.getString("name"));
				lecturers.add(lecturer);  
	          }
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - dm fbk: " + e1.getMessage());				
		}
		
		return lecturers;
	}
	
	/**
	 * Methode um einen Lecturer in der DB zu aktualisieren
	 * 
	 * @param	lecturer - Objekt welches aktualisiert werden soll 			
	 * @return	Lecturer-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Lecturer update(Lecturer lecturer) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Dozent-Entität in der DB		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "UPDATE Lecturer SET user='"+lecturer.getUser()+"', password='"+lecturer.getPassword()+"', firstname='"+lecturer.getFirstName()+"', name='"+lecturer.getName()+"' WHERE ID="+lecturer.getId()+";";
			stmt.executeUpdate(sql);
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - lm.update: " + e1.getMessage());
		}
		
		return lecturer;
	}
	
	/**
	 * Methode um einen neuen Lecturer in die DB zu schreiben
	 * 
	 * @param	dozent - Objekt welcher neu hinzukommt			
	 * @return	Dozent-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Lecturer insertIntoDB(Lecturer lecturer) throws RuntimeException {
		Connection con = DBConnection.connection();
		ResultSet rs;
						
		try{
			// Ausführung des SQL-Querys	
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Lecturer (`user`, `password`, `firstname`, `name`) VALUES ('"+lecturer.getUser()+"', '"+lecturer.getPassword()+"', '"+lecturer.getFirstName()+"', '"+lecturer.getName()+"');";
			stmt.executeUpdate(sql);
			
			// Auslesen der nach einfügen eines neuen Lecturers in DB entstandenen "größten" ID
			sql = "SELECT MAX(ID) AS maxid FROM Lecturer;";
			rs = stmt.executeQuery(sql);
			
			// Setzen der ID dem hier aktuellen Semesterverband-Objekt
			while(rs.next()){
				lecturer.setId(rs.getInt("maxid"));
			}
					
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
		}
		
		return lecturer;
	}
	
	/**
	 * Methode um alle Lecturer aus der DB auszulesen
	 * 
	 * @return	Vector mit Lecturer
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Lecturer> findAll() throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Lecturer> lecturers = new Vector<Lecturer>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Lecturer ORDER BY name;";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				Lecturer lecturer = new Lecturer();
				lecturer.setId(rs.getInt("ID"));
				lecturer.setUser(rs.getString("user"));
				lecturer.setPassword(rs.getString("password"));
				lecturer.setFirstName(rs.getString("firstname"));
				lecturer.setName(rs.getString("name"));
				lecturers.add(lecturer);   
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - dm fa: " + e1.getMessage());		
		}
		
				
		return lecturers;
			
	}
	
	/**
	 * Methode um einen Lecturer aus der DB zu löschen
	 * 
	 * @param	lecturer - Objekt welches gelöscht werden soll
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public void delete(Lecturer lecturer) throws RuntimeException {
		
		/* Das Löschen eines Lecturers hat das Löschen aller mit ihm
		 * in verbindungstehender Categories und Quizzes zur Folge
		 */
		
		
		Connection con = DBConnection.connection();
		try {
			Statement stmt = con.createStatement();
			
			// Löschen der Categories
			
			// Löschen der Quizze
			
			// Löschen Löschen der Dozent-Entität
			String sql = "DELETE FROM Lecturer WHERE ID = '"+lecturer.getId()+"';";
			stmt.executeUpdate(sql);
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - lm.delete: " + e1.getMessage());
		}
		
	}
	

}