package iva.client.swing.user.panels;

import iva.client.core.model.User;
import iva.client.core.services.UserService;
import iva.client.core.services.UserServiceHandler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class EditUserPanel extends JPanel {
	private static final long serialVersionUID = -1893453818328550482L;
	
	private JLabel lblUsername;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private final Action action = new ChangePasswordDialogAction();
	
	private final UserService userService;
	private User activeUser;
	
	public EditUserPanel() {
		this(new UserServiceHandler());
	}
	
	public EditUserPanel(UserService userService) {
		initializeGUI();
		this.userService = userService;
	}
	
	private void initializeGUI() {
		GridBagLayout gbl_panel = new GridBagLayout();
		this.setLayout(gbl_panel);
		
		JLabel label = new JLabel("Username:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		this.add(label, gbc_label);
		
		lblUsername = new JLabel("username");
		label.setLabelFor(lblUsername);
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.WEST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 0);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 0;
		this.add(lblUsername, gbc_lblUsername);
		
		JLabel label_2 = new JLabel("First Name:");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 1;
		this.add(label_2, gbc_label_2);
		
		txtFirstName = new JTextField("firstName");
		txtFirstName.setColumns(15);
		label_2.setLabelFor(txtFirstName);
		GridBagConstraints gbc_lblFirstName = new GridBagConstraints();
		gbc_lblFirstName.anchor = GridBagConstraints.WEST;
		gbc_lblFirstName.insets = new Insets(0, 0, 5, 0);
		gbc_lblFirstName.gridx = 1;
		gbc_lblFirstName.gridy = 1;
		this.add(txtFirstName, gbc_lblFirstName);
		
		JLabel label_4 = new JLabel("Last Name:");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.anchor = GridBagConstraints.EAST;
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 2;
		this.add(label_4, gbc_label_4);
		
		txtLastName = new JTextField("lastName");
		txtLastName.setColumns(15);
		label_4.setLabelFor(txtLastName);
		GridBagConstraints gbc_lblLastName = new GridBagConstraints();
		gbc_lblLastName.anchor = GridBagConstraints.WEST;
		gbc_lblLastName.insets = new Insets(0, 0, 5, 0);
		gbc_lblLastName.gridx = 1;
		gbc_lblLastName.gridy = 2;
		this.add(txtLastName, gbc_lblLastName);
		
		JButton btnChangePassword = new JButton();
		btnChangePassword.setAction(action);
		GridBagConstraints gbc_btnChangePassword = new GridBagConstraints();
		gbc_btnChangePassword.insets = new Insets(0, 0, 5, 0);
		gbc_btnChangePassword.gridx = 1;
		gbc_btnChangePassword.gridy = 3;
		add(btnChangePassword, gbc_btnChangePassword);
	}
	
	public void setUser(User activeUser) {
		this.activeUser = activeUser;
		
		lblUsername.setText(activeUser.getUsername());
		txtFirstName.setText(activeUser.getFirstName());
		txtLastName.setText(activeUser.getLastName());
	}
	
	public void saveChanges() {
		activeUser.setFirstName(txtFirstName.getText().trim());
		activeUser.setLastName(txtLastName.getText().trim());
		
		userService.update(activeUser);
	}

	private class ChangePasswordDialogAction extends AbstractAction {
		private static final long serialVersionUID = 1802114896146356331L;
		
		private JPanel panel;
		private JPasswordField txtOldPassword;
		private JPasswordField txtNewPassword;
		private JPasswordField txtConfirmPassword;
		
		public ChangePasswordDialogAction() {
			panel = new JPanel(new GridBagLayout());
			
			JLabel label = new JLabel("Old Password:");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = 0;
			panel.add(label, gbc_label);
			
			txtOldPassword = new JPasswordField();
			txtOldPassword.setColumns(15);
			label.setLabelFor(txtOldPassword);
			GridBagConstraints gbc_txtOldPassword = new GridBagConstraints();
			gbc_txtOldPassword.insets = new Insets(0, 0, 5, 0);
			gbc_txtOldPassword.anchor = GridBagConstraints.WEST;
			gbc_txtOldPassword.gridx = 1;
			gbc_txtOldPassword.gridy = 0;
			panel.add(txtOldPassword, gbc_txtOldPassword);
			
			JLabel label_1 = new JLabel("New Password:");
			GridBagConstraints gbc_label_1 = new GridBagConstraints();
			gbc_label_1.anchor = GridBagConstraints.EAST;
			gbc_label_1.insets = new Insets(0, 0, 5, 5);
			gbc_label_1.gridx = 0;
			gbc_label_1.gridy = 1;
			panel.add(label_1, gbc_label_1);
			
			txtNewPassword = new JPasswordField();
			txtNewPassword.setColumns(15);
			label.setLabelFor(txtNewPassword);
			GridBagConstraints gbc_txtNewPassword = new GridBagConstraints();
			gbc_txtNewPassword.insets = new Insets(0, 0, 5, 0);
			gbc_txtNewPassword.anchor = GridBagConstraints.WEST;
			gbc_txtNewPassword.gridx = 1;
			gbc_txtNewPassword.gridy = 1;
			panel.add(txtNewPassword, gbc_txtNewPassword);
			
			JLabel label_2 = new JLabel("Confirm Password:");
			GridBagConstraints gbc_label_2 = new GridBagConstraints();
			gbc_label_2.anchor = GridBagConstraints.EAST;
			gbc_label_2.insets = new Insets(0, 0, 5, 5);
			gbc_label_2.gridx = 0;
			gbc_label_2.gridy = 2;
			panel.add(label_2, gbc_label_2);
			
			txtConfirmPassword = new JPasswordField();
			txtConfirmPassword.setColumns(15);
			label.setLabelFor(txtConfirmPassword);
			GridBagConstraints gbc_txtConfirmPassword = new GridBagConstraints();
			gbc_txtConfirmPassword.insets = new Insets(0, 0, 5, 0);
			gbc_txtConfirmPassword.anchor = GridBagConstraints.WEST;
			gbc_txtConfirmPassword.gridx = 1;
			gbc_txtConfirmPassword.gridy = 2;
			panel.add(txtConfirmPassword, gbc_txtConfirmPassword);
			
			putValue(NAME, "Change Password...");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			int option = JOptionPane.showConfirmDialog(
					EditUserPanel.this, 
					panel, 
					"Change Password", 
					JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.PLAIN_MESSAGE );
			
			if(option == JOptionPane.OK_OPTION || activeUser != null) {
				char[] oldPassword = txtOldPassword.getPassword();
				char[] newPassword = txtNewPassword.getPassword();
				char[] newPassword_1 = txtConfirmPassword.getPassword();

				try {
					if(!Arrays.equals(newPassword, newPassword_1)) {
						throw new Exception("New passwords did not match");
					}
					userService.updatePassword(activeUser.getUsername(), oldPassword, newPassword);
					JOptionPane.showMessageDialog(
							panel, 
							"Your password has been changed", 
							"Password Change", 
							JOptionPane.INFORMATION_MESSAGE );
				} catch (Exception e) {
					JOptionPane.showMessageDialog(panel, 
							e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					Arrays.fill(oldPassword, '\0');
					Arrays.fill(newPassword, '\0');
					Arrays.fill(newPassword_1, '\0');
					txtOldPassword.setText("");
					txtNewPassword.setText("");
					txtConfirmPassword.setText("");
				}
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
			
			EditUserPanel testPanel = new EditUserPanel();
			
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
