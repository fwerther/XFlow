package br.ufpa.linc.xflow.data.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.ufpa.linc.xflow.metrics.entry.EntryMetricValues;
import br.ufpa.linc.xflow.metrics.file.FileMetricValues;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricValues;

@Entity(name = "metrics")
public class Metrics {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "METRICS_ID")
	private long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ASSOCIATED_ANALYSIS", nullable = false)
	private Analysis associatedAnalysis;
	
	@Column(name = "FILE_METRICS")
	@OneToMany(mappedBy = "associatedMetricsObject", cascade = CascadeType.ALL)
	private List<FileMetricValues> fileMetrics;

	@Column(name = "ENTRY_METRICS")
	@OneToMany(mappedBy = "associatedMetricsObject", cascade = CascadeType.ALL)
	private List<EntryMetricValues> entryMetrics;

	@Column(name = "PROJECT_METRICS")
	@OneToMany(mappedBy = "associatedMetricsObject", cascade = CascadeType.ALL)
	private List<ProjectMetricValues> projectMetrics;

	public Metrics() {
		// Emptry constructor.
	}
	
	public long getId(){
		return this.id;
	}
	
	public Analysis getAssociatedAnalysis() {
		return associatedAnalysis;
	}

	public void setAssociatedAnalysis(Analysis associatedAnalysis) {
		this.associatedAnalysis = associatedAnalysis;
	}

	public List<FileMetricValues> getFileMetrics() {
		return fileMetrics;
	}

	public void setFileMetrics(List<FileMetricValues> fileMetrics) {
		this.fileMetrics = fileMetrics;
	}

	public List<EntryMetricValues> getEntryMetrics() {
		return entryMetrics;
	}

	public void setEntryMetrics(List<EntryMetricValues> entryMetrics) {
		this.entryMetrics = entryMetrics;
	}

	public List<ProjectMetricValues> getProjectMetrics() {
		return projectMetrics;
	}

	public void setProjectMetrics(List<ProjectMetricValues> projectMetrics) {
		this.projectMetrics = projectMetrics;
	}
}
