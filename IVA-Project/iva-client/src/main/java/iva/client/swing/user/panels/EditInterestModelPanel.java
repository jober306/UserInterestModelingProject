package iva.client.swing.user.panels;

import iva.client.core.model.InterestModel;
import iva.client.core.services.InterestModelService;
import iva.client.core.services.InterestModelServiceHandler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class EditInterestModelPanel extends JPanel {
	private static final long serialVersionUID = -943611553946530082L;
	
	private final JSpinner spnrShortTermAgeRate = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
	private final JSpinner spnrLongTermAgeRate = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
	private final JSpinner spnrPromotionThreshold = new JSpinner(new SpinnerNumberModel(-1.0, -1.0, null, 1.0));
	private final JSpinner spnrDemotionThreshold = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
	private final JSpinner spnrExpirationThreshold = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
	
	private final InterestModelService modelService;
	private InterestModel model;
	
	public EditInterestModelPanel() {
		this(new InterestModelServiceHandler());
	}
	
	public EditInterestModelPanel(InterestModelService modelService) {
		initializeGUI();
		this.modelService = modelService;
	}
	
	private void initializeGUI() {
		GridBagLayout gbl_panel = new GridBagLayout();
		this.setLayout(gbl_panel);
		
		JLabel label = new JLabel("Short Term Age Rate");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(5, 5, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		this.add(label, gbc_label);
		
		((JSpinner.DefaultEditor) spnrShortTermAgeRate.getEditor()).getTextField().setColumns(3);
		label.setLabelFor(spnrShortTermAgeRate);
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.anchor = GridBagConstraints.WEST;
		gbc_spinner.insets = new Insets(5, 5, 5, 5);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 0;
		this.add(spnrShortTermAgeRate, gbc_spinner);
		
		JLabel label_1 = new JLabel("Long Term Age Rate");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(5, 5, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 1;
		this.add(label_1, gbc_label_1);
		
		((JSpinner.DefaultEditor) spnrLongTermAgeRate.getEditor()).getTextField().setColumns(3);
		label_1.setLabelFor(spnrLongTermAgeRate);
		GridBagConstraints gbc_spinner_1 = new GridBagConstraints();
		gbc_spinner_1.anchor = GridBagConstraints.WEST;
		gbc_spinner_1.insets = new Insets(5, 5, 5, 5);
		gbc_spinner_1.gridx = 1;
		gbc_spinner_1.gridy = 1;
		this.add(spnrLongTermAgeRate, gbc_spinner_1);
		
		JLabel label_2 = new JLabel("Promotion Threshold");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.insets = new Insets(5, 5, 5, 5);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 2;
		this.add(label_2, gbc_label_2);
		
		((JSpinner.DefaultEditor) spnrPromotionThreshold.getEditor()).getTextField().setColumns(3);
		label_2.setLabelFor(spnrPromotionThreshold);
		GridBagConstraints gbc_spinner_2 = new GridBagConstraints();
		gbc_spinner_2.anchor = GridBagConstraints.WEST;
		gbc_spinner_2.insets = new Insets(5, 5, 5, 5);
		gbc_spinner_2.gridx = 1;
		gbc_spinner_2.gridy = 2;
		this.add(spnrPromotionThreshold, gbc_spinner_2);
		
		JLabel label_3 = new JLabel("Demotion Threshold");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.EAST;
		gbc_label_3.insets = new Insets(5, 5, 5, 5);
		gbc_label_3.gridx = 0;
		gbc_label_3.gridy = 3;
		this.add(label_3, gbc_label_3);
		
		((JSpinner.DefaultEditor) spnrDemotionThreshold.getEditor()).getTextField().setColumns(3);
		label_3.setLabelFor(spnrDemotionThreshold);
		GridBagConstraints gbc_spinner_3 = new GridBagConstraints();
		gbc_spinner_3.anchor = GridBagConstraints.WEST;
		gbc_spinner_3.insets = new Insets(5, 5, 5, 5);
		gbc_spinner_3.gridx = 1;
		gbc_spinner_3.gridy = 3;
		this.add(spnrDemotionThreshold, gbc_spinner_3);
		
		JLabel label_4 = new JLabel("Expiration Threshold");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.anchor = GridBagConstraints.EAST;
		gbc_label_4.insets = new Insets(5, 5, 5, 5);
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 4;
		this.add(label_4, gbc_label_4);
		
		((JSpinner.DefaultEditor) spnrExpirationThreshold.getEditor()).getTextField().setColumns(3);
		label_4.setLabelFor(spnrExpirationThreshold);
		GridBagConstraints gbc_spinner_4 = new GridBagConstraints();
		gbc_spinner_4.anchor = GridBagConstraints.WEST;
		gbc_spinner_4.insets = new Insets(5, 5, 5, 5);
		gbc_spinner_4.gridx = 1;
		gbc_spinner_4.gridy = 4;
		this.add(spnrExpirationThreshold, gbc_spinner_4);
	}
	
	public void setInterestModel(InterestModel model) {
		this.model = model;
		
		spnrShortTermAgeRate.setValue(model.getShortTermAgeRate());
		spnrLongTermAgeRate.setValue(model.getLongTermAgeRate());
		spnrPromotionThreshold.setValue(model.getPromotionThreshold());
		spnrDemotionThreshold.setValue(model.getDemotionThreshold());
		spnrExpirationThreshold.setValue(model.getExpirationThreshold());
	}
	
	public void saveChanges() {
		model.setShortTermAgeRate((Double) spnrShortTermAgeRate.getValue());
		model.setLongTermAgeRate((Double) spnrLongTermAgeRate.getValue());
		model.setPromotionThreshold((Double) spnrShortTermAgeRate.getValue());
		model.setDemotionThreshold((Double) spnrShortTermAgeRate.getValue());
		model.setExpirationThreshold((Double) spnrShortTermAgeRate.getValue());
		
		modelService.update(model);
	}
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			try {
				javax.swing.UIManager.setLookAndFeel(
						javax.swing.UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
			
			EditInterestModelPanel testPanel = new EditInterestModelPanel();
			
			javax.swing.JFrame frame = new javax.swing.JFrame(
					testPanel.getClass().getSimpleName());
			
			frame.getContentPane().add(testPanel);
			frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

}
