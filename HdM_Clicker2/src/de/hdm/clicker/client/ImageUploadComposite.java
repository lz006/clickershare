package de.hdm.clicker.client;


import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class ImageUploadComposite extends Composite{
		
	final FormPanel form = new FormPanel();
	VerticalPanel vPanel = new VerticalPanel(); 
	FileUpload fileUpload = new FileUpload();
	Label imgFormats = new Label("Nur .jpg oder .png-Formate zulässig");
	TextBox tb = new TextBox();
	
	/**
	 * Flag zur Steuerung ob das hochgeladene Image angezeigt werden soll
	 */
	Boolean aendernMaske = false;
	
	/**
	 * Referenz auf ein ImageDownloadComposite-Objekt für den Methodenzugriff
	 */
	ImageDownloadComposite idc = null;
	
	QuestionForm qF = null;
	 
	public ImageUploadComposite(QuestionForm qF){
		
		this.qF = qF;
		
		form.setMethod(FormPanel.METHOD_POST);
		form.setEncoding(FormPanel.ENCODING_MULTIPART); //  multipart MIME encoding
		form.setAction("/clicker/upload"); // The servlet FileUploadGreeting
	    form.setWidget(vPanel);
	    fileUpload.setName("uploader"); // Very important    
	    vPanel.add(fileUpload); 
	    vPanel.add(imgFormats);
	    
	    tb.setName("textBoxFormElement");
	    tb.setVisible(false);
	    vPanel.add(tb);
		
	    initWidget(form);
	    
	    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				if (aendernMaske) {
					idc.loadImg(tb.getText());
					aendernMaske = false;
				}
				ImageUploadComposite.this.qF.setHasImage(true);
				//Window.alert("erledigt");
				
			}
	    	
	    });
	    
	    form.addSubmitHandler(new FormPanel.SubmitHandler() {
	        public void onSubmit(SubmitEvent event) {
	        	StringBuffer fileNameBuffer = new StringBuffer(fileUpload.getFilename());
	        	if (fileNameBuffer.length() < 1) {
	        		event.cancel();
	        		return;
	        	}
	        	if (fileNameBuffer.lastIndexOf(".") == -1) {
	        		event.cancel();
	        		Window.alert("Die Bild-Datei konnte nicht übernommen werden, bitte überprüfen Sie den Dateipfad");
	        		return;
	        	}
	        	String fileName = fileNameBuffer.substring(fileNameBuffer.lastIndexOf("."));
	        	if (!fileName.equals(".jpg") && !fileName.equals(".png")) {
	        		event.cancel();
	        		Window.alert("Nur Bildformate nach .jpg/.png zulässig!\nIhre Frage wurde ohne Bild gespeichert");
	        		
	        	}
	        }
	      });
	
	}
	
	public void aendernMaske(ImageDownloadComposite idc) {
		aendernMaske = true;
		this.idc = idc;
	}
	
	public void setImgID(String imgID) {
		tb.setText(imgID);
	}
	
	public void submitForm() {
		form.submit();
		
	}
}