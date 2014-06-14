package org.unbiquitous.uos.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("serial")
public class InitialProperties extends HashMap<String, Object> {

	private boolean readOnly;

	public InitialProperties() {}
	
	public InitialProperties(ResourceBundle bundle) {
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			this.put(key, bundle.getObject(key));
		}
	}
	
	public InitialProperties(Map<String, Object> initMap) {
		super(initMap);
	}

	public String getString(String key) {
		if (!this.containsKey(key)) return null;
		return (String) get(key);
	}
	
	public Integer getInt(String key) {
		if (!this.containsKey(key)) return null;
		Object value = get(key);
		if (value instanceof Integer) return (Integer) value;
		else return Integer.parseInt(value.toString());
	}
	
	public Boolean getBool(String key) {
		if (!this.containsKey(key)) return null;
		Object value = get(key);
		if (value instanceof Boolean) return (Boolean) value;
		else return Boolean.parseBoolean(value.toString());
	}
	
	@Override
	public Object put(String key, Object value) {
		if (readOnly){
			throw new IllegalAccessError("Can't change properties after init");
		}
		return super.put(key, value);
	}
	
	public void markReadOnly(){
		this.readOnly = true;
	}
}
