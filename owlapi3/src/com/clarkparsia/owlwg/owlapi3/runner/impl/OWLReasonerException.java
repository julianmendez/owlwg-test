package com.clarkparsia.owlwg.owlapi3.runner.impl;

/**

 */
public class OWLReasonerException extends RuntimeException {

	private static final long serialVersionUID = -5521748499650482799L;

	public OWLReasonerException() {
		super("This operation is not supported by jcel.");
	}

	public OWLReasonerException(String message) {
		super(message);
	}

	public OWLReasonerException(String message, Throwable cause) {
		super(message, cause);
	}

	public OWLReasonerException(Throwable cause) {
		super(cause);
	}
}
