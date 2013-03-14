package br.unb.unbiquitous.ubiquitos.uos.adaptabitilyEngine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.unb.unbiquitous.ubiquitos.uos.application.UOSMessageContext;
import br.unb.unbiquitous.ubiquitos.uos.driverManager.DriverManager;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.MessageEngine;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.dataType.UpDevice;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.messages.Notify;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.messages.ServiceCall;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.messages.ServiceResponse;


public class AdaptabitilyEngineTest {

	private AdaptabilityEngine engine ;
	
	@Before public void setUp(){
		engine = new AdaptabilityEngine();
	}
	
//	public void init(
//			ConnectionManagerControlCenter connectionManagerControlCenter, 
//			DriverManager driverManager, 
//			UpDevice currentDevice,
//			UOSApplicationContext applicationContext,
//			MessageEngine messageEngine,
//			ConnectivityManager connectivityManager) {
	
//	public ServiceResponse callService(
//			UpDevice device,
//			String serviceName, 
//			String driverName, 
//			String instanceId,
//			String securityType,
//			Map<String,String> parameters) throws ServiceCallException{
	@Test(expected=IllegalArgumentException.class)
	public void callService_shouldFailWithoutAServiceCall() throws ServiceCallException{
		engine.callService(null, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void callService_shouldFailWithoutADriverSpecified() throws ServiceCallException{
		engine.callService(null, new ServiceCall());
		engine.callService(null, new ServiceCall("",null));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void callService_shouldFailWithoutAServiceSpecified() throws ServiceCallException{
		engine.callService(null, new ServiceCall());
		engine.callService(null, new ServiceCall(null,""));
	}
	
	@Test public void callService_shouldRedirectLocalCallToDriverManagerForNullDevice() throws Exception {
		DriverManager driverManager = mock(DriverManager.class);
		ServiceResponse response = new ServiceResponse();
		when(driverManager.handleServiceCall((ServiceCall)anyObject(), (UOSMessageContext)anyObject())).thenReturn(response);
		engine.init(null, driverManager, null, null, null, null, null);
		
		ServiceCall call = new ServiceCall("my.driver","myService");
		assertEquals(response,engine.callService(null, call));
	}
	
	@Test public void callService_shouldRedirectLocalCallToDriverManagerForCurrentDevice() throws Exception {
		DriverManager driverManager = mock(DriverManager.class);
		ServiceResponse response = new ServiceResponse();
		UpDevice currentDevice = new UpDevice("me");
		when(driverManager.handleServiceCall((ServiceCall)anyObject(), (UOSMessageContext)anyObject())).thenReturn(response);
		engine.init(null, driverManager, currentDevice, null, null, null, null);
		
		ServiceCall call = new ServiceCall("my.driver","myService");
		assertEquals(response,engine.callService(currentDevice, call));
	}
	
	@Test public void callService_shouldCreateAMessageContextLocalCallToDriverManager() throws Exception {
		final UOSMessageContext[] ctx = {null};
		
		DriverManager driverManager = new DriverManager(null,null,null,null){
			public ServiceResponse handleServiceCall(ServiceCall call, UOSMessageContext c){
				ctx[0] = c;
				return new ServiceResponse();
			}
		};
		engine.init(null, driverManager, null, null, null, null, null);
		
		ServiceCall call = new ServiceCall("my.driver","myService");
		engine.callService(null, call);
		
		assertNotNull(ctx[0]);
	}
	

	@Test public void callService_shouldRedirectRemoteCallToMessageEngibeForOtherDevice() throws Exception {
		MessageEngine messageEngine = mock(MessageEngine.class);
		ServiceResponse response = new ServiceResponse();
		UpDevice callee = new UpDevice("other");
		ServiceCall call = new ServiceCall("my.driver","myService");
		when(messageEngine.callService(callee, call)).thenReturn(response);
		engine.init(null, null, new UpDevice("me"), null, messageEngine, null, null);
		
		assertEquals(response,engine.callService(callee, call));
	}
	
	//TODO : AdaptabilityEngine : callService : Test Stream Service (Local and Remote)
	
	@Test public void sendEventNotify_shouldDelagateToEventManager() throws Exception{
		EventManager eventManager = mock(EventManager.class);
		engine.init(null, null, null, null, null, null, eventManager);
		Notify notify = new Notify();
		UpDevice device = new UpDevice();
		engine.sendEventNotify(notify, device);
		verify(eventManager).sendEventNotify(notify, device);
	}
	
	@Test public void registerForEvent_shouldDelagateToEventManager() throws Exception{
		EventManager eventManager = mock(EventManager.class);
		engine.init(null, null, null, null, null, null, eventManager);
		UosEventListener listener = mock(UosEventListener.class);
		UpDevice device = new UpDevice();
		engine.registerForEvent(listener, device, "driver", "eventKey");
		verify(eventManager).registerForEvent(listener, device, "driver", null, "eventKey");
	}
	
	@Test public void registerForEvent_shouldDelagateToEventManagerWithId() throws Exception{
		EventManager eventManager = mock(EventManager.class);
		engine.init(null, null, null, null, null, null, eventManager);
		UosEventListener listener = mock(UosEventListener.class);
		UpDevice device = new UpDevice();
		engine.registerForEvent(listener, device, "driver", "id", "eventKey");
		verify(eventManager).registerForEvent(listener, device, "driver", "id", "eventKey");
	}
	
	@Test public void unregisterForEvent_shouldDelagateToEventManager() throws Exception{
		EventManager eventManager = mock(EventManager.class);
		engine.init(null, null, null, null, null, null, eventManager);
		UosEventListener listener = mock(UosEventListener.class);
		engine.unregisterForEvent(listener);
		verify(eventManager).unregisterForEvent(listener, null, null, null, null);
	}
	
	@Test public void unregisterForEvent_shouldDelagateToEventManagerWithId() throws Exception{
		EventManager eventManager = mock(EventManager.class);
		engine.init(null, null, null, null, null, null, eventManager);
		UosEventListener listener = mock(UosEventListener.class);
		UpDevice device = new UpDevice();
		engine.unregisterForEvent(listener, device, "driver", "id", "eventKey");
		verify(eventManager).unregisterForEvent(listener, device, "driver", "id", "eventKey");
	}
	
	@Test public void handleNofify_shouldDelagateToEventManager() throws Exception{
		EventManager eventManager = mock(EventManager.class);
		engine.init(null, null, null, null, null, null, eventManager);
		UpDevice device = new UpDevice();
		Notify notify = new Notify();
		engine.handleNofify(notify,device);
		verify(eventManager).handleNofify(notify,device);
	}
	
	@Test public void handleServiceCall_shouldDelagateToEventManager() throws Exception{
		DriverManager driverManager = mock(DriverManager.class);
		engine.init(null, driverManager, null, null, null, null, null);
		ServiceCall serviceCall = new ServiceCall();
		UOSMessageContext messageContext = new UOSMessageContext();
		engine.handleServiceCall(serviceCall,messageContext);
		verify(driverManager).handleServiceCall(serviceCall,messageContext);
	}
	
}