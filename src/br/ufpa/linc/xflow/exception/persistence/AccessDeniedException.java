package br.ufpa.linc.xflow.exception.persistence;

public class AccessDeniedException extends DatabaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5136926497569702438L;

	public AccessDeniedException(String message) {
		super(message);
	}

}
