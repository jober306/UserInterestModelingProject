package iva.client.swing.user.panels;

import iva.client.core.model.InterestModel;
import iva.client.core.model.User;
import iva.client.core.services.InterestModelService;
import iva.client.core.services.InterestModelServiceHandler;
import iva.client.core.services.QuestionService;
import iva.client.core.services.QuestionServiceHandler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class DeleteUserDataPanel extends JPanel {
	private static final Logger log = Logger.getLogger(DeleteUserDataPanel.class);
	
	private JCheckBox chckbxDeleteUserCategories;
	private JCheckBox chckbxDeleteUserHistory;
	
	private final InterestModelService modelService;
	private final QuestionService questionService;
	
	private User activeUser;
	private InterestModel model;
	
	public DeleteUserDataPanel() {
		this(new InterestModelServiceHandler(), new QuestionServiceHandler());
	}
	
	public DeleteUserDataPanel(InterestModelService modelService, QuestionService questionService) {
		initializeGUI();
		this.modelService = modelService;
		this.questionService = questionService;
	}
	
	private void initializeGUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		chckbxDeleteUserCategories = new JCheckBox("Delete User Categories");
		GridBagConstraints gbc_chckbxDeleteAllInterests = new GridBagConstraints();
		gbc_chckbxDeleteAllInterests.insets = new Insets(5, 0, 5, 0);
		gbc_chckbxDeleteAllInterests.anchor = GridBagConstraints.WEST;
		gbc_chckbxDeleteAllInterests.gridx = 0;
		gbc_chckbxDeleteAllInterests.gridy = 0;
		add(chckbxDeleteUserCategories, gbc_chckbxDeleteAllInterests);
		
		chckbxDeleteUserHistory = new JCheckBox("Delete User History");
		GridBagConstraints gbc_chckbxDeleteAllHistory = new GridBagConstraints();
		gbc_chckbxDeleteAllHistory.insets = new Insets(5, 0, 5, 0);
		gbc_chckbxDeleteAllHistory.anchor = GridBagConstraints.WEST;
		gbc_chckbxDeleteAllHistory.gridx = 0;
		gbc_chckbxDeleteAllHistory.gridy = 1;
		add(chckbxDeleteUserHistory, gbc_chckbxDeleteAllHistory);
	}
	
	public void setUser(User activeUser, InterestModel model) {
		this.activeUser = activeUser;
		this.model = model;
		
		chckbxDeleteUserCategories.setSelected(false);
		chckbxDeleteUserHistory.setSelected(false);
	}

	public void saveChanges() {
		if(chckbxDeleteUserCategories.isSelected()) {
			log.info("Deleting User Categories");
			deleteAllInterests();
		}
		if(chckbxDeleteUserHistory.isSelected()) {
			log.info("Deleting User History");
			deleteAllHistory();
		}
		chckbxDeleteUserCategories.setSelected(false);
		chckbxDeleteUserHistory.setSelected(false);
	}
	
	public void deleteAllInterests() {
		modelService.deleteAllCategories(model);
	}
	
	public void deleteAllHistory() {
		questionService.deleteAllWithOwner(activeUser);
	}
}
