package tables;


public class BasicEntity {
	protected String entityTypeName;
	protected String name;
	protected String context;

	public BasicEntity(){}
	
	public void setEntityTypeName(String entityTypeName){
		this.entityTypeName = entityTypeName;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setContext(String context){
		this.context = context;
	}
	
	public String getEntityTypeName(){
		return entityTypeName;
	}
	
	public String getName(){
		return name;
	}
	
	public String getContext(){
		return context;
	}
	
}
