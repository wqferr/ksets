package ui;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MockupWindow {

	private JFrame frame;
	private JTextField txtOutName;
	private JTextField txtDatasetName;
	private JTextField txtNetworkName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MockupWindow window = new MockupWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MockupWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 481, 216);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 140, 142, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		gbc_lblNetwork.anchor = GridBagConstraints.WEST;
		gbc_lblNetwork.gridx = 1;
		gbc_lblNetwork.gridy = 1;
		frame.getContentPane().add(lblNetwork, gbc_lblNetwork);
		
		txtNetworkName = new JTextField();
		txtNetworkName.setEditable(false);
		GridBagConstraints gbc_txtNetworkName = new GridBagConstraints();
		gbc_txtNetworkName.insets = new Insets(0, 0, 5, 5);
		gbc_txtNetworkName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNetworkName.gridx = 2;
		gbc_txtNetworkName.gridy = 1;
		frame.getContentPane().add(txtNetworkName, gbc_txtNetworkName);
		txtNetworkName.setColumns(10);
		
		JButton btnSelectNetwork = new JButton("Select");
		GridBagConstraints gbc_btnSelectNetwork = new GridBagConstraints();
		gbc_btnSelectNetwork.insets = new Insets(0, 0, 5, 5);
		gbc_btnSelectNetwork.gridx = 3;
		gbc_btnSelectNetwork.gridy = 1;
		frame.getContentPane().add(btnSelectNetwork, gbc_btnSelectNetwork);
		
		JLabel lblDataset = new JLabel("Dataset:");
		GridBagConstraints gbc_lblDataset = new GridBagConstraints();
		gbc_lblDataset.insets = new Insets(0, 0, 5, 5);
		gbc_lblDataset.anchor = GridBagConstraints.WEST;
		gbc_lblDataset.gridx = 1;
		gbc_lblDataset.gridy = 2;
		frame.getContentPane().add(lblDataset, gbc_lblDataset);
		
		txtDatasetName = new JTextField();
		txtDatasetName.setEditable(false);
		GridBagConstraints gbc_txtDatasetName = new GridBagConstraints();
		gbc_txtDatasetName.insets = new Insets(0, 0, 5, 5);
		gbc_txtDatasetName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDatasetName.gridx = 2;
		gbc_txtDatasetName.gridy = 2;
		frame.getContentPane().add(txtDatasetName, gbc_txtDatasetName);
		txtDatasetName.setColumns(10);
		
		JButton btnSelectDataset = new JButton("Select");
		GridBagConstraints gbc_btnSelectDataset = new GridBagConstraints();
		gbc_btnSelectDataset.insets = new Insets(0, 0, 5, 5);
		gbc_btnSelectDataset.gridx = 3;
		gbc_btnSelectDataset.gridy = 2;
		frame.getContentPane().add(btnSelectDataset, gbc_btnSelectDataset);
		
		JLabel lblOutputFile = new JLabel("Output file:");
		GridBagConstraints gbc_lblOutputFile = new GridBagConstraints();
		gbc_lblOutputFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutputFile.anchor = GridBagConstraints.WEST;
		gbc_lblOutputFile.gridx = 1;
		gbc_lblOutputFile.gridy = 3;
		frame.getContentPane().add(lblOutputFile, gbc_lblOutputFile);
		
		txtOutName = new JTextField();
		txtOutName.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOutName.setText("out.dat");
		GridBagConstraints gbc_txtOutName = new GridBagConstraints();
		gbc_txtOutName.gridwidth = 2;
		gbc_txtOutName.insets = new Insets(0, 0, 5, 5);
		gbc_txtOutName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtOutName.gridx = 2;
		gbc_txtOutName.gridy = 3;
		frame.getContentPane().add(txtOutName, gbc_txtOutName);
		txtOutName.setColumns(10);
		
		JButton btnRun = new JButton("Run");
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.anchor = GridBagConstraints.EAST;
		gbc_btnRun.insets = new Insets(0, 0, 5, 5);
		gbc_btnRun.gridx = 3;
		gbc_btnRun.gridy = 4;
		frame.getContentPane().add(btnRun, gbc_btnRun);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		GridBagConstraints gbc_rigidArea_1 = new GridBagConstraints();
		gbc_rigidArea_1.gridx = 4;
		gbc_rigidArea_1.gridy = 5;
		frame.getContentPane().add(rigidArea_1, gbc_rigidArea_1);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnNetwork = new JMenu("Network");
		mnFile.add(mnNetwork);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mnNetwork.add(mntmNew);
		
		JMenuItem mntmEdit = new JMenuItem("Edit");
		mnNetwork.add(mntmEdit);
		
		JMenuItem mntmTrain = new JMenuItem("Train");
		mnNetwork.add(mntmTrain);
		
		JMenu mnDatasets = new JMenu("Datasets");
		mnFile.add(mnDatasets);
		
		JMenuItem mntmNew_1 = new JMenuItem("New");
		mnDatasets.add(mntmNew_1);
		
		JMenuItem mntmEdit_1 = new JMenuItem("Edit");
		mnDatasets.add(mntmEdit_1);
	}

}
