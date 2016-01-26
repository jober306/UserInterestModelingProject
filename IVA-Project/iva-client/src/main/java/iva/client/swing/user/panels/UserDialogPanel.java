package iva.client.swing.user.panels;

import iva.client.core.model.InterestModel;
import iva.client.core.model.User;
import iva.client.core.services.InterestModelService;
import iva.client.core.services.InterestModelServiceHandler;
import iva.client.core.services.QuestionServiceHandler;
import iva.client.core.services.UserService;
import iva.client.core.services.UserServiceHandler;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * @author Aron
 */
@SuppressWarnings("serial")
public class UserDialogPanel extends JPanel {
	
	private final UserService userService = new UserServiceHandler();
	private final InterestModelService modelService = new InterestModelServiceHandler();
	
	private EditUserPanel panelEditUser;
	private EditInterestModelPanel panelEditInterestModel;
	private UserCategoriesPanel panelUserCategories;
	private ExplicitCategoryPanel panelExplicitCategory;
	private DeleteUserDataPanel panelDeleteUserData;

	public UserDialogPanel() {
		super();
		initializeGUI();
	}
	
	private void initializeGUI() {
		setLayout(new BorderLayout(0, 0));
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane);
		
		panelEditUser = new EditUserPanel(userService);
		tabbedPane.addTab("General", null, panelEditUser, null);
		
		panelEditInterestModel = new EditInterestModelPanel(modelService);
		tabbedPane.addTab("Interest Model", panelEditInterestModel);
		
		panelUserCategories = new UserCategoriesPanel();
		tabbedPane.addTab("Category Scores", null, panelUserCategories, null);
		
		panelExplicitCategory = new ExplicitCategoryPanel(modelService);
		tabbedPane.addTab("Explicit Categories", null, panelExplicitCategory, null);
		
		panelDeleteUserData = new DeleteUserDataPanel(modelService, new QuestionServiceHandler());
		tabbedPane.addTab("Delete Data", null, panelDeleteUserData, null);
	}
	
	public void setUser(User activeUser) {
		InterestModel model;
		if(activeUser != null) {
			model = modelService.findByOwner(activeUser);
		} else {
			// Used for layout testing
			activeUser = new User("username", ("password").toCharArray(), "firstName", "lastName");
			model = new InterestModel();
		}
		panelEditUser.setUser(activeUser);
		panelEditInterestModel.setInterestModel(model);
		panelUserCategories.setInterestModel(model);
		panelExplicitCategory.setInterestModel(model);
		panelDeleteUserData.setUser(activeUser, model);
	}
	
	public void saveChanges() {
		panelEditUser.saveChanges();
		panelEditInterestModel.saveChanges();
		panelExplicitCategory.saveChanges();
		panelDeleteUserData.saveChanges();
	}
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			try {
				javax.swing.UIManager.setLookAndFeel(
						javax.swing.UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
			
			UserDialogPanel userInfoPanel = new UserDialogPanel();
			
			javax.swing.JOptionPane.showConfirmDialog( 
					null, 
					userInfoPanel, 
					"Info for testuser", 
					javax.swing.JOptionPane.OK_CANCEL_OPTION, 
					javax.swing.JOptionPane.PLAIN_MESSAGE );
		});
	}

}