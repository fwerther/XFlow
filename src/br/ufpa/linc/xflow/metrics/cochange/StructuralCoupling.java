package br.ufpa.linc.xflow.metrics.cochange;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Index;

@Entity
public class StructuralCoupling {

	@Id
	@Column(name = "SC_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String client;
	
	@Index(name = "clientStamp_index")
	private int clientStamp;
	
	private String supplier;
	
	@Index(name = "supplierStamp_index")
	private int supplierStamp;
	
	private double degree;
	
	public StructuralCoupling(String client, int clientStamp, String supplier, 
			int supplierStamp, int totalCalls, int clientChanges){
		
		this.client = client;
		this.clientStamp = clientStamp;
		
		this.supplier = supplier;
		this.supplierStamp = supplierStamp;
		
		//Structural coupling calculation
		this.degree = (double)totalCalls/clientChanges;
	}
	
	public String getClient() {
		return client;
	}
	
	public int getClientStamp() {
		return clientStamp;
	}

	public String getSupplier() {
		return supplier;
	}
	
	public int getSupplierStamp() {
		return supplierStamp;
	}

	public double getDegree() {
		return degree;
	}
	
	public String toString(){
		String dependency = client + " -> " + supplier;
		String degree = "Average Degree: " + this.degree;
		return dependency + "\n" + degree + "\n"; 
	}
}