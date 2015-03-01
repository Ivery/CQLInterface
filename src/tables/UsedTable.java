package tables;

import java.util.HashMap;

import parser.SQLParser;
import utils.UserException;

public class UsedTable {
	
	protected String alias;
	protected String tableName;
	protected String tableType;
	protected SQLParser sqlParser;
	
	public String getTableType(){
		return tableType;
	}
	
	public String getTableName(){
		return tableName;
	}
	
	public String getAlias(){
		return alias;
	}
	
	public UsedTable(SQLParser sqlParser, String tableName) throws UserException{
		
		if(sqlParser.entities.containsKey(tableName)==false && sqlParser.basicEntities.containsKey(tableName)==false && sqlParser.relationships.containsKey(tableName)==false){
			throw new UserException("Table " + tableName + " not exists");
		}
		
		if(sqlParser.entities.containsKey(tableName)==true){
			tableType = "entities";
		}else if(sqlParser.basicEntities.containsKey(tableName)==true){
			tableType = "basicEntities";
		}else if(sqlParser.relationships.containsKey(tableName)==true){
			tableType = "relationships";
		}
		
		this.sqlParser = sqlParser;
		this.tableName = new String(tableName);
		this.alias = new String(tableName);	
	}
	
	public UsedTable(SQLParser sqlParser, String tableName, String alias) throws UserException{
		
		if(sqlParser.entities.containsKey(tableName)==false && sqlParser.basicEntities.containsKey(tableName)==false && sqlParser.relationships.containsKey(tableName)==false){
			throw new UserException("Table " + tableName + " not exists");
		}
		
		if(sqlParser.entities.containsKey(tableName)==true){
			tableType = "entities";
		}else if(sqlParser.basicEntities.containsKey(tableName)==true){
			tableType = "basicEntities";
		}else if(sqlParser.relationships.containsKey(tableName)==true){
			tableType = "relationships";
		}
		
		this.sqlParser = sqlParser;
		this.tableName = new String(tableName);
		this.alias = new String(alias);
		
	}
	
	public void addAttribute(HashMap<String, String> usedAttributeTypes, String attribute, HashMap<String, Relationship> relationships, HashMap<String, Entity> entities, HashMap<String, BasicEntity> basicEntities) throws UserException{		
		String[] attributeInfo = attribute.split("\\.");
		
		String tableAlias = attributeInfo[0];
		String attributeName = attributeInfo[1];
		
		if(entities.containsKey(tableName)){
			Entity entity = entities.get(tableName);
			if(entity.getName().equals(attributeName)){
				usedAttributeTypes.put(attribute, entity.getEntityTypeName());
			}else if(entity.getAllAttributes().containsKey(attribute)){
				usedAttributeTypes.put(attribute, entity.getAllAttributes().get(attribute).getBasicType());
			}else{
				throw new UserException("attribute " + attribute + " not exist");
			}
		}else if(basicEntities.containsKey(tableName)){
			BasicEntity basicEntity = basicEntities.get(tableName);
			if(basicEntity.getName().equals(attributeName)){
				usedAttributeTypes.put(attribute, basicEntity.getEntityTypeName());
			}else{
				throw new UserException("attribute " + attribute + "not exist");
			}
			
		}else if(relationships.containsKey(tableName)){
			Relationship relationship = relationships.get(tableName);
			if(relationship.getEntity1Name().equals(attributeName)){
				usedAttributeTypes.put(attribute, relationship.getEntity1Type());
			}else if(relationship.getEntity2Name().equals(attributeName)){
				usedAttributeTypes.put(attribute, relationship.getEntity2Type());
			}else{
				throw new UserException("attribute " + attribute + "not exist");
			}	
		}
	}
	
	
	// HashMap<String, String> groupNos
	public void addKeyAttributes(HashMap<String, String> usedAttributeTypes, HashMap<String, Relationship> relationships, HashMap<String, Entity> entities, HashMap<String, BasicEntity> basicEntities){
		if(tableType.equals("entities")){
			Entity entity = this.sqlParser.entities.get(tableName);
			if(usedAttributeTypes.containsKey(tableName+"."+entity.name) == false){
				usedAttributeTypes.put(tableName+"."+entity.name, entity.basicType);
			}
		}else if(tableType.equals("basicEntities")){
			BasicEntity basicEntity = this.sqlParser.basicEntities.get(tableName);
			if(usedAttributeTypes.containsKey(tableName+"."+basicEntity.name) == false){
				usedAttributeTypes.put(tableName+"."+basicEntity.name, basicEntity.entityTypeName);
			}
		}else if(tableType.equals("relationship")){
			Relationship relationship = this.sqlParser.relationships.get(tableName);
			if(usedAttributeTypes.containsKey(tableName+"."+relationship.entity1Name) == false){
				usedAttributeTypes.put(tableName+"."+relationship.entity1Name, relationship.entity1Type);
			}else if(usedAttributeTypes.containsKey(tableName+"."+relationship.entity2Name) == false){
				usedAttributeTypes.put(tableName+"."+relationship.entity2Name, relationship.entity2Type);
			}
		}
	}
	
	public void print(){
		System.out.println("alias:" + alias + ", tableName:" + tableName + ", tableType:" + tableType);
	}
	
}
