package tables;

import java.util.HashSet;
import java.util.Set;

public class RelatedEntity {

	protected String attributeType = "";
	protected Set<String> contexts = new HashSet<String>();
	protected String attributeValue = "";
	
	public RelatedEntity(){
	}
	
	public void setAttributeType(String attributeType){
		this.attributeType = attributeType;
	}
	
	public void addContext(String context){
		this.contexts.add(context);
	}
	
	public void addContexts(HashSet<String> contexts){
		this.contexts.addAll(contexts);
	}
	
	public void setAttributeValue(String attributeValue){
		this.attributeValue = attributeValue;
	}
	
	public String getAttributeType(){
		return this.attributeType;
	}
	
	public Set<String> getContexts(){
		return this.contexts;
	}
	
	public String getAttributeValue(){
		return this.attributeValue;
	}
	
}
