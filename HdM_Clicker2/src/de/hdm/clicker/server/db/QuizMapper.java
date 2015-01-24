package de.hdm.clicker.server.db;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.i18n.client.DateTimeFormat;

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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quizzes.add(quiz);  
	          }
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fbk: " + e1.getMessage());				
		}
		
		return quizzes;
	}
	
	/**
	 * Methode um eine beliebige Anzahl an Quizzes in den jeweils jüngsten
	 * Versionen anhand einer Question aus der DB auszulesen
	 * 
	 * @param	question - Question-Objekt
	 * @return	Vector mit Quizzes, die der Question zugeordnet sind
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Quiz> findByQuestion(Question question) throws RuntimeException {
					
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Quiz> quizzes = new Vector<Quiz>();
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM `hdm-clicker`.Quiz where (id, version) in (SELECT quizid, max(quizversion) as quizversion "
					+ "FROM `hdm-clicker`.NMTable_QQ where questionid ="+question.getId()+" group by quizid)";
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quizzes.add(quiz);   
	          }
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fbk: " + e1.getMessage());				
		}
		
		return quizzes;
	}
	
	/**
	 * Methode um alle Quizzes anhand eines Lecturers aus der DB auszulesen
	 * 
	 * @return	Vector mit Quizzes
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Quiz> findByLecturer(Lecturer lec) throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Quiz> quizzes = new Vector<Quiz>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM `hdm-clicker`.Quiz where lecturerid = "+lec.getId()+" and (id, version) in "
					+ "(SELECT id, max(version) as version FROM `hdm-clicker`.Quiz group by id) ORDER BY description;";
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quizzes.add(quiz);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fbl: " + e1.getMessage());		
		}
		
				
		return quizzes;
			
	}
	
	/**
	 * Methode um alle Startdaten von Quizzen anhand eines Lecturers aus der DB auszulesen
	 * 
	 * @return	Vector mit Daten
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Integer> findDatesByLecturer(Lecturer lec) throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Integer> dates = new Vector<Integer>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT startdate FROM `hdm-clicker`.Quiz WHERE lecturerid="+lec.getId()+" GROUP BY startdate ORDER BY startdate DESC";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				Integer date = new Integer(rs.getInt("startdate"));
				dates.add(date);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fdbl: " + e1.getMessage());		
		}
		
				
		return dates;
			
	}
	
	/**
	 * Methode um alle Quizzes anhand eines Lecturers und Datums aus der DB auszulesen
	 * 
	 * @return	Vector mit Quizzes
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Quiz> findByLecturerAndDate(Lecturer lec, int date) throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Quiz> quizzes = new Vector<Quiz>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM `hdm-clicker`.Quiz where lecturerid="+lec.getId()+" and startdate="+date;
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quizzes.add(quiz);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fblad: " + e1.getMessage());		
		}
		
				
		return quizzes;
			
	}
	
	/**
	 * Methode um alle Quizzes anhand eines Lecturers aus der DB auszulesen
	 * 
	 * @return	Vector mit Quizzes
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Quiz> findByLecturerAndActive(Lecturer lec) throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Quiz> quizzes = new Vector<Quiz>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM `hdm-clicker`.Quiz where lecturerid = "+lec.getId()+" AND (id, version) in "
					+ "(SELECT id, max(version) as version FROM `hdm-clicker`.Quiz group by id) AND active=1 ORDER BY description;";
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quizzes.add(quiz);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fbla: " + e1.getMessage());		
		}
		
				
		return quizzes;
			
	}
	
	

	/**
	 * Methode um ein Quiz in der DB zu aktualisieren
	 * 
	 * @param	quiz - Objekt welches aktualisiert werden soll 
	 * 			vq - Vector mit den zugeordneten Questions			
	 * @return	Quiz-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Quiz update(Quiz quiz, Vector<Question> vq) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		int active;
		int automatic;
		int random;
		int started;
		
		if (quiz.isActive()) {
			active = 1;
		}
		else {
			active = 0;
		}
		if (quiz.isAutomatic()) {
			automatic = 1;
		}
		else {
			automatic = 0;
		}
		if (quiz.isRandom()) {
			random = 1;
		}
		else {
			random = 0;
		}
		if (quiz.isStarted()) {
			started = 1;
		}
		else {
			started = 0;
		}
		
		// Aktualisierung der Dozent-Entität in der DB		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "UPDATE Quiz SET password='"+quiz.getPassword()+"', buttonduration='"+quiz.getDurationButton()
					+"', version='"+quiz.getVersion()+"', description='"+quiz.getDescription()+"', lecturerid='"+quiz.getLecturerID()
					+"', questionduration='"+quiz.getDurationQuestion()+"', startdate='"+quiz.getStartDate()+"', starttime='"+quiz.getStartTime()
					+"', active="+active+", automatic="+automatic+", random="+random+", started="+started+", questions="+quiz.getQuestionsCount()
					+" WHERE id="+quiz.getId()+" AND version="+quiz.getVersion()+";";
			stmt.executeUpdate(sql);
			
			// Bereinigen der Zwischentabelle
			sql = "DELETE FROM NMTable_QQ WHERE quizid="+quiz.getId()+" AND quizversion="+quiz.getVersion();
			stmt.executeUpdate(sql);
			
			// Neu Aufsetzen der N:M-Beziehung
			if (vq != null && vq.size() > 0) {
				sql = "INSERT INTO NMTable_QQ (`order`, `questionid`, `quizid`, `quizversion`) VALUES "
						+ "("+1+", "+vq.elementAt(0).getId()+", "+quiz.getId()+", "+quiz.getVersion()+")";
				for(int i = 1; i < vq.size(); i++) {
					sql = sql + " ,("+ i+1 +", "+vq.elementAt(i).getId()+", "+quiz.getId()+", "+quiz.getVersion()+")";
				}
				stmt.executeUpdate(sql);
			}
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm.update: " + e1.getMessage());
		}
		
		return quiz;
	}
	
	/**
	 * Methode um ein Quiz in der DB zu aktualisieren
	 * 
	 * @param	quiz - Objekt welches aktualisiert werden soll 
	 * 			vq - Vector mit den zugeordneten Questions			
	 * @return	Quiz-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Quiz startUpdate(Quiz quiz) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		int active;
		int automatic;
		int random;
		int started;
		
		if (quiz.isActive()) {
			active = 1;
		}
		else {
			active = 0;
		}
		if (quiz.isAutomatic()) {
			automatic = 1;
		}
		else {
			automatic = 0;
		}
		if (quiz.isRandom()) {
			random = 1;
		}
		else {
			random = 0;
		}
		if (quiz.isStarted()) {
			started = 1;
		}
		else {
			started = 0;
		}
		
		// Aktualisierung der Dozent-Entität in der DB		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "UPDATE Quiz SET password='"+quiz.getPassword()+"', buttonduration='"+quiz.getDurationButton()
					+"', version='"+quiz.getVersion()+"', description='"+quiz.getDescription()+"', lecturerid='"+quiz.getLecturerID()
					+"', questionduration='"+quiz.getDurationQuestion()+"', startdate='"+quiz.getStartDate()+"', starttime='"+quiz.getStartTime()
					+"', active="+active+", automatic="+automatic+", random="+random+", started="+started+", questions="+quiz.getQuestionsCount()
					+" WHERE id="+quiz.getId()+" AND version="+quiz.getVersion()+";";
			stmt.executeUpdate(sql);
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm.startupdate: " + e1.getMessage());
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
	
	
	public Quiz firstInsertIntoDB(Quiz quiz, Vector<Question> vq) throws RuntimeException {
		Connection con = DBConnection.connection();
		ResultSet rs;
		
		int active;
		int automatic;
		int random;
		int started = 0;
		
		if (quiz.isActive()) {
			active = 1;
		}
		else {
			active = 0;
		}
		if (quiz.isAutomatic()) {
			automatic = 1;
		}
		else {
			automatic = 0;
		}
		if (quiz.isRandom()) {
			random = 1;
		}
		else {
			random = 0;
		}
		
		try{
			
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			
			String sql = "INSERT INTO Quiz (`password`, `buttonduration`, `version`, `description`, `lecturerid`, `questionduration`, `startdate`, `starttime`, `active`, `automatic`, `random`, `started`, `questions`) "
					+ "VALUES ('"+quiz.getPassword()+"', '"+quiz.getDurationButton()+"', '"+quiz.getVersion()+1+"', '"+quiz.getDescription()+"', '"
					+quiz.getLecturerID()+"', '"+quiz.getDurationQuestion()+"', "+quiz.getStartDate()+", "+quiz.getStartTime()+", "+active+", "+automatic+", "+random+", "+started+", "+quiz.getQuestionsCount()+");";
			stmt.executeUpdate(sql);
			
			// Auslesen der nach einfügen eines neuen Lecturers in DB entstandenen "größten" ID
			sql = "SELECT MAX(id) AS maxid FROM Quiz;";
			rs = stmt.executeQuery(sql);
			
			// Setzen der ID dem hier aktuellen Quiz-Objekt
			while(rs.next()){
				quiz.setId(rs.getInt("maxid"));
			}
			
			// Aufsetzen der N:M-Beziehung
			if (vq != null && vq.size() > 0) {
				sql = "INSERT INTO NMTable_QQ (`order`, `questionid`, `quizid`, `quizversion`) VALUES "
						+ "("+1+", "+vq.elementAt(0).getId()+", "+quiz.getId()+", "+quiz.getVersion()+")";
				for(int i = 1; i < vq.size(); i++) {
					sql = sql + " ,("+ i+1 +", "+vq.elementAt(i).getId()+", "+quiz.getId()+", "+quiz.getVersion()+")";
				}
				stmt.executeUpdate(sql);
			}
					
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm firstinsert: " + e1.getMessage());
		}
		
		return quiz;
	}
	
	public Quiz insertIntoDB(Quiz quiz) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		int newVersion = 0;
		int active;
		int automatic;
		int random;
		int started;
		
		if (quiz.isActive()) {
			active = 1;
		}
		else {
			active = 0;
		}
		if (quiz.isAutomatic()) {
			automatic = 1;
		}
		else {
			automatic = 0;
		}
		if (quiz.isRandom()) {
			random = 1;
		}
		else {
			random = 0;
		}
		if (quiz.isStarted()) {
			started = 1;
		}
		else {
			started = 0;
		}
		
		newVersion = quiz.getVersion() + 1;
		
		try{
			
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			
			String sql = "INSERT INTO Quiz (`id`, `password`, `buttonduration`, `version`, `description`, `lecturerid`, `questionduration`, `startdate`, `starttime`, `active`, `automatic`, `random`, `started`, `questions`) "
					+ "VALUES ("+quiz.getId()+", '"+quiz.getPassword()+"', '"+quiz.getDurationButton()+"', '"+newVersion+"', '"+quiz.getDescription()+"', '"
					+quiz.getLecturerID()+"', '"+quiz.getDurationQuestion()+"', "+quiz.getStartDate()+", "+quiz.getStartTime()+", "+active+", "+automatic+", "+random+", "+started+", "+quiz.getQuestionsCount()+");";
			stmt.executeUpdate(sql);
			
			
			// Aufdoppeln der n:M Beziehungen der Questions hin von der "alte" zur neuen Quizversion
			sql ="SELECT * FROM `hdm-clicker`.NMTable_QQ WHERE quizid="+quiz.getId()+" AND quizversion="+quiz.getVersion();
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;
			boolean firstIt = true;
			int i = 2;
			
			while(rs.next()) {
				if (firstIt) {
					sql = "INSERT INTO NMTable_QQ (`order`, `questionid`, `quizid`, `quizversion`) VALUES "
							+ "("+1+", "+rs.getInt("questionid")+", "+rs.getInt("quizid")+", "+rs.getInt("quizversion")+1+")";
					firstIt = false;
				}
				else {
					sql = sql + " ,("+i+", "+rs.getInt("questionid")+", "+rs.getInt("quizid")+", "+rs.getInt("quizversion")+1+")";
					i++;
				}				
			}
			
			if (sql != null) {
				stmt.executeUpdate(sql);
			}
			
			
			
					
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm insert: " + e1.getMessage());
		}
		
		quiz.setVersion(quiz.getVersion() + 1);
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quizzes.add(quiz);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fa: " + e1.getMessage());		
		}
		
				
		return quizzes;
			
	}
	
	/**
	 * Methode um alle aktiven Quizzes aus der DB auszulesen
	 * 
	 * @return	Vector mit Quizzes
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Quiz> findAllActive() throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Quiz> quizzes = new Vector<Quiz>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Quiz WHERE active=1 ORDER BY description;";
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quiz.setQuestionsCount(rs.getInt("questions"));
				quizzes.add(quiz);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm faa: " + e1.getMessage());		
		}
		
				
		return quizzes;
			
	}
	
	/**
	 * Methode um alle gestarteten Quizzes aus der DB auszulesen
	 * 
	 * @return	Vector mit Quizzes
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Quiz> findAllStarted() throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Quiz> quizzes = new Vector<Quiz>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Quiz WHERE started=1 ORDER BY description;";
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quiz.setQuestionsCount(rs.getInt("questions"));
				quizzes.add(quiz);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fas: " + e1.getMessage());		
		}
		
				
		return quizzes;
			
	}
	
	/**
	 * Methode um alle aktiven und gestarten Quizzes aus der DB auszulesen
	 * 
	 * @return	Vector mit Quizzes
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Quiz> findAllActiveAndAuto() throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Quiz> quizzes = new Vector<Quiz>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Quiz WHERE active=1 AND automatic=1 AND NOT started=1 AND startdate="+new Integer(new SimpleDateFormat("yyyyMMdd").format(new Date())) +" ORDER BY description;";
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
				quiz.setStartDate(rs.getInt("startdate"));
				quiz.setStartTime(rs.getInt("starttime"));
				if(rs.getInt("active") > 0) {
					quiz.setActive(true);
				} 
				else {
					quiz.setActive(false);
				}
				if(rs.getInt("automatic") > 0) {
					quiz.setAutomatic(true);
				} 
				else {
					quiz.setAutomatic(false);
				}
				if(rs.getInt("random") > 0) {
					quiz.setRandom(true);
				} 
				else {
					quiz.setRandom(false);
				}
				if(rs.getInt("started") > 0) {
					quiz.setStarted(true);
				} 
				else {
					quiz.setStarted(false);
				}
				quiz.setQuestionsCount(rs.getInt("questions"));
				quizzes.add(quiz);    
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm faaa: " + e1.getMessage());		
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
			
			// Bereinigen der Zwischentabelle
			String sql = "DELETE FROM NMTable_QQ WHERE quizid="+quiz.getId()+" AND quizversion="+quiz.getVersion();
			stmt.executeUpdate(sql);
			
			// Löschen Löschen der Quiz-Entität
			sql = "DELETE FROM Quiz WHERE id = "+quiz.getId()+" AND version="+quiz.getVersion()+";";
			stmt.executeUpdate(sql);
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm.delete: " + e1.getMessage());
		}
		
	}
	

}