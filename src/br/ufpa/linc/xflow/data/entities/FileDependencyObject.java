package br.ufpa.linc.xflow.data.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Index;

@Entity(name="file_dependency")
@DiscriminatorValue(""+DependencyObject.FILE_DEPENDENCY)
public class FileDependencyObject extends DependencyObject {

	@ManyToOne
	@JoinColumn(name = "FILE_ID", nullable = false)
	private ObjFile file;
	
	@Column(name = "FILE_PATH")
	@Index(name = "file_path_index2")
	private String filePath;

	public ObjFile getFile() {
		return file;
	}

	public void setFile(final ObjFile file) {
		this.file = file;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	public FileDependencyObject() {
		super(DependencyObject.FILE_DEPENDENCY);		
	}

	@Override
	public String getDependencyObjectName() {
		return this.file.getName();
	}
	
}
