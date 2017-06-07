package app.ui.gui;

import app.ui.text.TextInterpreter;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jkset.DataIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class GuiMainWindow {

	//<editor-fold desc="UI persistent components">
	public JFrame frame;
	private TextInterpreter cmd;
	
	private JTextField txtModel;
	private JTextField txtDataset;
	private JTextField txtOutput;
	
	private JTextField[] txtParamEdit;
	private JTextField[] txtCreateModelLayers;
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

	public GuiMainWindow() {
		initialize();
	}

	@NotNull
	private String getModelName(boolean forcePopup) {
		String name = "";
		
		if (!forcePopup)
			name = txtModel.getText();
		
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
		mntmNew.addActionListener(this::showCreateModelDialog);
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
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(this::showOpenModelDialog);
		mntmOpen.setAccelerator(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                KeyEvent.CTRL_MASK
            )
		);
		mnFile.add(mntmOpen);
		
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
		
		JMenuItem mntmViewModel = new JMenuItem("Model");
		mntmViewModel.addActionListener(this::showModelDisplayDialog);
		mntmViewModel.setAccelerator(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_M,
				KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK
			)
		);
		mnView.add(mntmViewModel);
		
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
		mntmReference.addActionListener(this::showReference);
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
		
		JLabel lblModel = new JLabel("Model:");
		GridBagConstraints gbc_lblModel = new GridBagConstraints();
		gbc_lblModel.insets = new Insets(0, 0, 5, 30);
		gbc_lblModel.anchor = GridBagConstraints.WEST;
		gbc_lblModel.gridx = 1;
		gbc_lblModel.gridy = 1;
		frame.getContentPane().add(lblModel, gbc_lblModel);
		
		txtModel = new JTextField();
		txtModel.setHorizontalAlignment(SwingConstants.RIGHT);
		txtModel.setEditable(false);
		GridBagConstraints gbc_txtModel = new GridBagConstraints();
		gbc_txtModel.insets = new Insets(0, 0, 5, 5);
		gbc_txtModel.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtModel.gridx = 2;
		gbc_txtModel.gridy = 1;
		frame.getContentPane().add(txtModel, gbc_txtModel);
		txtModel.setColumns(10);
		
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
		txtDataset.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					EventQueue.invokeLater(
                        () -> {
                            txtOutput.requestFocusInWindow();
                        }
					);
				}
			}
		});
		txtDataset.setColumns(10);

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
				if (!checkKsetOpened())
				    return;

				JDialog waitDialog = showWaitDialog();

				Timer t = new Timer(100, actionEvent -> {
					try {
						cmd.RUN_NETWORK.execute(txtDataset.getText(), txtOutput.getText());
					} catch (IllegalArgumentException ex) {
						showError(ex);
					}
					EventQueue.invokeLater(
                        () -> {
                            waitDialog.dispose();
                            frame.toFront();
                            frame.repaint();
                            JOptionPane.showMessageDialog(frame, "Processing complete");
                        }
					);
                });
				t.setRepeats(false);
				t.start();
			}
		);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.insets = new Insets(5, 0, 5, 5);
		gbc_btnRun.anchor = GridBagConstraints.EAST;
		gbc_btnRun.gridx = 2;
		gbc_btnRun.gridy = 4;
		frame.getContentPane().add(btnRun, gbc_btnRun);


		txtOutput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					EventQueue.invokeLater(
                        () -> {
                            btnRun.doClick();
                        }
					);
				}
			}
		});

		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		GridBagConstraints gbc_rigidArea_1 = new GridBagConstraints();
		gbc_rigidArea_1.gridx = 3;
		gbc_rigidArea_1.gridy = 5;
		frame.getContentPane().add(rigidArea_1, gbc_rigidArea_1);

		frame.pack();
		//</editor-fold>
		//<editor-fold desc="Dialog text boxes init">
		txtCreateModelLayers = new JTextField[3];
		for (int i = 0; i < txtCreateModelLayers.length; i++)
			txtCreateModelLayers[i] = new JTextField(5);
		txtCreateModelLayers[0].addHierarchyListener(requestFocusOnShow);

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
	private void showCreateModelDialog(@SuppressWarnings("unused") ActionEvent e) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		//<editor-fold desc="Text fields init">
		for (int i = 0; i < txtCreateModelLayers.length; i++) {
			txtCreateModelLayers[i].setText("");
			
			panel.add(new JLabel("Layer " + i + " size:"));
			panel.add(txtCreateModelLayers[i]);
		}
		//</editor-fold>

		int res = JOptionPane.showConfirmDialog(
			frame, panel,
			"Enter layer sizes", JOptionPane.OK_CANCEL_OPTION
		);

		if (res == JOptionPane.OK_OPTION) {
			// Create command string
			StringBuilder bld = new StringBuilder(cmd.NEW_NETWORK.toString()).append(' ');
			for (JTextField txt : txtCreateModelLayers)
				bld.append(txt.getText()).append(' ');

			try {
				cmd.execute(bld.toString());
			} catch (IllegalArgumentException ex) {
				showError(ex);
			}
		}
	}

	private void trySave(boolean forcePopup) {
		if (!checkKsetOpened())
			return;
        String name = getModelName(forcePopup);
        if (name != null) {
            try {
                cmd.SAVE_NETWORK.execute(name + ".kset");
                txtModel.setText(name);
            } catch (IllegalArgumentException ex) {
            	showError(ex);
            }
        }
	}

	private void showOpenModelDialog(@SuppressWarnings("unused") ActionEvent e) {
		String name = getModelName(true);
		if (name != null) {
			try {
				cmd.LOAD_NETWORK.execute(name + ".kset");
				txtModel.setText(name);
			} catch (IllegalArgumentException ex) {
			    showError(ex);
			}
		}
	}


	private void showSetParamDialog(@SuppressWarnings("unused") ActionEvent e) {
		if (!checkKsetOpened())
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
	    if (!checkKsetOpened())
	    	return;

	    //<editor-fold desc="Component init & layout">
	    JCheckBox[] boxes = new JCheckBox[cmd.kset.k3.length];
		JPanel pane = new JPanel(new GridLayout(0, 2));

		JTextField txtTrainDataset = new JTextField();
		txtTrainDataset.addHierarchyListener(requestFocusOnShow);

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

			final JDialog waitDialog = showWaitDialog();

			// Wait 100 ms so Swing has time to draw the dialog
			Timer t = new Timer(100, actionEvent -> {
				try {
					cmd.SET_PARAM.execute(layerTrain);
					cmd.TRAIN_NETWORK.execute(txtTrainDataset.getText());
				} catch (IllegalArgumentException ex) {
					showError(ex);
				}
				EventQueue.invokeLater(
                    () -> {
                        waitDialog.dispose();
                        frame.toFront();
                        frame.repaint();
                        JOptionPane.showMessageDialog(frame, "Training complete");
                    }
				);
			});
			t.setRepeats(false);
			t.start();
		}
	}


	private void showModelDisplayDialog(@SuppressWarnings("unused") ActionEvent e) {
		if (!checkKsetOpened())
			return;

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.anchor = GridBagConstraints.WEST;
		g.gridx = 0;
		g.gridy = 0;

		JLabel lbModel = new JLabel("Model details");
		Font f = lbModel.getFont();
		lbModel.setFont(new Font(f.getName(), Font.BOLD, f.getSize()+2));
		panel.add(lbModel, g);

		g.gridy++;
		panel.add(new JLabel("Output layer: " + cmd.kset.getOutputLayer()), g);

		g.gridy++;
		panel.add(new JLabel("Learning rate: " + cmd.kset.k3[0].getLearningRate()), g);

		g.gridy++;
		panel.add(new JLabel("Detecting instability: " + cmd.kset.getDetectInstability()), g);

		//<editor-fold desc="Component init & layout">
		for (int i = 0; i < txtCreateModelLayers.length; i++) {
			g.gridy++;

			JLabel sep = new JLabel();
			sep.setPreferredSize(new Dimension(70, 20));
			panel.add(sep, g);

			JLabel lbLayer = new JLabel(String.format("Layer %d:", i));
			f = lbLayer.getFont();
			lbLayer.setFont(new Font(f.getName(), Font.BOLD, f.getSize()+2));
			panel.add(lbLayer, g);

			g.gridy++;
			panel.add(new JLabel("Size: " + cmd.kset.k3[i].getSize()), g);

			g.gridy++;
			panel.add(new JLabel(String.format("Training: %b", cmd.kset.getLayerTraining(i))), g);

			JButton btn = new JButton("Details");

			// Create button which shows details about the corresponding layer.
			// The action command string is the number of the layer.
			btn.addActionListener(ev -> showLayerDetails(Integer.parseInt(ev.getActionCommand())));
			btn.setActionCommand(String.valueOf(i));

			g.gridy++;
			panel.add(btn, g);

            g.gridy++;
		}
		//</editor-fold>
		JOptionPane.showMessageDialog(frame, panel, txtModel.getText(), JOptionPane.PLAIN_MESSAGE);
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
			txtData.setPreferredSize(new Dimension(300, 400));
			txtData.setMinimumSize(new Dimension(300, 400));
			JScrollPane scrollPane = new JScrollPane(txtData);
			scrollPane.setPreferredSize(new Dimension(300, 400));
			scrollPane.setMinimumSize(new Dimension(300, 400));
			pane.add(scrollPane, gbc);
			//</editor-fold>
			JOptionPane.showMessageDialog(frame, pane, filename, JOptionPane.PLAIN_MESSAGE);
		}
	}


	private void showReference(@SuppressWarnings("unused") ActionEvent e) {
		try {
			Desktop.getDesktop().open(new File("freemans-kiii-reference.pdf"));
		} catch (IllegalArgumentException | IOException ex) {
			showError(ex);
		}
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
		panel.add(new JLabel("Weights:"));
		
		double[] w = cmd.kset.k3[i].getWeights();
		
		for (int j = 0; j < w.length; j++)
			panel.add(new JLabel(String.format("%d: %.5f", j, w[j])));
		//</editor-fold>
		JOptionPane.showMessageDialog(frame, panel);
	}

	private boolean checkKsetOpened() {
		if (cmd.kset != null)
			return true;

		JOptionPane.showMessageDialog(frame, "No kset loaded");
		return false;
	}

	private void showError(@NotNull Exception e) {
	    JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	private JDialog showWaitDialog() {
	    final JDialog dialog = new JDialog(frame);
	    dialog.setTitle("Please wait");
	    dialog.setModal(false);
	    dialog.setContentPane(
			new JOptionPane(
					"This might take a while",
					JOptionPane.WARNING_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
					null,
					new Object[] {},
					null));

	    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    dialog.pack();
	    dialog.setResizable(false);

		dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
		return dialog;
	}

}
