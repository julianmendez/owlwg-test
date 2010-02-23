package com.clarkparsia.owlwg.runner.jcel;

import java.net.URI;
import java.util.Collections;
import java.util.Iterator;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLLogicalAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import com.clarkparsia.owlwg.owlapi2.runner.impl.OwlApi2AbstractRunner;

/**
 * Test runner for jcel.
 */
public class JcelTestRunner extends OwlApi2AbstractRunner {

	private static final URI uri;

	static {
		uri = URI.create("http://lat.inf.tu-dresden.de/systems/jcel/");
	}

	private OWLReasoner reasoner = null;

	public void dispose() throws OWLReasonerException {
		if (this.reasoner != null) {
			this.reasoner.dispose();
		}
	}

	public String getName() {
		return "jcel";
	}

	public URI getURI() {
		return uri;
	}

	protected boolean isConsistent(OWLOntologyManager manager, OWLOntology o)
			throws OWLReasonerException {
		OWLReasoner reasoner = JcelReasonerManager.getInstance()
				.getJcelReasoner(manager);
		this.reasoner = reasoner;

		reasoner.clearOntologies();
		reasoner.loadOntologies(Collections.singleton(o));
		reasoner.classify();
		boolean ret = reasoner.isConsistent(o);
		return ret;
	}

	protected boolean isEntailed(OWLOntologyManager manager,
			OWLOntology premise, OWLOntology conclusion)
			throws OWLReasonerException {

		OWLReasoner reasoner = JcelReasonerManager.getInstance()
				.getJcelReasoner(manager);
		this.reasoner = reasoner;

		reasoner.clearOntologies();
		reasoner.loadOntologies(Collections.singleton(premise));
		reasoner.classify();

		JcelEntailmentChecker checker = new JcelEntailmentChecker(reasoner,
				manager.getOWLDataFactory());

		boolean ret = true;
		for (Iterator<OWLLogicalAxiom> it = conclusion.getLogicalAxioms()
				.iterator(); ret && it.hasNext();) {
			OWLLogicalAxiom axiom = it.next();

			if (!checker.isEntailed(axiom)) {
				ret = false;
			}
		}
		return ret;
	}
}
