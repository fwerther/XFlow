package br.ufpa.linc.xflow.exception.persistence;

import br.ufpa.linc.xflow.exception.XFlowException;

public class DatabaseException extends XFlowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 811924985464909218L;

	public DatabaseException(String message) {
		super(message, new Throwable("Database error"));
	}
}
