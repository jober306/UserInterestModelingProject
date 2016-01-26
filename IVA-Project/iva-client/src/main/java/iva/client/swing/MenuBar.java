package iva.client.swing;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class MenuBar extends JMenuBar {
	private static final long serialVersionUID = -6213454895639562924L;
	
	private final JMenuItem mntmExit = new JMenuItem("Exit");
	private final JMenuItem mntmUserInfo = new JMenuItem("View Info");
	private final JMenuItem mntmSignOut = new JMenuItem("Sign Out");
	private final JMenuItem mntmAskQuestions = new JMenuItem("Ask Questions");
	private final JMenuItem mntmHistory = new JMenuItem("History");
	
	public MenuBar() {
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		mnFile.add(new JSeparator());
		mnFile.add(mntmExit);
		this.add(mnFile);
		
		JMenu mnUser = new JMenu("User");
		mnUser.setMnemonic(KeyEvent.VK_U);
		mnUser.add(mntmUserInfo);
		mnUser.add(new JSeparator());
		mnUser.add(mntmSignOut);
		this.add(mnUser);
		
		JMenu mnNavigate = new JMenu("Navigate");
		mnNavigate.setMnemonic(KeyEvent.VK_N);
		mnNavigate.add(mntmAskQuestions);
		mnNavigate.add(mntmHistory);
		this.add(mnNavigate);
	}

	public JMenuItem getExitMenuItem() {
		return mntmExit;
	}

	public JMenuItem getUserInfoMenuItem() {
		return mntmUserInfo;
	}

	public JMenuItem getSignOutMenuItem() {
		return mntmSignOut;
	}

	public JMenuItem getAskQuestionsMenuItem() {
		return mntmAskQuestions;
	}

	public JMenuItem getHistoryMenuItem() {
		return mntmHistory;
	}

}
