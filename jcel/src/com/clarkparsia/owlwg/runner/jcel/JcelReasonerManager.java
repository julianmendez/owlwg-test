package com.clarkparsia.owlwg.runner.jcel;

import org.semanticweb.owl.model.OWLOntologyManager;

import de.tudresden.inf.lat.jcel.owlapi.JcelReasoner;

public class JcelReasonerManager {

	private static JcelReasonerManager instance = null;
	private JcelReasoner reasoner = null;

	private JcelReasonerManager() {
	}

	public JcelReasoner getJcelReasoner(OWLOntologyManager manager) {
		if (this.reasoner == null && manager != null) {
			this.reasoner = new JcelReasoner(manager);
		}
		return this.reasoner;
	}

	public static JcelReasonerManager getInstance() {
		if (instance == null) {
			instance = new JcelReasonerManager();
		}
		return instance;
	}
}
