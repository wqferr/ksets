package ui.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ui.text.TextInterpreter;

public class GuiMainWindow {

	private JFrame frame;
	private TextInterpreter cmd;
	
	public JTextField txtNetwork;
	private JTextField txtDataset;
	private JTextField txtOutput;
	
	private JTextField[] txtCreateNetworkLayers;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiMainWindow window = new GuiMainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					System.err.printf("Error initializing window: %s", e.getMessage());
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiMainWindow() {
		initialize();
	}

	
	private String getNetworkName(boolean forcePopup) {
		String name = "";
		
		if (!forcePopup)
			name = txtNetwork.getText();
		
		while (name != null && name.isEmpty())
			name = JOptionPane.showInputDialog("Enter network name");
		
		return name;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		cmd = new TextInterpreter();
		
		frame = new JFrame("Freeman's KIII");
		frame.setBounds(100, 100, 317, 178);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(
			ev -> {
				int result = showCreateNetworkDialog();
				
				if (result == JOptionPane.OK_OPTION) {
					StringBuilder bld = new StringBuilder(cmd.NEW_NETWORK.toString()).append(' ');
					for (JTextField txt : txtCreateNetworkLayers)
						bld.append(txt.getText()).append(' ');
					
					cmd.execute(bld.toString());
				}
			}
		);
		mnFile.add(mntmNew);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(
			ev -> {
				if (checkKsetLoaded()) {
					String name = getNetworkName(false);
					if (name != null) {
						try {
							cmd.execute(cmd.SAVE_NETWORK, name + ".kset");
							txtNetwork.setText(name);
						} catch (IllegalArgumentException ex) {
							JOptionPane.showMessageDialog(frame, "Could not save to file " + name + ".kset\n" + ex.getMessage());
						}
					}
				}
			}
		);
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.addActionListener(
			ev -> {
				if (checkKsetLoaded()) {
					String name = getNetworkName(true);
					if (name != null) {
						try {
							cmd.execute(cmd.SAVE_NETWORK, name + ".kset");
							txtNetwork.setText(name);
						} catch (IllegalArgumentException ex) {
							JOptionPane.showMessageDialog(frame, "Could not save to file " + name + ".kset\n" + ex.getMessage());
						}
					}
				}
			}
		);
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmLoad = new JMenuItem("Load");
		mntmLoad.addActionListener(
			ev -> {
				String name = getNetworkName(true);
				if (name != null) {
					try {
						cmd.execute(cmd.LOAD_NETWORK, name + ".kset");
						txtNetwork.setText(name);
					} catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(frame, "Could not load file " + name + ".kset\n" + ex.getMessage());
					}
				}
			}
		);
		mnFile.add(mntmLoad);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenuItem mntmViewNetwork = new JMenuItem("Network");
		mntmViewNetwork.addActionListener(
			ev -> {
				if (checkKsetLoaded())
					showNetworkDisplayDialog();
			}
		);
		mnView.add(mntmViewNetwork);
		
		JMenuItem mntmViewDataset = new JMenuItem("Dataset");
		mntmViewDataset.addActionListener(
			ev -> {
				// TODO show dialog with dataset
			}
		);
		mnView.add(mntmViewDataset);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(
			ev -> {
				// TODO open help window
			}
		);
		menuBar.add(mntmHelp);
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		GridBagConstraints gbc_rigidArea = new GridBagConstraints();
		gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
		gbc_rigidArea.gridx = 0;
		gbc_rigidArea.gridy = 0;
		frame.getContentPane().add(rigidArea, gbc_rigidArea);
		
		JLabel lblNetwork = new JLabel("Network:");
		GridBagConstraints gbc_lblNetwork = new GridBagConstraints();
		gbc_lblNetwork.insets = new Insets(0, 0, 5, 5);
		gbc_lblNetwork.anchor = GridBagConstraints.EAST;
		gbc_lblNetwork.gridx = 1;
		gbc_lblNetwork.gridy = 1;
		frame.getContentPane().add(lblNetwork, gbc_lblNetwork);
		
		txtNetwork = new JTextField();
		txtNetwork.setHorizontalAlignment(SwingConstants.RIGHT);
		txtNetwork.setEditable(false);
		GridBagConstraints gbc_txtNetwork = new GridBagConstraints();
		gbc_txtNetwork.insets = new Insets(0, 0, 5, 5);
		gbc_txtNetwork.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNetwork.gridx = 2;
		gbc_txtNetwork.gridy = 1;
		frame.getContentPane().add(txtNetwork, gbc_txtNetwork);
		txtNetwork.setColumns(10);
		
		JLabel lblDataset = new JLabel("Dataset:");
		GridBagConstraints gbc_lblDataset = new GridBagConstraints();
		gbc_lblDataset.anchor = GridBagConstraints.EAST;
		gbc_lblDataset.insets = new Insets(0, 0, 5, 5);
		gbc_lblDataset.gridx = 1;
		gbc_lblDataset.gridy = 2;
		frame.getContentPane().add(lblDataset, gbc_lblDataset);
		
		txtDataset = new JTextField();
		txtDataset.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_txtDataset = new GridBagConstraints();
		gbc_txtDataset.insets = new Insets(0, 0, 5, 5);
		gbc_txtDataset.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDataset.gridx = 2;
		gbc_txtDataset.gridy = 2;
		frame.getContentPane().add(txtDataset, gbc_txtDataset);
		txtDataset.setColumns(10);
		
		JButton btnTrain = new JButton("Train");
		btnTrain.addActionListener(
			ev -> {
				if (checkKsetLoaded()) {
					try {
						cmd.execute(cmd.TRAIN_NETWORK, txtDataset.getText());
					} catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
					}
				}
			}
		);
		GridBagConstraints gbc_btnTrain = new GridBagConstraints();
		gbc_btnTrain.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnTrain.insets = new Insets(0, 0, 5, 5);
		gbc_btnTrain.gridx = 3;
		gbc_btnTrain.gridy = 2;
		frame.getContentPane().add(btnTrain, gbc_btnTrain);
		
		JLabel lblOutput = new JLabel("Output:");
		GridBagConstraints gbc_lblOutput = new GridBagConstraints();
		gbc_lblOutput.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutput.anchor = GridBagConstraints.EAST;
		gbc_lblOutput.gridx = 1;
		gbc_lblOutput.gridy = 3;
		frame.getContentPane().add(lblOutput, gbc_lblOutput);
		
		txtOutput = new JTextField();
		txtOutput.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOutput.setText("out.csv");
		GridBagConstraints gbc_txtOutput = new GridBagConstraints();
		gbc_txtOutput.insets = new Insets(0, 0, 5, 5);
		gbc_txtOutput.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtOutput.gridx = 2;
		gbc_txtOutput.gridy = 3;
		frame.getContentPane().add(txtOutput, gbc_txtOutput);
		txtOutput.setColumns(10);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(
			ev -> {
				if (checkKsetLoaded()) {
					try {
						cmd.execute(cmd.RUN_NETWORK, txtDataset.getText(), txtOutput.getText());
					} catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
					}
				}
			}
		);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.insets = new Insets(0, 0, 5, 5);
		gbc_btnRun.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRun.gridx = 3;
		gbc_btnRun.gridy = 3;
		frame.getContentPane().add(btnRun, gbc_btnRun);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		GridBagConstraints gbc_rigidArea_1 = new GridBagConstraints();
		gbc_rigidArea_1.gridx = 4;
		gbc_rigidArea_1.gridy = 4;
		frame.getContentPane().add(rigidArea_1, gbc_rigidArea_1);
		
		txtCreateNetworkLayers = new JTextField[3];
		for (int i = 0; i < txtCreateNetworkLayers.length; i++)
			txtCreateNetworkLayers[i] = new JTextField(5);
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
	
	private int showCreateNetworkDialog() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		
		for (int i = 0; i < txtCreateNetworkLayers.length; i++) {
			txtCreateNetworkLayers[i].setText("");
			
			panel.add(new JLabel("Layer " + i + " size:"));
			panel.add(txtCreateNetworkLayers[i]);
		}
		
		return JOptionPane.showConfirmDialog(
			frame, panel,
			"Enter layer sizes", JOptionPane.OK_CANCEL_OPTION
		);
	}
	
	private void showNetworkDisplayDialog() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		
		for (int i = 0; i < txtCreateNetworkLayers.length; i++) {
			panel.add(
				new JLabel(
					String.format("Layer %d:%s", i, i == cmd.kset.getOutputLayer() ? " [OUTPUT LAYER]" : "")
				)
			);
			panel.add(new JLabel("Id: " + cmd.kset.k3[i].getId()));
			panel.add(new JLabel("Size: " + cmd.kset.k3[i].getSize()));
			panel.add(new JLabel("Learning rate: " + cmd.kset.k3[i].getLearningRate()));
			
			if (i != txtCreateNetworkLayers.length-1)
				panel.add(new JLabel(""));
		}
		
		JOptionPane.showMessageDialog(frame, panel);
	}
	
	private boolean checkKsetLoaded() {
		if (cmd.kset != null)
			return true;

		JOptionPane.showMessageDialog(frame, "No kset loaded");
		return false;
	}
}
