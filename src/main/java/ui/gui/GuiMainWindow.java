package ui.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ui.text.TextInterpreter;

public class GuiMainWindow {

	private JFrame frame;
	private TextInterpreter cmd;
	private JTextField txtNetwork;
	private JTextField txtDataset;
	private JTextField txtOutput;

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
		
		frame = new JFrame();
		frame.setBounds(100, 100, 317, 178);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(
			ev -> {
				// TODO create popup or new window
			}
		);
		mnFile.add(mntmNew);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(
			ev -> {
				String name = getNetworkName(false);
				if (name != null) {
					try {
						cmd.execute(cmd.SAVE_NETWORK, name + ".kset");
					} catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(frame, "Could not save to file " + name + ".kset");
					}
				}
			}
		);
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.addActionListener(
			ev -> {
				String name = getNetworkName(true);
				if (name != null) {
					try {
						cmd.execute(cmd.SAVE_NETWORK, name + ".kset");
						txtNetwork.setText(name);
					} catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(frame, "Could not save to file " + name + ".kset");
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
						JOptionPane.showMessageDialog(frame, "Could not load file " + name + ".kset");
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
				// TODO show dialog with basic information
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
				try {
					cmd.execute(cmd.TRAIN_NETWORK, txtDataset.getText());
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
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
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
}
