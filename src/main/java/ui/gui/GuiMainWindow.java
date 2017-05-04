package ui.gui;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jkset.DataIO;
import ui.text.TextInterpreter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class GuiMainWindow {

	//<editor-fold desc="UI persistent components">
	private JFrame frame;
	private TextInterpreter cmd;
	
	private JTextField txtNetwork;
	private JTextField txtDataset;
	private JTextField txtOutput;
	
	private JTextField[] txtParamEdit;
	private JTextField[] txtCreateNetworkLayers;
	//</editor-fold>

	/*
	Workaround because of poor Swing internal architecture
	Without this, the JOptionPane will always focus the button
	first, regardless of invokeLater and requestFocus
	*/
	private static final HierarchyListener requestFocusOnShow = e -> {
		final Component c = e.getComponent();
		if (c.isShowing() && (e.getChangeFlags() &
				HierarchyEvent.SHOWING_CHANGED) != 0) {
			Window toplevel = SwingUtilities.getWindowAncestor(c);
			toplevel.addWindowFocusListener(new WindowAdapter() {
				public void windowGainedFocus(WindowEvent e) {
					c.requestFocus();
				}
			});
		}
	};

	public static void main(String[] args) {
		EventQueue.invokeLater(
			() -> {
				try {
					GuiMainWindow window = new GuiMainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					System.err.printf("Error initializing window: %s", e.getMessage());
				}
			}
		);
	}

	private GuiMainWindow() {
		initialize();
	}

	@NotNull
	private String getNetworkName(boolean forcePopup) {
		String name = "";
		
		if (!forcePopup)
			name = txtNetwork.getText();
		
		while (name != null && name.isEmpty())
			name = JOptionPane.showInputDialog("Enter network name");
		
		return name;
	}

	private void initialize() {
		cmd = new TextInterpreter();
		//<editor-fold desc="Frame init">
		frame = new JFrame("Freeman's KIII");
		frame.setBounds(100, 100, 320, 180);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//</editor-fold>
		//<editor-fold desc="Menu bar init">
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(this::showCreateNetworkDialog);
		mntmNew.setAccelerator(
            KeyStroke.getKeyStroke(
				KeyEvent.VK_N,
				KeyEvent.CTRL_MASK
			)
		);
		mnFile.add(mntmNew);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(ev -> trySave(false));
		mntmSave.setAccelerator(
            KeyStroke.getKeyStroke(
				KeyEvent.VK_S,
				KeyEvent.CTRL_MASK
            )
		);
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.addActionListener(ev -> trySave(true));
		mntmSaveAs.setAccelerator(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_S,
                KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK
            )
		);
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmLoad = new JMenuItem("Load");
		mntmLoad.addActionListener(this::showLoadNetworkDialog);
		mntmLoad.setAccelerator(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                KeyEvent.CTRL_MASK
            )
		);
		mnFile.add(mntmLoad);
		
		JMenu mnEdit = new JMenu("Edit");
		mnEdit.setMnemonic(KeyEvent.VK_E);
		menuBar.add(mnEdit);
		
		JMenuItem mntmSetParam = new JMenuItem("Set parameter");
		mntmSetParam.addActionListener(this::showSetParamDialog);
		mntmSetParam.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_P,
				KeyEvent.CTRL_MASK
			)
		);
		mnEdit.add(mntmSetParam);

		JMenuItem mntmTrain = new JMenuItem("Train");
		mntmTrain.addActionListener(this::showTrainingDialog);
		mntmTrain.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_T,
				KeyEvent.CTRL_MASK
			)
		);
		mnEdit.add(mntmTrain);
		
		JMenu mnView = new JMenu("View");
		mnView.setMnemonic(KeyEvent.VK_V);
		menuBar.add(mnView);
		
		JMenuItem mntmViewNetwork = new JMenuItem("Network");
		mntmViewNetwork.addActionListener(this::showNetworkDisplayDialog);
		mntmViewNetwork.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_N,
				KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK
			)
		);
		mnView.add(mntmViewNetwork);
		
		JMenuItem mntmViewDataset = new JMenuItem("Dataset");
		mntmViewDataset.addActionListener(this::showDatasetDisplayDialog);
		mntmViewDataset.setAccelerator(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_D,
                KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK
            )
		);
		mnView.add(mntmViewDataset);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		menuBar.add(mnHelp);

		JMenuItem mntmReference = new JMenuItem("Reference");
		mntmReference.addActionListener(this::showReferences);
		mnHelp.add(mntmReference);

		//</editor-fold>
		//<editor-fold desc="Frame component layout">
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
		gbc_lblNetwork.insets = new Insets(0, 0, 5, 30);
		gbc_lblNetwork.anchor = GridBagConstraints.WEST;
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
		
		JLabel lblDataset = new JLabel("Input:");
		GridBagConstraints gbc_lblDataset = new GridBagConstraints();
		gbc_lblDataset.anchor = GridBagConstraints.WEST;
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
		
		/*JButton btnTrain = new JButton("Train");
		btnTrain.addActionListener(
			ev -> {
				if (checkKsetLoaded()) {
					try {
						cmd.TRAIN_NETWORK.execute(txtDataset.getText());
					} catch (IllegalArgumentException ex) {
						showError(ex);
					}
				}
			}
		);
		GridBagConstraints gbc_btnTrain = new GridBagConstraints();
		gbc_btnTrain.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnTrain.insets = new Insets(0, 0, 5, 5);
		gbc_btnTrain.gridx = 3;
		gbc_btnTrain.gridy = 2;
		frame.getContentPane().add(btnTrain, gbc_btnTrain);*/
		
		JLabel lblOutput = new JLabel("Output:");
		GridBagConstraints gbc_lblOutput = new GridBagConstraints();
		gbc_lblOutput.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutput.anchor = GridBagConstraints.WEST;
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
				if (!checkKsetLoaded())
				    return;

                try {
                    cmd.RUN_NETWORK.execute(txtDataset.getText(), txtOutput.getText());
                } catch (IllegalArgumentException ex) {
                    showError(ex);
                }
			}
		);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.insets = new Insets(5, 0, 5, 5);
		gbc_btnRun.anchor = GridBagConstraints.EAST;
		gbc_btnRun.gridx = 2;
		gbc_btnRun.gridy = 4;
		frame.getContentPane().add(btnRun, gbc_btnRun);

		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		GridBagConstraints gbc_rigidArea_1 = new GridBagConstraints();
		gbc_rigidArea_1.gridx = 3;
		gbc_rigidArea_1.gridy = 5;
		frame.getContentPane().add(rigidArea_1, gbc_rigidArea_1);

		frame.pack();
		//</editor-fold>
		//<editor-fold desc="Dialog text boxes init">
		txtCreateNetworkLayers = new JTextField[3];
		for (int i = 0; i < txtCreateNetworkLayers.length; i++)
			txtCreateNetworkLayers[i] = new JTextField(5);
		txtCreateNetworkLayers[0].addHierarchyListener(requestFocusOnShow);

		txtParamEdit = new JTextField[2];
		for (int i = 0; i < txtParamEdit.length; i++)
			txtParamEdit[i] = new JTextField(5);
		txtParamEdit[0].addHierarchyListener(requestFocusOnShow);
		//</editor-fold>
		//<editor-fold desc="Frame config">
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		//</editor-fold>
	}

	// Commands
	private void showCreateNetworkDialog(@SuppressWarnings("unused") ActionEvent e) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		//<editor-fold desc="Text fields init">
		for (int i = 0; i < txtCreateNetworkLayers.length; i++) {
			txtCreateNetworkLayers[i].setText("");
			
			panel.add(new JLabel("Layer " + i + " size:"));
			panel.add(txtCreateNetworkLayers[i]);
		}
		//</editor-fold>

		int res = JOptionPane.showConfirmDialog(
			frame, panel,
			"Enter layer sizes", JOptionPane.OK_CANCEL_OPTION
		);

		if (res == JOptionPane.OK_OPTION) {
			// Create command string
			StringBuilder bld = new StringBuilder(cmd.NEW_NETWORK.toString()).append(' ');
			for (JTextField txt : txtCreateNetworkLayers)
				bld.append(txt.getText()).append(' ');

			cmd.execute(bld.toString());
		}
	}

	private void trySave(boolean forcePopup) {
		if (!checkKsetLoaded())
			return;
        String name = getNetworkName(forcePopup);
        if (name != null) {
            try {
                cmd.SAVE_NETWORK.execute(name + ".kset");
                txtNetwork.setText(name);
            } catch (IllegalArgumentException ex) {
                showMessage("Error saving file", "Could not save to file " + name + ".kset\n" + ex.getMessage());
            }
        }
	}

	private void showLoadNetworkDialog(@SuppressWarnings("unused") ActionEvent e) {
		String name = getNetworkName(true);
		if (name != null) {
			try {
				cmd.LOAD_NETWORK.execute(name + ".kset");
				txtNetwork.setText(name);
			} catch (IllegalArgumentException ex) {
				showMessage("Error loading file", "Could not load file " + name + ".kset\n" + ex.getMessage());
			}
		}
	}


	private void showSetParamDialog(@SuppressWarnings("unused") ActionEvent e) {
		if (!checkKsetLoaded())
			return;

        boolean done = false;
        do {
            String args = showEditParamDialog();
            if (args == null) {
                done = true;
            } else {
                StringBuilder bld = new StringBuilder(cmd.SET_PARAM.toString()).append(' ');
                bld.append(args);
                try {
                    cmd.execute(bld.toString());
                    done = true;
                } catch (IllegalArgumentException ex) {
                    showError(ex);
                }
            }
        } while (!done);
	}

	private void showTrainingDialog(@SuppressWarnings("unused") ActionEvent e) {
	    if (!checkKsetLoaded())
	    	return;

	    //<editor-fold desc="Component init & layout">
	    JCheckBox[] boxes = new JCheckBox[cmd.kset.k3.length];
		JPanel pane = new JPanel(new GridLayout(0, 2));

		JTextField txtTrainDataset = new JTextField();

		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = new JCheckBox();
			boxes[i].setSelected(cmd.kset.getLayerTraining(i));

			pane.add(new JLabel(String.format("Train layer %d", i)));
		    pane.add(boxes[i]);
		}

		pane.add(new JLabel("Training dataset:"));
		pane.add(txtTrainDataset);
		//</editor-fold>

		int res = JOptionPane.showConfirmDialog(
			frame, pane, "Select layer training",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
		);

		if (res == JOptionPane.OK_OPTION) {
		    String[] layerTrain = new String[boxes.length+1];
		    layerTrain[0] = "layer_training";
			for (int i = 0; i < boxes.length; i++)
				layerTrain[i+1] = String.valueOf(boxes[i].isSelected());

			try {
				cmd.SET_PARAM.execute(layerTrain);
				cmd.TRAIN_NETWORK.execute(txtTrainDataset.getText());
			} catch (IllegalArgumentException ex) {
			    showError(ex);
			}
		}
	}


	private void showNetworkDisplayDialog(@SuppressWarnings("unused") ActionEvent e) {
		if (!checkKsetLoaded())
			return;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;

        //<editor-fold desc="Component init & layout">
    for (int i = 0; i < txtCreateNetworkLayers.length; i++) {
    panel.add(
    new JLabel(
    String.format("Layer %d:%s", i, i == cmd.kset.getOutputLayer() ? " [OUTPUT LAYER]" : "")
    ),
    g
    );

    g.gridy++;
    panel.add(new JLabel("Size: " + cmd.kset.k3[i].getSize()), g);

    g.gridy++;
    panel.add(new JLabel("Learning rate: " + cmd.kset.k3[i].getLearningRate()), g);

    g.gridy++;
    panel.add(new JLabel(String.format("Training: %b", cmd.kset.getLayerTraining(i))), g);

    JButton btn = new JButton("Details");

    // Create button which shows details about the corresponding layer.
    // The action command string is the number of the layer.
    btn.addActionListener(ev -> showLayerDetails(Integer.parseInt(ev.getActionCommand())));
    btn.setActionCommand(String.valueOf(i));

    g.gridy++;
    panel.add(btn, g);

    if (i != txtCreateNetworkLayers.length - 1) {
    g.gridy++;

    JLabel sep = new JLabel();
    sep.setPreferredSize(new Dimension(70, 20));
    panel.add(sep, g);

    g.gridy++;
    }
    }
    //</editor-fold>
        JOptionPane.showMessageDialog(frame, panel, txtNetwork.getText(), JOptionPane.PLAIN_MESSAGE);
	}

	private void showDatasetDisplayDialog(@SuppressWarnings("unused") ActionEvent e) {
		String filename = JOptionPane.showInputDialog(frame, "Dataset file name");
		File f;

		if (filename != null) {
			f = new File(filename);
			double[][] data;
			try {
				data = DataIO.read(f);
			} catch (IOException ex) {
				showError(ex);
				return;
			}

			//<editor-fold desc="Component init & layout">
			JPanel pane = new JPanel();
			pane.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridx = 0;
			gbc.gridy = 0;

			JLabel lbData = new JLabel(String.format("%dx%d dataset\n", data.length, data[0].length));
			pane.add(lbData, gbc);

			JTextArea txtData = new JTextArea();
			txtData.setEditable(false);

			// Fill text area with dataset
			StringBuilder bld = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				bld.append(data[i][0]);

				for (int j = 1; j < data[i].length; j++)
					bld.append('\t').append(data[i][j]);

				txtData.append(bld.toString());
				if (i < data.length-1)
					txtData.append("\n");

				bld.setLength(0);
			}
			gbc.gridy++;
			pane.add(txtData, gbc);
			//</editor-fold>
			JOptionPane.showMessageDialog(frame, pane, filename, JOptionPane.PLAIN_MESSAGE);
		}
	}


	private void showReferences(@SuppressWarnings("unused") ActionEvent e) {
		// FIXME open references window
		JOptionPane.showMessageDialog(
				frame,
				"Section not available", "Not available",
				JOptionPane.WARNING_MESSAGE
		);
	}


	// Utility methods
	@Nullable
	private String showEditParamDialog() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		for (JTextField txt : txtParamEdit)
			txt.setText("");

		panel.add(new JLabel("Parameter name:"));
		panel.add(txtParamEdit[0]);
		panel.add(new JLabel("Parameter value:"));
		panel.add(txtParamEdit[1]);

		int res = JOptionPane.showConfirmDialog(
				frame, panel,
				"Input parameter data", JOptionPane.OK_CANCEL_OPTION
		);
		if (res == JOptionPane.OK_OPTION)
			return txtParamEdit[0].getText() + ' ' + txtParamEdit[1].getText();

		return null;
	}

	private void showLayerDetails(int i) {
		//<editor-fold desc="Component init & layout">
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("Id: " + cmd.kset.k3[i].getId()));
		panel.add(new JLabel("Weights:"));
		
		double[] w = cmd.kset.k3[i].getWeights();
		
		for (int j = 0; j < w.length; j++)
			panel.add(new JLabel(String.format("%d: %.5f", j, w[j])));
		//</editor-fold>
		JOptionPane.showMessageDialog(frame, panel);
	}

	private boolean checkKsetLoaded() {
		if (cmd.kset != null)
			return true;

		JOptionPane.showMessageDialog(frame, "No kset loaded");
		return false;
	}
	
	private void showMessage(@NotNull String title, @NotNull String message) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
	}

	private void showError(@NotNull Exception e) {
		showMessage("Error", e.getMessage());
	}

}
