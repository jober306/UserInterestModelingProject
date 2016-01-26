package iva.client.swing.qa.panels;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class ReportPanel extends JPanel {
	
	private AnswerDetailsHtmlPane answerDetailsPane;
	
	public ReportPanel() {
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(600, 500));
		
		// Make the parent dialog resizable
		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent arg0) {
				Window window = SwingUtilities.getWindowAncestor(ReportPanel.this);
				if(window instanceof Dialog) {
					Dialog dialog = (Dialog) window;
					if(!dialog.isResizable()) {
						dialog.setResizable(true);
					}
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		answerDetailsPane = new AnswerDetailsHtmlPane();
		scrollPane.setViewportView(answerDetailsPane);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		add(toolBar, BorderLayout.NORTH);
		
		JButton btnSaveReport = new JButton(new SaveReportAction());
		toolBar.add(btnSaveReport);
	}
	
	public String getPlainText() {
		String htmlText = getHtmlText();
		
		Matcher m = Pattern.compile("(?s)<body>(.*)</body>").matcher(htmlText);
		m.find();
		String htmlBody = m.group(1);
		
		StringBuilder sb = new StringBuilder();
		String[] lines = htmlBody.split("\n");
		for(String line : lines) {
			line = line.trim();
			sb.append(line);
		}
		String text = sb.toString();
		
		text = text.replaceAll("<(br|/?p|/?h\\d|/tr|table)>", System.getProperty("line.separator"));
		text = text.replaceAll("</(td><td|th><th)>", "|");
		text = text.replaceAll("<.*?>", "");
		
		return text.trim();
	}
	
	public String getHtmlText() {
		return answerDetailsPane.getText();
	}
	
	public void setHtmlText(String t) {
		answerDetailsPane.setText(t);
	}
	
	private class SaveReportAction extends AbstractAction {
		private final JFileChooser fc = 
				new JFileChooser(new File("").getAbsoluteFile());
		
		private final FileFilter txtFileFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Text Files (*.txt)";
			}
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
			        return true;
			    }
			    String extension = getExtension(f);
			    if (extension != null && extension.equals("txt")) {
			        return true;
			    }
			    return false;
			}
		};
		private final FileFilter htmlFileFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "HTML Files (*.html)";
			}
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
			        return true;
			    }
			    String extension = getExtension(f);
			    if (extension != null && extension.equals("html")) {
			        return true;
			    }
			    return false;
			}
		};
		
		public SaveReportAction() {
			super("Save As...");
			
			// Add file filters
			fc.addChoosableFileFilter(txtFileFilter);
			fc.addChoosableFileFilter(htmlFileFilter);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			int option = fc.showSaveDialog(ReportPanel.this);
			
			if(option == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				if(fc.getFileFilter().equals(txtFileFilter)) {
					file = changeExtension(file, "txt");
					saveReport(ReportPanel.this.getPlainText(), file);
				} else if(fc.getFileFilter().equals(htmlFileFilter)) {
					file = changeExtension(file, "html");
					saveReport(ReportPanel.this.getHtmlText(), file);
				} else {
					saveReport(ReportPanel.this.getPlainText(), file);
				}
			}
		}
		
		private void saveReport(String report, File dest) {
			try(BufferedWriter writer = 
					Files.newBufferedWriter(dest.toPath(), Charset.defaultCharset()) )
			{
				writer.write(report);
			} catch (IOException e) {
				throw new RuntimeException("Error saving report to "+dest.getName(), e);
			}
		}
		
		private File changeExtension(File file, String newExt) {
			String s = file.getPath();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length()) {
	            String name = s.substring(0, i+1);
	        	String ext = s.substring(i+1).toLowerCase();
	        	
	            if(ext.equals(newExt)) {
	            	return file;
	            } else {
	            	return new File(name + newExt);
	            }
	        } else {
	        	return new File(s+"."+newExt);
	        }
		}
		
		private String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length()) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	}
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					javax.swing.UIManager.setLookAndFeel(
							javax.swing.UIManager.getSystemLookAndFeelClassName());
				} catch(Exception e) {
					System.out.println("Error setting native LAF: " + e);
				}
				
				ReportPanel panel = new ReportPanel();
				
				panel.setHtmlText("<p><b>Thu Aug 21 19:32:59 EDT 2014 Report for testuser</b></p><p><b>User Specified Categories (Min Score = 1.0)</b></p><p><b>Initial User Categories</b></p><table></table>"
						+ "<p><b>1A. Question</b><br>Who founded International Management Group (IMG)?</p>"
						+ "<table><tr><th>1B. Question Categories</th><th>Score</th><th>Source</th></tr></table>"
						+ "<table><tr><th>1C. User Categories</th><th>Before</th><th>After</th></tr></table>"
						+ "<p><b>2A. Question</b><br>In what year was International Management Group (IMG) founded?</p>"
						+ "<table><tr><th>2B. Question Categories</th><th>Score</th><th>Source</th></tr><tr><td>Orders_of_magnitude_(time)</td><td>+6.957440307302141</td><td>Question</td></tr><tr><td>Units_of_time</td><td>+6.957440307302141</td><td>Question</td></tr></table>"
						+ "<table><tr><th>2C. User Categories</th><th>Before</th><th>After</th></tr><tr><td>Orders_of_magnitude_(time)</td><td>0.0</td><td>6.957440307302141</td></tr><tr><td>Units_of_time</td><td>0.0</td><td>6.957440307302141</td></tr></table>"
						);
				
				System.out.println(panel.getPlainText());
				
				javax.swing.JOptionPane.showMessageDialog(
						null, 
						panel, 
						"QA Report", 
						javax.swing.JOptionPane.PLAIN_MESSAGE );
			}
		});
	}
	
}
