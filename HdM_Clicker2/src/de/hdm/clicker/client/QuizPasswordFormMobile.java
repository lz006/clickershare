package de.hdm.clicker.client;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.dialog.AlertDialog;
import com.googlecode.mgwt.ui.client.widget.input.MPasswordTextBox;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;

import de.hdm.clicker.shared.VerwaltungAsync;
import de.hdm.clicker.shared.bo.Question;
import de.hdm.clicker.shared.bo.Quiz;

public class QuizPasswordFormMobile extends FlexPanel{

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;
	
	/**
	 * Referenz auf die Clicker-Start-Seite
	 */
	Clicker clicker = null;
	
	MTextBox pwLBL = null;
	MPasswordTextBox pwTB = null;
	Button pwButton = null;
	
	Button zurueckButton = null;
	
	AlertDialog ad = null;
	
	ChooseQuizFormMobile cqfm = null;
	Quiz quiz = null;
	
	public QuizPasswordFormMobile (VerwaltungAsync verwaltungA) {
		
		this.verwaltung = verwaltungA;
		
		
		
		pwLBL = new MTextBox();
		pwLBL.setText("Password:");
		pwLBL.setEnabled(false);
		pwTB =  new MPasswordTextBox();
		pwButton = new Button("OK");
		
		zurueckButton = new Button("Zurück");
		
		this.add(pwLBL);
		this.add(pwTB);
		this.add(pwButton);
		this.add(zurueckButton);
		
		pwButton.addTapHandler(new TapHandler() {

			
			
			@Override
			public void onTap(TapEvent event) {
				
				if (!quiz.getPassword().equals(pwTB.getText())) {
					ad = new AlertDialog("Passwort nicht korrekt!","");
					ad.addTapHandler(new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						ad.hide();
						clicker.setChooseQuizFormMobileToMain();
						}
						
					});
					ad.show();
				}
				else {
					verwaltung.preloadQuizPackage(quiz, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							ad = new AlertDialog("Fehler",caught.getMessage());
							ad.addTapHandler(new TapHandler() {

							@Override
							public void onTap(TapEvent event) {
								ad.hide();
								clicker.setChooseQuizFormMobileToMain();
								}
								
							});
							ad.show();
							
						}

						@Override
						public void onSuccess(Void result) {
							verwaltung.loadQuestions(quiz, new AsyncCallback< Vector<Question>>() {

								@Override
								public void onFailure(Throwable caught) {
									ad = new AlertDialog("Fehler",caught.getMessage());
									ad.addTapHandler(new TapHandler() {

									@Override
									public void onTap(TapEvent event) {
										ad.hide();
										clicker.setChooseQuizFormMobileToMain();
										}
										
									});
									ad.show();
									
									
								}
	
								@Override
								public void onSuccess( Vector<Question> result) {
									PlayQuizFormMobile pqf = new PlayQuizFormMobile(verwaltung);
									pqf.setShownQuiz(quiz);
									pqf.setQuestionVector(result);
									pqf.setClicker(clicker);
									clicker.setPlayQuizFormMobileToMain(pqf);
								}
								
							});
							
						}
						
					});
				}
				
				
				
				
			}
			
		});
		
		zurueckButton.addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				clicker.setChooseQuizFormMobileToMain();
			}
		});
	}
	
	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}
	
	public void setChooseQuizFormMobile(ChooseQuizFormMobile cqfm) {
		this.cqfm = cqfm;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}
	
	
	
}
