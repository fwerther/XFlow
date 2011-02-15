package br.ufpa.linc.xflow.data.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="author_dependency")
@DiscriminatorValue(""+DependencyObject.AUTHOR_DEPENDENCY)
public class AuthorDependencyObject extends DependencyObject {

	@OneToOne
	@JoinColumn(name = "AUTHOR_ID", nullable = false)
	private Author author;

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(final Author author) {
		this.author = author;
	}

	
	public AuthorDependencyObject() {
		super(DependencyObject.AUTHOR_DEPENDENCY);
	}

	@Override
	public String getDependencyObjectName() {
		return this.author.getName();
	}
	
}
