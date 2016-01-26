package iva.client.swing.user.panels;

import iva.client.core.model.InterestModel;
import iva.client.core.services.InterestModelService;
import iva.client.core.services.InterestModelServiceHandler;
import iva.client.swing.FileInputPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;

public class ExplicitCategoryPanel extends JPanel {
	private static final long serialVersionUID = -4221916596378648002L;
	
	private final JTextArea txtrExplicitCategoryInput = new JTextArea();
	private final JSpinner spnrScore = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 1.0));
	private final ExplicitCategoryMappingPanel mappingPanel = new ExplicitCategoryMappingPanel();
	private final Action actionFileInput = new FileInputAction();
	
	private SwingWorker<Void, Void> mappingTask;
	private ProgressMonitor monitor;
	
	private final InterestModelService modelService;
	private InterestModel model;
	
	public ExplicitCategoryPanel() {
		this(new InterestModelServiceHandler());
	}
	
	public ExplicitCategoryPanel(InterestModelService modelService) {
		initializeGUI();
		this.modelService = modelService;
	}
	
	private void initializeGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		txtrExplicitCategoryInput.setLineWrap(true);
		txtrExplicitCategoryInput.setWrapStyleWord(true);
		scrollPane.setViewportView(txtrExplicitCategoryInput);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		
		JLabel lblExplicitCategoryInput = new JLabel("Explicit Category Input (line deliminated)");
		panel_2.add(lblExplicitCategoryInput);
		lblExplicitCategoryInput.setLabelFor(txtrExplicitCategoryInput);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panel.add(horizontalGlue);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		
		JLabel lblScore = new JLabel("Score");
		panel_1.add(lblScore);
		
		((JSpinner.DefaultEditor) spnrScore.getEditor()).getTextField().setColumns(3);
		lblScore.setLabelFor(spnrScore);
		panel_1.add(spnrScore);
		
		JButton btnFileInput = new JButton();
		panel_1.add(btnFileInput);
		btnFileInput.setAction(actionFileInput);
	}
	
	public void setInterestModel(InterestModel model) {
		this.model = model;
		
		Set<String> explicitCategories = model.getLongTermCategories().keySet();
		StringBuilder explicitCategoryText = new StringBuilder();
		for(String category : explicitCategories) {
			explicitCategoryText.append(category+"\n");
		}
		txtrExplicitCategoryInput.setText(explicitCategoryText.toString());
	}
	
	public void saveChanges() {
		if(mappingTask != null && !mappingTask.isDone()) {
			// Prevent the user from interrupting a running task
			JOptionPane.showMessageDialog(this, 
					"A category mapping task is still running.\n"
					+ "Try again after it is finished.", 
					"Category Changes Not Saved", 
					JOptionPane.WARNING_MESSAGE );
			return;
		}
		
		Set<String> inputs = getInputs();
		Double score = (Double) spnrScore.getValue();
		Map<String, Double> explicitCategories = model.getLongTermCategories();
		
		// Input mappings to display in dialog
		Map<String, String> categoryMappings = new HashMap<>();
		
		// Remove categories that were deleted from input list
		explicitCategories.keySet().stream()
		.filter(category -> !inputs.contains(category))
		.collect(Collectors.toSet())
		.forEach(deletedCategory -> {
			explicitCategories.remove(deletedCategory);
			categoryMappings.put(deletedCategory, "[Deleted]");
		});
		
		Set<String> newInputs = inputs.stream()
				.filter(input -> !explicitCategories.containsKey(input))
				.collect(Collectors.toSet());
		
		monitor = new ProgressMonitor(this, "Mapping inputs to categories", "", 0, newInputs.size());
		monitor.setMillisToDecideToPopup(0);
		monitor.setMillisToPopup(0);
		
		mappingTask = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				for(String input : newInputs)
				{
					fireMonitorEvent(input);
					String category = modelService.mapToCategory(input);
					
					if( category != null ) {
						if(!explicitCategories.containsKey(category)) {
							explicitCategories.put(category, score);
						} else {
							category += " [already exists]";
						}
					} else {
						category = "[No mapping found!]";
					}
					categoryMappings.put(input, category);
				}
				return null;
			}
			@Override
			protected void done() {
				modelService.update(model);
				monitor.close();
				// Display message dialog of mapped categories
				if(!categoryMappings.isEmpty()) {
					mappingPanel.setCategoryMappings(categoryMappings);
					JOptionPane.showMessageDialog(
							ExplicitCategoryPanel.this, 
							mappingPanel, 
							"Changes Made to Explicit Categories", 
							JOptionPane.PLAIN_MESSAGE );
				}
			}
			private int count = 0;
			private void fireMonitorEvent(String input) {
				firePropertyChange("mapping", input, count++);
			}
		};
		mappingTask.addPropertyChangeListener(event -> {
			if(monitor.isCanceled()) {
				mappingTask.cancel(true);
			} else if(event.getPropertyName().equals("mapping")) {
				String input = (String) event.getOldValue();
				int count = (Integer) event.getNewValue();
				int total = newInputs.size();
				monitor.setProgress(count);
				monitor.setNote(String.format("Mapping input %s of %s: %s.", count + 1, total, input));
				System.out.println(monitor.getNote());
			}
		});
		mappingTask.execute();
	}
	
	public Set<String> getInputs() {
		String[] inputs = txtrExplicitCategoryInput.getText().split("\n");
		return Arrays.stream(inputs)
				.map(input -> input.trim())
				.filter(input -> !input.isEmpty())
				.collect(Collectors.toSet());
	}
	
	private class FileInputAction extends AbstractAction {
		private static final long serialVersionUID = -8564788756467732059L;
		
		private final FileInputPanel fileInputPanel = new FileInputPanel();
		
		public FileInputAction() {
			putValue(NAME, "From File...");
			putValue(SHORT_DESCRIPTION, "Adds categories from a line deliminated text file");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog( 
					ExplicitCategoryPanel.this, 
					fileInputPanel, 
					"Input Question File", 
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE );
			
			if(option == JOptionPane.OK_OPTION) 
			{
				List<String> explicitCategoryList = fileInputPanel.readSelectedFileByLine();
				StringBuilder explicitCategoryText = new StringBuilder(txtrExplicitCategoryInput.getText());
				for(String category : explicitCategoryList) {
					explicitCategoryText.append(category+"\n");
				}
				txtrExplicitCategoryInput.setText(explicitCategoryText.toString());
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
			
			ExplicitCategoryPanel panel = new ExplicitCategoryPanel();
			panel.setPreferredSize(new java.awt.Dimension(400,300));
			panel.setInterestModel(new InterestModel());
			
			int option = javax.swing.JOptionPane.showConfirmDialog( 
					null, 
					panel, 
					"Info for testuser", 
					javax.swing.JOptionPane.OK_CANCEL_OPTION, 
					javax.swing.JOptionPane.PLAIN_MESSAGE );
			
			if(option == javax.swing.JOptionPane.OK_OPTION) {
				panel.saveChanges();
			}
		});
	}
}
