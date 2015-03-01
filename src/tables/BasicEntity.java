package tables;


public class BasicEntity {
	protected String entityTypeName;
	protected String name;

	public BasicEntity(){}
	
	public BasicEntity(String entityTypeName, String name){
		this.entityTypeName = entityTypeName;
		this.name = name;
	}
	
	public void setEntityTypeName(String entityTypeName){
		this.entityTypeName = entityTypeName;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getEntityTypeName(){
		return entityTypeName;
	}
	
	public String getName(){
		return name;
	}
	
}
