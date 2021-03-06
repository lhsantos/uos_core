package org.unbiquitous.uos.core.applicationManager;

import java.util.Map;

import org.unbiquitous.uos.core.InitialProperties;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Response;
import org.unbiquitous.uos.core.ontologyEngine.api.OntologyDeploy;
import org.unbiquitous.uos.core.ontologyEngine.api.OntologyStart;
import org.unbiquitous.uos.core.ontologyEngine.api.OntologyUndeploy;


public class DummyApp implements UosApplication{
	
	public boolean inited;
	public int	initedCount;
	public boolean started;
	public int	startedCount;
	public boolean stoped;
	public int	stopedCount;
	public boolean finished;
	public int	finishedCount;
	public String appId;
	public OntologyDeploy initOntology;
	public OntologyStart startOntology;
	public OntologyUndeploy teardownOntology;
	public Gateway gateway;
	public Map<String,Object> callbackMap;
	public Call serviceCall;
	public CallContext context;
	public InitialProperties properties;
	
	public static DummyApp lastInstance;
	
	public DummyApp() {
		lastInstance = this;
	}
	
	@Override
	public void init(OntologyDeploy ontology, InitialProperties properties, String appId) {
		this.initOntology = ontology;
		this.appId = appId;
		this.properties = properties;
		inited = true;
		initedCount ++;
	}
	
	public void start(Gateway gateway, OntologyStart ontology) {
		this.gateway = gateway;
		this.startOntology = ontology;
		started= true;
		startedCount++;
	}

	@Override
	public void stop() throws Exception {
		stoped = true;
		stopedCount++;
	}

	@Override
	public void tearDown(OntologyUndeploy ontology) throws Exception {
		this.teardownOntology = ontology;
		finished = true;
		finishedCount++;
	}
	
	public Map<String,Object> callback(Map<String,Object> parameter){
		return callbackMap = parameter;
	}
	
	public Response commonCallback(Call call, CallContext ctx){
		this.serviceCall = call;
		this.context = ctx;
		return null;
	}
	
}