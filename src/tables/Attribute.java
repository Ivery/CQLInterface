package tables;

public class Attribute {
	protected String name;
	protected String basicType;
	protected String context;
	
	
	public Attribute(){}
	
	public Attribute(String name){
		this.name = name;
	}
	
	public Attribute(String name, String basicType, String context){
		this.name = name;
		this.basicType = basicType;
		this.context = context;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setBasicType(String basicType){
		this.basicType = basicType;
	}
	
	public void setContext(String context){
		this.context = context;
	}
	
	public String getName(){
		return name;
	}
	
	public String getBasicType(){
		return basicType;
	}
	
	public String getContext(){
		return context;
	}
	
}
