package de.hdm.clicker.server;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hdm.clicker.server.db.DBConnection;

@SuppressWarnings("serial")
public class ImageDownloadPlayQuizImpl extends HttpServlet{
	
	public static byte[] bbb = null;
	Blob blob = null;
	String imageId = null;
	int quizID;
	int questionID;
	TempDatabase tempDB = null;
	byte[] imageData = null;
	boolean flag = false;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		imageId =  request.getParameter("textBoxFormElement");
        
        if (imageId != null) {
			quizID = Integer.valueOf(imageId.substring(0,imageId.lastIndexOf(",")));
			questionID = Integer.valueOf(imageId.substring(imageId.lastIndexOf(",") + 1));
			tempDB = TempDatabase.get();
			for (QuizPackage qp : TempDatabase.getQpv()) {
				if (qp.getId() == quizID) {
					blob = qp.getImages().get(questionID);
					flag = true;
					break;
				}
			}
        }
        
        if (!flag) {
			blob = selectBlob();
			flag = false;
		}
		byte[] imageData = null;
		try {
			imageData = blob.getBytes(1, (int)blob.length());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		response.setContentType("image/png");
         try {
			response.getOutputStream().write(imageData,0,imageData.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Blob selectBlob() {
		Connection con = DBConnection.connection();
		ResultSet rs = null;
		
		
		try{
			// Ausf�hren des SQL-Statements
			Statement stmt = con.createStatement();
			String sql = "Select Image from `hdm-clicker`.Images Where id = " + questionID;
			rs = stmt.executeQuery(sql);
			
			if (rs.next()) {
             blob = rs.getBlob("Image");
			}
							
		}		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem: " + e1.getMessage());
		}
		return blob;
	}
}
