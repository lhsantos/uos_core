package org.unbiquitous.uos.core.messageEngine.messages;

import java.util.HashMap;
import java.util.Map;

import org.unbiquitous.json.JSONException;
import org.unbiquitous.json.JSONObject;
import org.unbiquitous.uos.core.applicationManager.CallContext;


public class Response extends Message{
	
	private Map<String,Object> responseData;
	
	private CallContext messageContext;
	
	public Response() {
		setType(Message.Type.SERVICE_CALL_RESPONSE);
	}

	public Map<String,Object> getResponseData() {
		return responseData;
	}
	
	public Object getResponseData(String key) {
		if (responseData != null)
			return responseData.get(key);
		else
			return null;
	}
	
	public String getResponseString(String key) {
		return (String) getResponseData(key);
	}

	public void setResponseData(Map<String,Object> responseData) {
		this.responseData = responseData;
	}
	
	public Response addParameter(String key, Object value){
		if (responseData == null){
			responseData = new HashMap<String, Object>();
		}
		responseData.put(key, value);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		if (!( obj instanceof Response)){
			return false;
		}
		Response temp = (Response) obj; 
		
		if (	!( this.responseData == temp.responseData || (this.responseData != null && this.responseData.equals(temp.responseData)))){
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		if (this.responseData != null){
			hash += this.responseData.hashCode();
		}
		return hash;
	}

	public CallContext getMessageContext() {
		return messageContext;
	}

	public void setMessageContext(CallContext messageContext) {
		this.messageContext = messageContext;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();
		if (this.responseData != null){
			json.put("responseData", this.responseData);
		}
		return json;
	}

	public static Response fromJSON(JSONObject json) throws JSONException {
		Response r = new Response();
		Message.fromJSON(r, json);
		if (json.has("responseData")){
			r.responseData = json.optJSONObject("responseData").toMap();
		}
		return r;
	}
	
	@Override
	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return super.toString();
		}
	}
}
