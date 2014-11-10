package de.hdm.clicker.server.db;


import java.sql.*;
import java.util.Vector;

import de.hdm.clicker.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Results</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see QuizMapper
 * @see LecturerMapper
 * @see QuestionMapper
 * @see ResultsMapper
 * 
 * @author Zimmermann, Roth, Zanella
 * @version 1.0
 */
public class ResultsMapper {
	
	/**
	 * Die Klasse ResultsMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static ResultsMapper ResultsMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected ResultsMapper(){
		
	}
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>ResultsMapper.resultsMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie dafür sorgt, dass nur eine einzige
	 * Instanz von <code>QuestionMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> ResultsMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>ResultsMapper</code>-Objekt.
	 */
	public static ResultsMapper questionMapper() {
	    if (ResultsMapper == null) {
	    	ResultsMapper = new ResultsMapper();
	    }

	    return ResultsMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Results anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	keys - Primärschlüsselattribut(e) (->DB)
	 * @return	Vector mit Results, die den Primärschlüsselattributen entsprechen
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Vector<Results> findByQuiz(int quizID, int quizVersion) throws RuntimeException {
					
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Results> results = new Vector<Results>();
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Results WHERE quizid="+quizID+" AND quizversion="+quizVersion+" ORDER BY hdmuser";
			rs = stmt.executeQuery(sql);
			
			// Erstellung des "Results-Vectors"
			while(rs.next()){
				Results result = new Results();
				result.setId(rs.getInt("id"));
				result.setAnswerNo(rs.getInt("answerno"));
				if (rs.getInt("answered") == 1) {
					result.setAnswered(true);
				} else {
					result.setAnswered(false);
				}
				result.setHdmUser(rs.getString("hdmuser"));
				result.setQuestionID(rs.getInt("questionid"));
				result.setQuizID(rs.getInt("quizid"));
				result.setQuizVersion(rs.getInt("quizversion"));
				if (rs.getInt("successed") == 1) {
					result.setSuccessed(true);;
				} else {
					result.setSuccessed(false);
				}
				results.add(result);
			}
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - rm fbk: " + e1.getMessage());				
		}
		
		return results;
	}
	
	/**
	 * Methode um ein neues Result in die DB zu schreiben
	 * 
	 * @param	results - Objekt welcher neu hinzukommt			
	 * @return	Results-Objekt
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public Results insertIntoDB(Results result) throws RuntimeException {
		int answered;
		int successed;
		
		if (result.isAnswered()) {
			answered = 1;
		} else {
			answered = 0;
		}
		
		if (result.isSuccessed()) {
			successed = 1;
		} else {
			successed = 0;
		}
		
				
		Connection con = DBConnection.connection();
		ResultSet rs;
						
		try{
			// Ausführung des SQL-Querys	
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Results (`answerno`, `answered`, `hdmuser`, `questionid`, "
					+ "`quizid`, `quizversion`, `successed`) "
					+ "VALUES ('"+result.getAnswerNo()+"', '"+answered+"', '"+result.getHdmUser()+"', '"+result.getQuestionID()
					+"', '"+result.getQuizID()+"', '"+result.getQuizVersion()+"', '"+successed+"');";
			stmt.executeUpdate(sql);
			
			// Auslesen der nach einfügen einer neuen Question in DB entstandenen "größten" ID
			sql = "SELECT MAX(ID) AS maxid FROM Question;";
			rs = stmt.executeQuery(sql);
			
			// Setzen der ID dem hier aktuellen Result-Objekt
			while(rs.next()){
				result.setId(rs.getInt("maxid"));
			}
					
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - rm.insert: " + e1.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Methode um alle Results aus der DB auszulesen
	 * 
	 * @return	Vector mit Results
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */	
	public Vector<Results> findAll() throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Results> results = new Vector<Results>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Results ORDER BY quizid, quizversion;";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				Results result = new Results();
				result.setId(rs.getInt("id"));
				result.setAnswerNo(rs.getInt("answerno"));
				if (rs.getInt("answered") == 1) {
					result.setAnswered(true);
				} else {
					result.setAnswered(false);
				}
				result.setHdmUser(rs.getString("hdmuser"));
				result.setQuestionID(rs.getInt("questionid"));
				result.setQuizID(rs.getInt("quizid"));
				result.setQuizVersion(rs.getInt("quizversion"));
				if (rs.getInt("successed") == 1) {
					result.setSuccessed(true);;
				} else {
					result.setSuccessed(false);
				}
				results.add(result);
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - rm fa: " + e1.getMessage());		
		}
		
				
		return results;
			
	}
	
	/**
	 * Methode um eine Results aus der DB zu löschen
	 * 
	 * @param	result - Objekt welches gelöscht werden soll
	 * @throws	Bei der Kommunikation mit der DB kann es zu Komplikationen kommen,
	 * 			die entstandene Exception wird an die aufrufende Methode weitergereicht
	 */
	public void delete(Results result) throws RuntimeException {
		
		/*
		 * Das löschen von Results ist kritisch, da Sie Bewegeungsdaten sind
		 * und zur Nachprüfung vorgehalten werden sollten
		 */
		
		Connection con = DBConnection.connection();
		try {
			Statement stmt = con.createStatement();
									
			// Löschen der Results-Entität
			String sql = "DELETE FROM Results WHERE id = '"+result.getId()+"';";
			stmt.executeUpdate(sql);
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - rm.delete: " + e1.getMessage());
		}
		
	}
	

}