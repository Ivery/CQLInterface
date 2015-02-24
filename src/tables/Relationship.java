package tables;

public class Relationship {
	protected String relationshipTypeName;
	protected String entity1Name;
	protected String entity1Type;
	protected String entity2Name;
	protected String entity2Type;
	protected String context;
	
	public Relationship(){}
	
	public Relationship(String relationshipTypeName){
		this.relationshipTypeName = relationshipTypeName;
	}
	
	public Relationship(String relationshipTypeName, String entity1Name, String entity1Type, String entity2Name, String entity2Type, String context){
		this.relationshipTypeName = relationshipTypeName;
		this.entity1Name = entity1Name;
		this.entity1Type = entity1Type;
		this.entity2Name = entity2Name;
		this.entity2Type = entity2Type;
		this.context = context;
	}
	
	public void setRelationshipTypeName(String relationshipTypeName){
		this.relationshipTypeName = relationshipTypeName;
	}
	
	public void setEntity1Name(String entity1Name){
		this.entity1Name = entity1Name;
	}
	
	public void setEntity1Type(String entity1Type){
		this.entity1Type = entity1Type;
	}
	
	public void setEntity2Name(String entity2Name){
		this.entity2Name = entity2Name;
	}
	
	public void setEntity2Type(String entity2Type){
		this.entity2Type = entity2Type;
	}
	
	public void setContext(String context){
		this.context = context;
	}
	
	public String getRelationshipTypeName(){
		return this.relationshipTypeName;
	}
	
	public String getEntity1Name(){
		return this.entity1Name;
	}
	
	public String getEntity1Type(){
		return this.entity1Type;
	}
	
	public String getEntity2Name(){
		return this.entity2Name;
	}
	
	public String getEntity2Type(){
		return this.entity2Type;
	}
	
	public String getContext(){
		return this.context;
	}
	
}
