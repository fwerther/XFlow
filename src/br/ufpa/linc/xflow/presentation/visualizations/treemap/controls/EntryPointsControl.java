package br.ufpa.linc.xflow.presentation.visualizations.treemap.controls;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import prefuse.util.FontLib;
import br.ufpa.linc.xflow.presentation.Visualizer;
import br.ufpa.linc.xflow.presentation.visualizations.treemap.JCustomSearchPanel;


public class EntryPointsControl implements TreeMapViewController {

	private JButton nextCommitButton;
	private JButton previousCommitButton;
	private JLabel currentCommitLabel;
	private JCustomSearchPanel fileSequenceSearchPanel;

	public EntryPointsControl() {
		nextCommitButton = new JButton(">>");
		previousCommitButton = new JButton("<<");
		currentCommitLabel = new JLabel("Commit: 0");
		this.fileSequenceSearchPanel = Visualizer.getTreeMapView().getTreeMapNewLayout().getFilesSequenceSearchPanel();
		this.fileSequenceSearchPanel.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
		this.fileSequenceSearchPanel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
		this.fileSequenceSearchPanel.setBackground(new Color(UIManager.getColor("Panel.background").getRGB()));
	}

	@Override
	public JComponent getControlComponent() {
		JPanel controlPanel = new JPanel();
		GroupLayout layout = new GroupLayout(controlPanel);
		controlPanel.setLayout(layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addGap(15, 15, 15)
						.addComponent(fileSequenceSearchPanel, GroupLayout.PREFERRED_SIZE, 81, Short.MAX_VALUE)
						.addGap(449, 449, 449)
						.addComponent(previousCommitButton)
						.addGap(18, 18, 18)
						.addComponent(currentCommitLabel, GroupLayout.DEFAULT_SIZE, 38, GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(nextCommitButton, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()
				)
		);

		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(currentCommitLabel)
								.addComponent(previousCommitButton)
								.addComponent(nextCommitButton)
								.addComponent(fileSequenceSearchPanel)
						)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				)
		);

		return controlPanel;
	}

}
