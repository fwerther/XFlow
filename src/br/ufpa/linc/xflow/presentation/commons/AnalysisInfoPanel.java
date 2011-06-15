package br.ufpa.linc.xflow.presentation.commons;

import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class AnalysisInfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5655487115857478823L;

	public final String FIRST_REVISION_LABEL_TEXT;
	public final String FIRST_DATE_LABEL_TEXT;
	public final String LAST_REVISION_LABEL_TEXT;
	public final String LAST_DATE_LABEL_TEXT;
	
	
	private JLabel firstRevisionLabel;
	private JLabel firstRevisionDateLabel;
	private JLabel lastRevisionLabel;
	private JLabel lastRevisionDateLabel;

	private SliderControl sliderControl;

	public AnalysisInfoPanel(Analysis analysis) throws DatabaseException {
		super();
		
		final EntryDAO entryDAO = new EntryDAO();
		final int numberOfEntries = entryDAO.countEntriesByRevisionsLimit(analysis.getFirstEntry(), analysis.getLastEntry());
		this.putClientProperty("Analysis", analysis);
		this.sliderControl = new SliderControl(1, numberOfEntries-1);
		
		final Date initialDate;
		final Date finalDate;
		
		if(analysis.getProject().isTemporalConsistencyForced()){
			initialDate = analysis.getFirstEntry().getDate();
			finalDate = analysis.getLastEntry().getDate();
		} else{
			initialDate = new EntryDAO().getMinorEntryDateByEntries(analysis.getFirstEntry(), analysis.getLastEntry());
			finalDate = new EntryDAO().getHighestEntryDateByEntries(analysis.getFirstEntry(), analysis.getLastEntry());
		}
		
		final Date firstEntryDate = analysis.getFirstEntry().getDate();
		final Date lastEntryDate = analysis.getLastEntry().getDate();
		
		this.FIRST_REVISION_LABEL_TEXT = new String("First revision: "+analysis.getFirstEntry().getRevision());
		this.FIRST_DATE_LABEL_TEXT = new String("Date: "+firstEntryDate);
		this.LAST_REVISION_LABEL_TEXT = new String("Last revision: "+analysis.getLastEntry().getRevision());
		this.LAST_DATE_LABEL_TEXT = new String("Date: "+lastEntryDate);
		
		this.firstRevisionLabel = new JLabel(FIRST_REVISION_LABEL_TEXT + " (1)");
		this.lastRevisionLabel = new JLabel(LAST_REVISION_LABEL_TEXT + " ("+(numberOfEntries-1)+")");
		this.firstRevisionDateLabel = new JLabel(FIRST_DATE_LABEL_TEXT + " (" + initialDate + ")");
		this.lastRevisionDateLabel = new JLabel(LAST_DATE_LABEL_TEXT + " (" + finalDate + ")");
	}

	public JPanel createInfoPanel(){
		this.setBorder(BorderFactory.createTitledBorder("Analysis Info"));
		GroupLayout layout = createLayout();
		this.setLayout(layout);
		return this;
	}

	private GroupLayout createLayout() {
		GroupLayout layout = new GroupLayout(this);

		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(firstRevisionLabel)
								.addComponent(firstRevisionDateLabel))
								.addPreferredGap(ComponentPlacement.RELATED, 493, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(lastRevisionLabel)
										.addComponent(lastRevisionDateLabel)
								)
								.addGap(11, 11, 11)
						)
				.addComponent(sliderControl, GroupLayout.PREFERRED_SIZE, 769, Short.MAX_VALUE)

		);

		layout.setVerticalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(firstRevisionLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(firstRevisionDateLabel)
								)
								.addGroup(layout.createSequentialGroup()
										.addComponent(lastRevisionLabel)
										.addComponent(lastRevisionDateLabel)
								)
						)
						.addGap(11, 11, 11)
						.addComponent(sliderControl)
				)
		);

		return layout;
	}

	public JLabel getLastRevisionLabel() {
		return lastRevisionLabel;
	}

	public JLabel getLastRevisionDateLabel() {
		return lastRevisionDateLabel;
	}

	public SliderControl getSliderControl() {
		return sliderControl;
	}
	
}
