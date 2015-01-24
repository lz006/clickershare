package de.hdm.clicker.server.db;


import java.sql.*;
import java.util.Hashtable;

import de.hdm.clicker.server.QuizPackage;
import de.hdm.clicker.shared.bo.*;

/**
 * 
 * 
 * @author Zimmermann, Roth, Zanella
 * @version 1.0
 */
public class QuizPackageMapper {
	
	/**
	 * Die Klasse QuizMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal für
	 * sämtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 */
	private static QuizPackageMapper QuizPackageMapper = null;
	
	/**
	 * Geschützter Konstruktor - verhindert die Möglichkeit, mit new neue
	 * Instanzen dieser Klasse zu erzeugen.
	 * 
	 */
	protected QuizPackageMapper(){
		
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
	public static QuizPackageMapper quizPackageMapper() {
	    if (QuizPackageMapper == null) {
	    	QuizPackageMapper = new QuizPackageMapper();
	    }

	    return QuizPackageMapper;
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
	public QuizPackage findByQuiz(Quiz quiz) throws RuntimeException {
					
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		QuizPackage qpv = null;
		Hashtable<Integer, Question> questions = new Hashtable<Integer, Question>();
		Hashtable<Integer, Blob> images = new Hashtable<Integer, Blob>();
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Quiz AS quiz INNER JOIN NMTable_QQ AS nm ON quiz.id = nm.quizid AND "
					+ "quiz.version = nm.quizversion INNER JOIN Question AS ques ON nm.questionid = ques.id "
					+ "LEFT JOIN Images AS img ON img.id = ques.id WHERE quiz.id=" + quiz.getId() + " AND "
							+ "quiz.version=" + quiz.getVersion();
			rs = stmt.executeQuery(sql);
			
			boolean firstIt = true;
			
			while(rs.next()){
				if (firstIt) {
					qpv = new QuizPackage();
					qpv.setId(rs.getInt(1));
					qpv.setVersion(rs.getInt(2));
					
					Quiz tmpquiz = new Quiz();
					tmpquiz.setId(rs.getInt(1));
					tmpquiz.setPassword(rs.getString("password"));
					tmpquiz.setDurationButton(rs.getInt("buttonduration"));
					tmpquiz.setVersion(rs.getInt(2));
					tmpquiz.setDescription(rs.getString("description"));
					tmpquiz.setLecturerID(rs.getInt("lecturerid"));
					tmpquiz.setDurationQuestion(rs.getInt("questionduration"));
					tmpquiz.setStartDate(rs.getInt("startdate"));
					tmpquiz.setStartTime(rs.getInt("starttime"));
					if(rs.getInt(10) > 0) {
						tmpquiz.setActive(true);
					} 
					else {
						tmpquiz.setActive(false);
					}
					if(rs.getInt("automatic") > 0) {
						tmpquiz.setAutomatic(true);
					} 
					else {
						tmpquiz.setAutomatic(false);
					}
					if(rs.getInt("random") > 0) {
						tmpquiz.setRandom(true);
					} 
					else {
						tmpquiz.setRandom(false);
					}
					if(rs.getInt("started") > 0) {
						tmpquiz.setStarted(true);
					} 
					else {
						tmpquiz.setStarted(false);
					}
					qpv.setQuiz(tmpquiz);
					
					firstIt = false;
				}
				Question question = new Question();
				question.setId(rs.getInt(20));
				question.setQuestionBody(rs.getString("questionbody"));
				if (rs.getInt("image") == 1) {
					question.setImage(true);
					images.put(new Integer(rs.getInt(20)), rs.getBlob(32));
				} else {
					question.setImage(false);
				}
				question.setCorrectAnswer(rs.getInt("correctAnswer"));
				question.setSeverity(rs.getInt("severity"));
				question.setAnswer1(rs.getString("Answer_1"));
				question.setAnswer2(rs.getString("Answer_2"));
				question.setAnswer3(rs.getString("Answer_3"));
				question.setAnswer4(rs.getString("Answer_4"));
				if (rs.getInt("Active") == 1) {
					question.setActive(true);
				} else {
					question.setActive(false);
				}
				question.setCategoryID(rs.getInt("categoryid"));
				questions.put(new Integer(rs.getInt(20)), question);
	          }
			qpv.setQuestionHT(questions);
			qpv.setImages(images);
						
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - quizm fbk: " + e1.getMessage());				
		}
		
		return qpv;
	}
	

}