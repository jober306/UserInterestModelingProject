package iva.client.swing.user.panels;

import iva.client.core.model.User;
import iva.client.core.services.UserService;
import iva.client.core.services.UserServiceHandler;
import iva.client.exceptions.AuthenticationException;
import iva.client.swing.ApplicationWindow;
import iva.client.swing.ApplicationWindow.View;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.web.client.RestClientException;

/**
 * The panel to log in to the system.
 * @author Kyle
 * @author Aron
 */
@SuppressWarnings("serial")
public class LoginPanel extends JPanel implements ActionListener {
	
	private final UserService service = new UserServiceHandler();
	
	private JTextField txtUsername;
	private JPasswordField passwordField;
	private JButton btnCreateUser;
	private JButton btnSignIn;
	private ApplicationWindow window;
	
	public LoginPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		JLabel lblTitle = new JLabel("<html><center>Welcome to the IVA User Profile System.<br> Please log in or create an account. </center></html>");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.gridx = 0;
		gbc_lblTitle.gridy = 0;
		add(lblTitle, gbc_lblTitle);
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 1;
		add(panel_3, gbc_panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_3.add(panel);
		
		JLabel lblUsername = new JLabel("Username");
		panel.add(lblUsername);
		
		txtUsername = new JTextField();
		txtUsername.addActionListener(this);
		panel.add(txtUsername);
		txtUsername.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_3.add(panel_1);
		
		JLabel lblPassword = new JLabel("Password");
		panel_1.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.addActionListener(this);
		passwordField.setColumns(10);
		panel_1.add(passwordField);
		
		JPanel panel_2 = new JPanel();
		
		btnCreateUser = new JButton("Create User");
		btnCreateUser.addActionListener(event -> {
			if( window != null ) {
				window.showView(View.CreateUser);
			}
		});
		panel_2.add(btnCreateUser);
		
		btnSignIn = new JButton("Sign In");
		btnSignIn.addActionListener(this);
		panel_2.add(btnSignIn);
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		add(panel_2, gbc_panel_2);
	}
	
	/**
	 * Initialize the LoginPanel layout and associate with ApplicationWindow.
	 * @param window the ApplicationWindow
	 */
	public LoginPanel(ApplicationWindow window) {
		this();
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String username = txtUsername.getText();
		char[] password = passwordField.getPassword();
		try {
			User activeUser = service.authenticate(username, password);
			if (window != null) {
				window.setActiveUser(activeUser);
				window.showView(View.QA);
				window.setMenuItemsEnabled(true);
			}
		} catch (AuthenticationException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Login Refused", JOptionPane.WARNING_MESSAGE);
		} catch (RestClientException e) {
			JOptionPane.showMessageDialog(this, "Unable to connect to IVA Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			Arrays.fill(password, '\0');
			reset();
		}
	}

	public ApplicationWindow getWindow() {
		return window;
	}

	public void setWindow(ApplicationWindow window) {
		this.window = window;
	}
	
	public void reset(){
		txtUsername.setText("");
		passwordField.setText("");
	}
	
	/* (non-Javadoc)
	 * Display the panel in a simple JFrame.
	 */
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			try {
				javax.swing.UIManager.setLookAndFeel(
						javax.swing.UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
			
			LoginPanel testPanel = new LoginPanel();
			
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
