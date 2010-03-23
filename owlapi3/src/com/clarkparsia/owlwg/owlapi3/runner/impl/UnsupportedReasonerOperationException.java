package com.clarkparsia.owlwg.owlapi3.runner.impl;

/**

 */
public class UnsupportedReasonerOperationException extends OWLReasonerException {

	private static final long serialVersionUID = -5521748499650482799L;

	public UnsupportedReasonerOperationException() {
		super("This operation is not supported by jcel.");
	}

	public UnsupportedReasonerOperationException(String message) {
		super(message);
	}

	public UnsupportedReasonerOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedReasonerOperationException(Throwable cause) {
		super(cause);
	}
}
