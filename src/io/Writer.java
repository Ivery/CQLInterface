package io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

import tables.Attribute;
import tables.BasicEntity;
import tables.Entity;
import tables.Relationship;

public class Writer {

	
	public void saveCurrentSchema(String path, HashMap<String, Relationship> relationships, HashMap<String, Entity> entities, HashMap<String, BasicEntity> basicEntities) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		
		writer.println("BasicEntities:");
		for(BasicEntity basicEntity : basicEntities.values()){
			String basicEntityLine = basicEntity.getEntityTypeName() + ";" + basicEntity.getName();
			writer.println(basicEntityLine);
		}
		
		writer.println("Entities:");
		for(Entity entity : entities.values()){
			String entityLine = entity.getEntityTypeName() + ";" + entity.getName() + ";" + entity.getBasicType() + ";";
			
			Set<String> contexts = entity.getContexts();
			String contextLine = "";
			for(String context : contexts){
				if(contextLine.length()!=0){
					contextLine += ",";
				}
				contextLine += context;
			}
			entityLine += contextLine + ";";
			
			String attributeLine = "";
			for(Attribute attribute : entity.getAllAttributes().values()){
				String attributeInfo = "";
				
				attributeInfo = attribute.getName() + "," + attribute.getBasicType() + "," + attribute.getContext();
				
				if(attributeLine.length()!=0){
					attributeLine += ".";
				}
				attributeLine += attributeInfo;
			}
			entityLine += attributeLine;
			
			writer.println(entityLine);
		}
		
		System.out.println("Relationships:");
		for(Relationship relationship : relationships.values()){
			String relationshipLine = "";
			
			relationshipLine += relationship.getRelationshipTypeName() + ";" + relationship.getEntity1Name() + ";" + relationship.getEntity1Type() + ";" + relationship.getEntity2Name() + ";" + relationship.getEntity2Type() + ";" + relationship.getContext();
			
			writer.println(relationshipLine);
		}
		
		writer.close();
	}
	
}
