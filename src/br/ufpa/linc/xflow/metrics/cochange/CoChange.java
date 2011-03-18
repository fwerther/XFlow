package br.ufpa.linc.xflow.metrics.cochange;

public class CoChange {

	private String lhs;
	private String rhs;
	
	private int support;
	private double confidence;
	
	public CoChange(String lhs, String rhs, int support, int lhsChanges){
		this.lhs = lhs;
		this.rhs = rhs;
		this.support= support;
		
		//Confidence calculation
		this.confidence = (double)support/lhsChanges;
	}
	
	public String getLhs() {
		return lhs;
	}

	public String getRhs() {
		return rhs;
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