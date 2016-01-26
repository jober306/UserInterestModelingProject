package iva.client.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * General purpose dialog panel for allowing the user to select a file
 * and extract the lines in that file.
 * @author Aron
 */
@SuppressWarnings("serial")
public class FileInputPanel extends JPanel {
	
	private static final Logger log = Logger.getLogger(FileInputPanel.class);
	
	private final JTextField txtFilePath = new JTextField();
	private final JLabel lblPreview = new JLabel("Preview");
	
	private final DefaultListModel<String> listModelFileLines = new DefaultListModel<String>();
	private final JList<String> listFileLines = new JList<String>(listModelFileLines);
	
	private final Action actionBrowseFiles = new BrowseFilesAction(txtFilePath);
	private final Action actionPreviewFile = new PreviewFileAction();
	
	public FileInputPanel() {
		super();
		initializeGUI();
	}
	
	private void initializeGUI() {
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		this.add(panel, BorderLayout.NORTH);
		
		JLabel lblFile = new JLabel("File");
		panel.add(lblFile);
		
		txtFilePath.setColumns(25);
		panel.add(txtFilePath);
		
		JButton btnBrowse = new JButton();
		btnBrowse.setAction(actionBrowseFiles);
		panel.add(btnBrowse);
		
		JButton btnPreview = new JButton();
		btnPreview.setAction(actionPreviewFile);
		panel.add(btnPreview);
		
		JScrollPane scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(listFileLines);
		
		JPanel panel_1 = new JPanel();
		scrollPane.setColumnHeaderView(panel_1);
		
		panel_1.add(lblPreview);
	}

	public List<String> readSelectedFileByLine() {
		List<String> selectedFileLines = new ArrayList<String>();
		
		Path selectedFilePath = Paths.get(txtFilePath.getText());
		
		try {
			if(!selectedFilePath.isAbsolute()) {
				throw new NoSuchFileException(selectedFilePath.toString());
			}
			
			List<String> rawFileLines = Files.readAllLines(
					selectedFilePath, Charset.defaultCharset() );
			
			for(String line : rawFileLines) {
				// Clean up lines before adding to list
				line = line.trim();
				
				if( !line.isEmpty() ) {
					selectedFileLines.add(line);
				}
			}
		} catch (IOException e) {
			String message = "Unable to read file '"+selectedFilePath+"'";
			log.error(message, e);
			JOptionPane.showMessageDialog(this, message, e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
		}
		
		return selectedFileLines;
	}
	
	
	private class PreviewFileAction extends AbstractAction {
		public PreviewFileAction() {
			putValue(NAME, "Preview File");
			putValue(SHORT_DESCRIPTION, "List the lines in the selected file");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			Path selectedFilePath = Paths.get(txtFilePath.getText()).getFileName();
			lblPreview.setText("Preview of '"+selectedFilePath+"'");
			
			List<String> fileLines = readSelectedFileByLine();
			
			listModelFileLines.removeAllElements();
			if( fileLines.isEmpty() ) {
				listModelFileLines.addElement("The file is empty!");
			} else {
				for(String line : fileLines) {
					listModelFileLines.addElement(line);
				}
			}
		}
	}
	
	public static class BrowseFilesAction extends AbstractAction {
		private final JFileChooser fc = 
				new JFileChooser(new File("").getAbsoluteFile());
		
		private JTextField textField;
		
		public BrowseFilesAction() {
			putValue(NAME, "Browse...");
		}
		
		public BrowseFilesAction(JTextField textField) {
			this();
			setTextField(textField);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int option = fc.showOpenDialog(textField);
			
			if(option == JFileChooser.APPROVE_OPTION) {
				if( textField != null ) {
					File selectedFile = getSelectedFile();
					textField.setText(selectedFile.toString());
				}
			}
		}
		
		/**
		 * Returns the selected file.
		 * @return the selected file
		 * @see javax.swing.JFileChooser#getSelectedFile()
		 */
		public File getSelectedFile() {
			return fc.getSelectedFile();
		}

		/**
		 * Binds a text field to by this action. The action will then write the
		 * selected file path to the text field upon selection.
		 * @param textField the text field to set
		 */
		public void setTextField(JTextField textField) {
			this.textField = textField;
		}
	}

	public static void main(String[] args) {
		try {
			javax.swing.UIManager.setLookAndFeel(
					javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}
		
		FileInputPanel fileInputPanel = new FileInputPanel();
		
		int option = javax.swing.JOptionPane.showConfirmDialog( 
				null, 
				fileInputPanel , 
				"Input File", 
				javax.swing.JOptionPane.OK_CANCEL_OPTION, 
				javax.swing.JOptionPane.PLAIN_MESSAGE );
		
		System.out.println("Selected OK = "+(option == javax.swing.JOptionPane.OK_OPTION));
		for(String line : fileInputPanel.readSelectedFileByLine()) {
			System.out.println("  "+line);
		}
	}

}
