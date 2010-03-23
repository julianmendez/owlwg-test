package com.clarkparsia.owlwg.runner.jcel;

import org.semanticweb.owl.inference.OWLReasonerException;

/**
 * This exception is thrown when the reasoner is requested an operation that is
 * unsupported. This happens because the reasoner implements an interface that
 * requires a wider functionality than the one given by the reasoner.
 * 
 * @author Julian Mendez
 * 
 * */
public class UnsupportedReasonerOperationInJcelException extends
		OWLReasonerException {

	private static final long serialVersionUID = -8551310868899524604L;

	public UnsupportedReasonerOperationInJcelException() {
		super("This operation is not supported by jcel.");
	}

	public UnsupportedReasonerOperationInJcelException(String message) {
		super(message);
	}

	public UnsupportedReasonerOperationInJcelException(String message,
			Throwable cause) {
		super(message, cause);
	}

	public UnsupportedReasonerOperationInJcelException(Throwable cause) {
		super(cause);
	}
}
