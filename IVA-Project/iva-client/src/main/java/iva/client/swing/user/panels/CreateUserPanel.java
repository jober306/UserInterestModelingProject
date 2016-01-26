package iva.client.swing.user.panels;

import iva.client.core.model.User;
import iva.client.core.services.UserService;
import iva.client.core.services.UserServiceHandler;
import iva.client.swing.ApplicationWindow;
import iva.client.swing.ApplicationWindow.View;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.web.client.RestClientException;

/**
 * The panel to add the details of the user into the database.
 * @author Kyle
 * @author Aron
 */
@SuppressWarnings("serial")
public class CreateUserPanel extends JPanel {
	
	private ApplicationWindow window;
	
	private JTextField txtUsername;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JButton btnCancel;
	private JButton btnSubmit;
	private final Action actionCreateUser = new CreateUserAction();
	private final Action actionCancel = new CancelAction();
	
	/**
	 * Initialize the CreateUserPanel layout.
	 */
	public CreateUserPanel() {
		initializeGUI();
	}
	
	/**
	 * Initialize the CreateUserPanel layout and associate with ApplicationWindow.
	 * @param window the ApplicationWindow
	 */
	public CreateUserPanel(ApplicationWindow window) {
		this();
		this.window = window;
	}
	
	private void initializeGUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		JLabel lblMessage = new JLabel("<html><center>Please enter your personal details below. <br/>You must fill out all fields.</center><html>");
		lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblMessage = new GridBagConstraints();
		gbc_lblMessage.gridx = 0;
		gbc_lblMessage.gridy = 0;
		add(lblMessage, gbc_lblMessage);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel usernamePanel = new JPanel();
		panel.add(usernamePanel);
		FlowLayout flowLayout_5 = (FlowLayout) usernamePanel.getLayout();
		flowLayout_5.setAlignment(FlowLayout.RIGHT);
		
		JLabel lblUsername = new JLabel("Username");
		usernamePanel.add(lblUsername);
		
		txtUsername = new JTextField();
		usernamePanel.add(txtUsername);
		txtUsername.setColumns(10);
		
		JPanel passwordPanel = new JPanel();
		panel.add(passwordPanel);
		FlowLayout flowLayout_4 = (FlowLayout) passwordPanel.getLayout();
		flowLayout_4.setAlignment(FlowLayout.RIGHT);
		
		JLabel lblPassword = new JLabel("Password");
		passwordPanel.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		passwordPanel.add(passwordField);
		
		JPanel passwordPanel_1 = new JPanel();
		panel.add(passwordPanel_1);
		FlowLayout flowLayout_3 = (FlowLayout) passwordPanel_1.getLayout();
		flowLayout_3.setAlignment(FlowLayout.RIGHT);
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		passwordPanel_1.add(lblConfirmPassword);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setColumns(10);
		passwordPanel_1.add(passwordField_1);
		
		JPanel firstNamePanel = new JPanel();
		panel.add(firstNamePanel);
		FlowLayout flowLayout_2 = (FlowLayout) firstNamePanel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);
		
		JLabel lblFirstName = new JLabel("First Name");
		firstNamePanel.add(lblFirstName);
		
		txtFirstName = new JTextField();
		firstNamePanel.add(txtFirstName);
		txtFirstName.setColumns(10);
		
		JPanel lastNamePanel = new JPanel();
		panel.add(lastNamePanel);
		FlowLayout flowLayout_1 = (FlowLayout) lastNamePanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		
		JLabel lblLastName = new JLabel("Last Name");
		lastNamePanel.add(lblLastName);
		
		txtLastName = new JTextField();
		lastNamePanel.add(txtLastName);
		txtLastName.setColumns(10);
		
		JPanel buttonPanel = new JPanel();
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 2;
		add(buttonPanel, gbc_buttonPanel);
		
		btnSubmit = new JButton();
		btnSubmit.setAction(actionCreateUser);
		buttonPanel.add(btnSubmit);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setAction(actionCancel);
		buttonPanel.add(btnCancel);
	}
	
	public void reset() {
		txtUsername.setText("");
		txtFirstName.setText("");
		txtLastName.setText("");
		passwordField.setText("");
		passwordField_1.setText("");
	}

	private class CreateUserAction extends AbstractAction {
		private final UserService service = new UserServiceHandler();
		
		public CreateUserAction() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "");
		}
		@Override
		public void actionPerformed(ActionEvent event) {
			String username = txtUsername.getText().trim();
			char[] password = passwordField.getPassword();
			char[] password_1 = passwordField_1.getPassword();
			String firstName = txtFirstName.getText().trim();
			String lastName = txtLastName.getText().trim();
			
			try {
				if(username.length() == 0 || password.length == 0 || password_1.length == 0) {
					throw new Exception("Missing username or password.");
				}
				else if( !Arrays.equals(password, password_1) ) {
					throw new Exception("Passwords do not match.");
				}
				else {
					User activeUser = service.create(username, password, firstName, lastName);
					JOptionPane.showMessageDialog(CreateUserPanel.this, "New user created.", "Success", JOptionPane.INFORMATION_MESSAGE);
					if( window != null ) {
						window.setActiveUser(activeUser);
						window.setMenuItemsEnabled(true);
						window.showView(View.QA);
					}
				}
			} catch (RestClientException e) {
				JOptionPane.showMessageDialog(CreateUserPanel.this, "Unable to connect to IVA Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(CreateUserPanel.this, 
						e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				Arrays.fill(password, '\0');
				Arrays.fill(password_1, '\0');
			}
		}
	}
	
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if( window != null ) {
				reset();
				window.showView(View.Login);
			}
		}
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			try {
				javax.swing.UIManager.setLookAndFeel(
						javax.swing.UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
			
			CreateUserPanel testPanel = new CreateUserPanel();
			
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
