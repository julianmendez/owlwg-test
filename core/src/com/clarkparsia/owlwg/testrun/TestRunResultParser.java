package com.clarkparsia.owlwg.testrun;

import static com.clarkparsia.owlwg.testcase.TestVocabulary.DatatypeProperty.IDENTIFIER;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.AnnotationProperty.DETAILS;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.PROFILE_IDENTIFICATION_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.SYNTAX_TRANSLATION_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.TEST_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.ObjectProperty.RUNNER;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.ObjectProperty.TEST;
import static java.lang.String.format;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;

import com.clarkparsia.owlwg.runner.TestRunner;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Test Run Result Parser
 * </p>
 * <p>
 * Description: Convert from OWLAPI object model to test run result objects. See
 * also {@link TestRunResultAdapter}.
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class TestRunResultParser {

	private static class ReadOnlyTestRunner implements TestRunner {

		final private URI	uri;

		public ReadOnlyTestRunner(URI uri) {
			this.uri = uri;
		}

		@Override
		public boolean equals(Object obj) {
			if( this == obj )
				return true;

			if( obj instanceof ReadOnlyTestRunner ) {
				final ReadOnlyTestRunner other = (ReadOnlyTestRunner) obj;
				return this.uri.equals( other.uri );
			}

			return false;
		}

		public URI getURI() {
			return uri;
		}

		@Override
		public int hashCode() {
			return uri.hashCode();
		}

		public Collection<TestRunResult> run(TestCase testcase, long timeout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return uri.toString();
		}
	}

	private static final Logger					log;

	private static final Map<URI, TestRunner>	runners;

	static {
		log = Logger.getLogger( TestRunResultParser.class.getCanonicalName() );
		runners = new HashMap<URI, TestRunner>();
	}

	private static TestRunner getRunner(URI uri) {
		TestRunner runner = runners.get( uri );
		if( runner == null ) {
			runner = new ReadOnlyTestRunner( uri );
			runners.put( uri, runner );
		}
		return runner;
	}

	public Collection<TestRunResult> getResults(OWLOntology o, Map<String, TestCase> tests) {

		List<TestRunResult> results = new ArrayList<TestRunResult>();

		for( OWLClassAssertionAxiom axiom : o.getClassAssertionAxioms( TEST_RUN.getOWLClass() ) ) {
			final OWLIndividual i = axiom.getIndividual();
			final Map<OWLObjectPropertyExpression, Set<OWLIndividual>> oValues = i
					.getObjectPropertyValues( o );

			Set<OWLIndividual> testObjects = oValues.get( TEST.getOWLObjectProperty() );
			if( testObjects.size() != 1 ) {
				log.warning( format(
						"Skipping result, missing or more than one test assertion (\"%s\",%s)", i
								.getURI(), testObjects ) );
				continue;
			}
			Map<OWLDataPropertyExpression, Set<OWLConstant>> testDValues = testObjects.iterator()
					.next().getDataPropertyValues( o );

			Set<OWLConstant> ids = testDValues.get( IDENTIFIER.getOWLDataProperty() );
			TestCase testCase = null;
			for( OWLConstant c : ids ) {
				String id = c.getLiteral();
				testCase = tests.get( id );
				if( testCase != null )
					break;
			}

			if( testCase == null ) {
				log.warning( format( "Skipping result, no matching test case found (\"%s\",%s)", i
						.getURI(), ids ) );
				continue;
			}

			Set<OWLIndividual> runnerUris = oValues.get( RUNNER.getOWLObjectProperty() );
			TestRunner runner = null;
			if( runnerUris.size() != 1 ) {
				log
						.warning( format(
								"Skipping result, missing or more than one test runner assertion (\"%s\",%s)",
								i.getURI(), runnerUris ) );
				continue;
			}
			runner = getRunner( runnerUris.iterator().next().getURI() );

			Set<OWLDescription> types = i.getTypes( o );

			RunResultType resultType = null;
			for( RunResultType t : RunResultType.values() ) {
				if( types.contains( t.getOWLClass() ) ) {
					resultType = t;
					break;
				}
			}
			if( resultType == null ) {
				log.warning( format( "Skipping result, missing result type (\"%s\")", i.getURI() ) );
				continue;
			}

			@SuppressWarnings("unchecked")
			Set<OWLAnnotation> detailsAnnotations = i.getAnnotations( o, DETAILS
					.getAnnotationPropertyURI() );
			String details = null;
			int ndetails = detailsAnnotations.size();
			if( ndetails > 0 ) {
				if( ndetails > 1 )
					log
							.info( format(
									"Result contains multiple details annotations, ignoring all but first (\"%s\")",
									i.getURI() ) );
				details = detailsAnnotations.iterator().next().getAnnotationValueAsConstant()
						.getLiteral();
			}

			TestRunResult result = null;
			if( types.contains( SYNTAX_TRANSLATION_RUN.getOWLClass() ) ) {
				result = (details == null)
					? new SyntaxTranslationRun( testCase, resultType, runner )
					: new SyntaxTranslationRun( testCase, resultType, runner, details );
			}
			else if( types.contains( PROFILE_IDENTIFICATION_RUN.getOWLClass() ) ) {
				result = (details == null)
					? new ProfileIdentificationRun( testCase, resultType, runner )
					: new ProfileIdentificationRun( testCase, resultType, runner, details );
			}
			else {
				for( ReasoningRunType t : ReasoningRunType.values() ) {
					if( types.contains( t.getOWLClass() ) )
						result = (details == null)
							? new ReasoningRun( testCase, t, resultType, runner )
							: new ReasoningRun( testCase, t, resultType, runner, details );
				}
				if( result == null ) {
					log.warning( format(
							"Skipping result, missing recognizable run type (\"%s\",%s)", i
									.getURI(), types ) );
					continue;
				}
			}

			results.add( result );
		}

		return results;
	}
}