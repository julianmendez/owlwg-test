package com.clarkparsia.owlwg.runner.jcel;

import org.semanticweb.owl.model.OWLOntologyManager;

import de.tudresden.inf.lat.jcel.protege.main.JcelOldReasoner;

public class JcelReasonerManager {

	private static JcelReasonerManager instance = null;
	private JcelOldReasoner reasoner = null;

	private JcelReasonerManager() {
	}

	public JcelOldReasoner getJcelReasoner(OWLOntologyManager manager) {
		if (this.reasoner == null && manager != null) {
			this.reasoner = new JcelOldReasoner(manager);
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
