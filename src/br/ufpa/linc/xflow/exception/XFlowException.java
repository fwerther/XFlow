package br.ufpa.linc.xflow.exception;

public abstract class XFlowException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1984023562481219351L;

	public XFlowException() {
		super();
	}

	public XFlowException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public XFlowException(final String message) {
		super(message);
	}

	public XFlowException(final Throwable cause) {
		super(cause);
	}
}
