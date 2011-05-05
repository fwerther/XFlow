package br.ufpa.linc.xflow.metrics.cochange;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Index;

@Entity
public class CoChange {

	@Id
	@Column(name = "CC_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String lhs;
	
	@Index(name = "lhsStamp_index")
	private int lhsStamp;
	
	private String rhs;
	
	@Index(name = "rhsStamp_index")
	private int rhsStamp;
	
	private int support;
	private double confidence;
	
	public CoChange(String lhs, int lhsStamp, String rhs, int rhsStamp, 
			int support, int lhsChanges){
		
		this.lhs = lhs;
		this.lhsStamp = lhsStamp;
		
		this.rhs = rhs;
		this.rhsStamp = rhsStamp;
		
		this.support= support;
		
		//Confidence calculation
		this.confidence = (double)support/lhsChanges;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getLhs() {
		return lhs;
	}
	
	public int getLhsStamp() {
		return lhsStamp;
	}

	public String getRhs() {
		return rhs;
	}
	
	public int getRhsStamp() {
		return rhsStamp;
	}

	public int getSupport() {
		return support;
	}

	public double getConfidence() {
		return confidence;
	}
	
	public String toString(){
		String rule = lhs + " -> " + rhs;
		String support = "Support: " + this.support;
		String confidence = "Confidence: " + this.confidence;
		return rule + "\n" + support + "\n" + confidence + "\n"; 
	}
}