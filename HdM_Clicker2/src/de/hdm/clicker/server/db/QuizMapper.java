package de.hdm.clicker.server.db;


import java.sql.*;
import java.util.Vector;

import de.hdm.clicker.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Quiz</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see LecturerMapper
 * @see CategoryMapper
 * @see QuestionMapper
 * @see ResultsMapper
 * 
 * @author Zimmermann, Roth, Zanella
 * @version 1.0
 */
public class QuizMapper {
	
	/**
	 * Die Klasse QuizMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static QuizMapper QuizMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected QuizMapper(){
		
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>QuizMapper.quizMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>QuizMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> QuizMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>QuizMapper</code>-Objekt.
	 */
	public static QuizMapper quizMapper() {
	    if (QuizMapper == null) {
	    	QuizMapper = new QuizMapper();
	    }

	    return QuizMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Quizzes in den jeweils jüngsten
	 * Versionen anhand Ihrerer ID's aus der DB auszulesen
	 * 
	 * @param	keys - Primärschlüsselattribut(e) (->DB)
	 * @return	Vector mit Quizzes, die den Primärschlüsselattributen entsprechen
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Quiz> findByKeyHV(Vector<Integer> keys) throws RuntimeException {
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
		Vector<Quiz> quizzes = new Vector<Quiz>();
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Quiz WHERE (id, version) IN (SELECT id, max(version) "
					+ "FROM Quiz Group By id) AND id IN (" + ids.toString() + ") ORDER BY description";
			rs = stmt.executeQuery(sql);
			
			// Erstellung des "Lecturer-Vectors"
			while(rs.next()){
				Quiz quiz = new Quiz();
				quiz.setId(rs.getInt("id"));
				quiz.setPassword(rs.getString("password"));
				quiz.setDurationButton(rs.getInt("buttonduration"));
				quiz.setVersion(rs.getInt("version"));
				quiz.setDescription(rs.getString("description"));
				quiz.setLecturerID(rs.getInt("lecturerid"));
				quiz.setDurationQuestion(rs.getInt("questionduration"));
				quiz.setNeed2SS(rs.getInt("solvingnumber"));
				quizzes.add(quiz);  
	          }
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fbk: " + e1.getMessage());				
		}
		
		return quizzes;
	}
	
	/**
	 * Methode um ein Quiz in der DB zu aktualisieren
	 * 
	 * @param	quiz - Objekt welches aktualisiert werden soll 			
	 * @return	Quiz-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Quiz update(Quiz quiz) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Dozent-Entität in der DB		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "UPDATE Quiz SET password='"+quiz.getPassword()+"', buttonduration='"+quiz.getDurationButton()
					+"', version='"+quiz.getVersion()+"', description='"+quiz.getDescription()+"', lecturerid='"+quiz.getLecturerID()
					+"', questionduration='"+quiz.getDurationQuestion()+"', solvingnumber='"+quiz.getNeed2SS()
					+"' WHERE id="+quiz.getId()+" AND version="+quiz.getVersion()+";";
			stmt.executeUpdate(sql);
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm.update: " + e1.getMessage());
		}
		
		return quiz;
	}
	
	/**
	 * Methode um ein neues Quiz in die DB zu schreiben
	 * 
	 * @param	quiz - Objekt welches neu hinzukommt			
	 * @return	Quiz-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	
	
	public Quiz insertIntoDB(Quiz quiz) throws RuntimeException {
		Connection con = DBConnection.connection();
		ResultSet rs;
		
		try{
			
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			
			// Auslesen der nach einfügen eines neuen Lecturers in DB entstandenen "größten" ID
			String sql = "SELECT MAX(id) AS maxid FROM Quiz;";
			rs = stmt.executeQuery(sql);
			
			sql = "INSERT INTO Quiz (`id`, `password`, `buttonduration`, `version`, `description`, `lecturerid`, `questionduration`, `solvingnumber`) "
					+ "VALUES ('"+(rs.getInt("maxid") + 1)+"', '"+quiz.getPassword()+"', '"+quiz.getDurationButton()+"', '"+quiz.getVersion()+"', '"+quiz.getDescription()+"', '"
					+quiz.getLecturerID()+"', '"+quiz.getDurationQuestion()+"', '"+quiz.getNeed2SS()+"');";
			stmt.executeUpdate(sql);
			
			// Auslesen der nach einfügen eines neuen Lecturers in DB entstandenen "größten" ID
			sql = "SELECT MAX(id) AS maxid FROM Quiz;";
			rs = stmt.executeQuery(sql);
			
			// Setzen der ID dem hier aktuellen Semesterverband-Objekt
			while(rs.next()){
				quiz.setId(rs.getInt("maxid") + 1);
			}
					
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm insert: " + e1.getMessage());
		}
		
		return quiz;
	}
	
	/**
	 * Methode um alle Quizzes aus der DB auszulesen
	 * 
	 * @return	Vector mit Quizzes
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Quiz> findAll() throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Quiz> quizzes = new Vector<Quiz>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Quiz ORDER BY description;";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				Quiz quiz = new Quiz();
				quiz.setId(rs.getInt("id"));
				quiz.setPassword(rs.getString("password"));
				quiz.setDurationButton(rs.getInt("buttonduration"));
				quiz.setVersion(rs.getInt("version"));
				quiz.setDescription(rs.getString("description"));
				quiz.setLecturerID(rs.getInt("lecturerid"));
				quiz.setDurationQuestion(rs.getInt("questionduration"));
				quiz.setNeed2SS(rs.getInt("solvingnumber"));
				quizzes.add(quiz);  
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fa: " + e1.getMessage());		
		}
		
				
		return quizzes;
			
	}
	
	/**
	 * Methode um ein Quiz aus der DB zu löschen
	 * 
	 * @param	quiz - Objekt welches gelöscht werden soll
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public void delete(Quiz quiz) throws RuntimeException {
		
		/* Das Löschen eines Lecturers hat das Löschen aller mit ihm
		 * in verbindungstehender Categories und Quizzes zur Folge
		 */
		
		
		Connection con = DBConnection.connection();
		try {
			Statement stmt = con.createStatement();
			
			// Löschen der Results
			
			// Löschen der NMTable_QQ
			
			// Löschen Löschen der Quiz-Entität
			String sql = "DELETE FROM Quiz WHERE ID = '"+quiz.getId()+"';";
			stmt.executeUpdate(sql);
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm.delete: " + e1.getMessage());
		}
		
	}
	

}