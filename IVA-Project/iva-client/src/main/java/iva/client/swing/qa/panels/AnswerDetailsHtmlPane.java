/**
 * 
 */
package iva.client.swing.qa.panels;

import iva.client.core.model.Answer;
import iva.client.core.model.Question;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;

import org.apache.log4j.Logger;

/**
 * @author Aron
 */
@SuppressWarnings("serial")
public class AnswerDetailsHtmlPane extends JEditorPane {
	
	private static final Logger log = Logger.getLogger(AnswerDetailsHtmlPane.class);
	
	public AnswerDetailsHtmlPane() {
		super("text/html", "No answer selected");
		
		// Opens a clicked hyperlink in the system default web browser
		addHyperlinkListener(event -> {
			if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
			{
				if(Desktop.isDesktopSupported()) {
					try {
						URI link = event.getURL().toURI();
						try {
							Desktop.getDesktop().browse(link);
						} catch (IOException e1) {
							log.error("Unable to open hyperlink: "+link.toString(), e1);
						}
					} catch (URISyntaxException e2) {
						log.error("Unable to parse URI: "+event.getDescription(), e2);
					}
				} else {
					log.error(Desktop.class.getName()+" not supported");
				}
			}
		});
		
		setEditable(false);
	}
	
	public void displayAnswerDetails(Question question) {
		displayAnswerDetails(
				question.getQuestion(), 
				question.getCategories(), 
				"No query", 
				"No answer", 
				"N/A" );
	}
	
	public void displayAnswerDetails(Answer answer) {
		Question question = answer.getSourceQuestion();
		String docID = answer.getSourceDocument();
		
		final String wikipediaKMPrefix = "http://en.wikipedia.org/w/api.php";
		if( docID.startsWith(wikipediaKMPrefix) )
		{
			String wikipediaURL = "http://en.wikipedia.org/wiki/";

			int beginIndex = docID.indexOf("titles=") + ("titles=").length();
			int endIndex = docID.indexOf("&redirects&");

			String pageName = docID.substring(beginIndex, endIndex);

			docID = wikipediaURL.concat(pageName);
		}

		displayAnswerDetails(
				question.getQuestion(), 
				question.getCategories(), 
				answer.getSourceQuery(), 
				answer.getAnswer(), 
				"<a href=" + docID + ">"+docID+"</a>" );
	}
	
	public void displayAnswerDetails(
			String question, 
			Map<String, Double> categories, 
			String query, 
			String answer, 
			String source ) 
	{
		// Create an HTML table of question categories
		StringBuilder categoryTable = new StringBuilder("<table>");
		for(String category : categories.keySet()) {
			categoryTable.append("<tr><td>"+category+"</td><td>+"+categories.get(category)+"</td></tr>");
		}
		categoryTable.append("</table>");
		
		this.setText("<html>"
				+ "<p><b>Question:</b><br>" + question + "</p>"
				+ "<p><b>Question Categories:</b>" + categoryTable.toString() + "</p>"
				+ "<p><b>Query:</b><br>" + query + "</p>"
				+ "<p><b>Answer:</b><br>" + answer + "</p>"
				+ "<p><b>Source:</b><br>" + source + "</p>"
				+ "</html>" );
	}
	
	@Override
	public void setText(String t) {
		super.setText(t);
		setCaretPosition(0);
	}

}
