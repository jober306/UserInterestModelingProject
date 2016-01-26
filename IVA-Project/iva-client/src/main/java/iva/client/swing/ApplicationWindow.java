package iva.client.swing;

import iva.client.core.model.User;
import iva.client.swing.qa.panels.QAPanel;
import iva.client.swing.qa.panels.QuestionHistoryPanel;
import iva.client.swing.user.panels.CreateUserPanel;
import iva.client.swing.user.panels.LoginPanel;
import iva.client.swing.user.panels.UserDialogPanel;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * The entry point of the program that creates and displays the main JFrame.
 * Swing components contained in the JFrame are also initialized.
 * @author Aron
 * @author Kyle
 */
public class ApplicationWindow implements Runnable {
	
	public enum View {
		QA,
		Login,
		CreateUser,
		UserHistory
	}
	
	public static final String DefaultTitle = "IVA Client";
	
	private MenuBar menuBar;
	private UserDialogPanel userInfoDialog;
	
	private JFrame frame;
	private QAPanel panelQA;
	private LoginPanel panelLogin;
	private CreateUserPanel panelCreateUser;
	private QuestionHistoryPanel panelUserHistory;
	
	private View currentView;
	private User activeUser;
	
	@Override
	public void run() {
		if(!EventQueue.isDispatchThread()) {
			System.err.println("ApplicationWindow not invoked from EDT.");
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			System.err.println("Error setting native LAF");
		}
		
		frame = new JFrame(DefaultTitle);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(800, 600));
		
		initMenuBar();
		initCardLayout();
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void initMenuBar() {
		menuBar = new MenuBar();
		userInfoDialog = new UserDialogPanel();
		
		menuBar.getExitMenuItem().addActionListener(event -> frame.dispose());
		
		menuBar.getUserInfoMenuItem().addActionListener(event -> {
			userInfoDialog.setUser(activeUser);
			
			int option = JOptionPane.showConfirmDialog(
					frame, 
					userInfoDialog, 
					"Properties for "+activeUser.getUsername(), 
					JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.PLAIN_MESSAGE );
			
			if(option == JOptionPane.OK_OPTION) {
				userInfoDialog.saveChanges();
			}
		});
		menuBar.getSignOutMenuItem().addActionListener(event -> {
			resetAll();
			setActiveUser(null);
			setMenuItemsEnabled(false);
			showView(View.Login);
		});
		menuBar.getAskQuestionsMenuItem().addActionListener(event -> {
			if(getCurrentView() != View.QA) {
				showView(View.QA);
			}
		});
		menuBar.getHistoryMenuItem().addActionListener(event -> {
			if(getCurrentView() != View.UserHistory) {
				panelUserHistory.loadUser(activeUser);
				showView(View.UserHistory);
			}
		});
		
		frame.setJMenuBar(menuBar);
		setMenuItemsEnabled(false);
	}
	
	private void initCardLayout() {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new CardLayout(0, 0));

		panelQA = new QAPanel(this);
		contentPane.add(panelQA, View.QA.toString());

		panelLogin = new LoginPanel(this);
		contentPane.add(panelLogin, View.Login.toString());

		panelCreateUser = new CreateUserPanel(this);
		contentPane.add(panelCreateUser, View.CreateUser.toString());

		panelUserHistory = new QuestionHistoryPanel();
		contentPane.add(panelUserHistory, View.UserHistory.toString());
		
		// Set the initial panel
		showView(View.Login);
	}
	
	/**
	 * Change the displayed panel.
	 * @param view the panel to display
	 */
	public void showView(View view) {
		Container contentPane = frame.getContentPane();
		CardLayout cl = (CardLayout) contentPane.getLayout();
		cl.show(contentPane, view.toString());
		currentView = view;
	}
	
	public View getCurrentView() {
		return currentView;
	}
	
	public User getActiveUser() {
		return activeUser;
	}
	
	public void setActiveUser(User activeUser) {
		this.activeUser = activeUser;
		
		// Update title bar to reflect the active user
		if(activeUser != null) {
			frame.setTitle( DefaultTitle + ": Logged in as " + activeUser.getUsername() );
		} else {
			frame.setTitle( DefaultTitle );
		}
	}
	
	public void setMenuItemsEnabled(boolean enabled){
		menuBar.getExitMenuItem().setEnabled(true);
		menuBar.getUserInfoMenuItem().setEnabled(enabled);
		menuBar.getSignOutMenuItem().setEnabled(enabled);
		menuBar.getAskQuestionsMenuItem().setEnabled(enabled);
		menuBar.getHistoryMenuItem().setEnabled(enabled);
	}

	public void resetAll() {
		panelQA.reset();
		panelLogin.reset();
		panelCreateUser.reset();
		panelUserHistory.reset();
	}
	
	// Preview
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new ApplicationWindow());
	}
}
