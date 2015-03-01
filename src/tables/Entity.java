package tables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Entity {
	
	protected String entityTypeName;
	protected String name;
	protected String basicType;
	protected Set<String> contexts;
	protected HashMap<String, Attribute> attributeMap;

	public Entity(){
		this.contexts = new HashSet<String>();
		this.attributeMap = new HashMap<String, Attribute>();
	}
	
	public Entity(String entityTypeName, String name, String basicType, Set<String> contexts, HashMap<String, Attribute> attributeMap){
		this.entityTypeName = entityTypeName;
		this.name = name;
		this.basicType = basicType;
		this.contexts = new HashSet<String>();
		contexts.addAll(contexts);
		this.attributeMap = new HashMap<String, Attribute>();
		attributeMap.putAll(attributeMap);
	}
	
	public void setEntityTypeName(String entityTypeName){
		this.entityTypeName = entityTypeName;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setBasicType(String basicType){
		this.basicType = basicType;
	}
	
	public void addContext(String context){
		this.contexts.add(context);
	}
	
	public void addContexts(Set<String> contexts){
		this.contexts.addAll(contexts);
	}
	
	public void addAttribute(String name, String basicType, String context){
		this.attributeMap.put(name, new Attribute(name, basicType, context));
	}
	
	public String getEntityTypeName(){
		return entityTypeName;
	}
	
	public String getName(){
		return name;
	}
	
	public String getBasicType(){
		return basicType;
	}
	
	public Set<String> getContexts(){
		return contexts;
	}
	
	public HashMap<String, Attribute> getAllAttributes(){
		return this.attributeMap;
	}
	
	public Attribute getAttribute(String name){
		return this.attributeMap.get(name);
	}
	
}
