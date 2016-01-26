package iva.client.swing.qa.panels;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class SelectRatingPanel extends JPanel {
	
	private JLabel lblFeedback;
	private JRadioButton rbYes, rbNo, rbNA;
	private final ButtonGroup bgFeedback = new ButtonGroup();
	
	public SelectRatingPanel() {
		initializeGUI();
	}
	
	public SelectRatingPanel(Action ratingHandler) {
		this();
		setAction(ratingHandler);
	}
	
	private void initializeGUI() {
		lblFeedback = new JLabel("Was this answer helpful?");
		this.add(lblFeedback);

		rbYes = new JRadioButton("Yes");
		bgFeedback.add(rbYes);
		this.add(rbYes);

		rbNo = new JRadioButton("No");
		bgFeedback.add(rbNo);
		this.add(rbNo);
		
		rbNA = new JRadioButton("N/A");
		rbNA.setSelected(true);
		bgFeedback.add(rbNA);
		this.add(rbNA);
	}

	public void setAction(Action a) {
		rbYes.addActionListener(a);
		rbNo.addActionListener(a);
	}
	
	public JRadioButton getRadioButton(String actionCommand) {
		JRadioButton foundRadioButton = null;
		
		Enumeration<AbstractButton> elements = bgFeedback.getElements();
		
		while(elements.hasMoreElements()) {
			AbstractButton radioButton = elements.nextElement();
			
			if(radioButton.getActionCommand().equals(actionCommand)) {
				foundRadioButton = (JRadioButton) radioButton;
			}
		}
		assert(foundRadioButton != null):"Radio button not found: "+actionCommand;
		return foundRadioButton;
	}
	
	public void setSelectedRadioButton(String actionCommand) {
		JRadioButton radioButton = getRadioButton(actionCommand);
		radioButton.setSelected(true);
	}
	
}