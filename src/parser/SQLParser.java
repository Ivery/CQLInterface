package parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import tables.BasicEntity;
import tables.Entity;
import tables.Relationship;
import tables.UsedTable;
import utils.UserException;

public class SQLParser {
	
	// TODO: Haven't support calculating an expression in SELECT clause
	
	public static HashMap<String, Relationship> relationships = new HashMap<String, Relationship>();
	public static HashMap<String, Entity> entities = new HashMap<String, Entity>();
	public static HashMap<String, BasicEntity> basicEntities = new HashMap<String, BasicEntity>();
	// alias to table
	protected HashMap<String, UsedTable> usedTables = new HashMap<String, UsedTable>();
	// alias table.attributeName to basicType
	protected HashMap<String, String> usedAttributeTypes = new HashMap<String, String>();
	// attribute that are in the same group
	protected HashMap<String, Integer> groupNos = new HashMap<String, Integer>();
	protected HashMap<Integer, String> groupValues = new HashMap<Integer, String>();
	protected HashMap<Integer, String> groupToTypes = new HashMap<Integer, String>();
	
	protected List<String> selectedColumns = new ArrayList<String>();
	protected List<String> selectedColumnsAlias = new ArrayList<String>();
	
	String SQLQuery;
	
	public SQLParser(){}
	
	public SQLParser(HashMap<String, Relationship> relationships, HashMap<String, Entity> entities, HashMap<String, BasicEntity> basicEntities){
		this.relationships.putAll(relationships);
		this.entities.putAll(entities);
		this.basicEntities.putAll(basicEntities);
	}

	public String parse(String query) throws Exception{
		String cqlQuery = "";
		
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = query;
		int failed = sqlParser.checkSyntax();
		if(failed == 0){
			parse(sqlParser);
			groupToTypes();			
			String cql = translate();
			
		}else{
			System.out.println(sqlParser.getErrormessage());
		}
		
		return cqlQuery;
	}
	
	public String parse(TGSqlParser sqlParser) throws Exception{
		String cqlQuery = "";
				
		for(int index = 0; index < sqlParser.sqlstatements.size(); index++){
			TCustomSqlStatement statement = sqlParser.sqlstatements.get(index);
			
			if(statement.sqlstatementtype == ESqlStatementType.sstselect){
				TSelectSqlStatement selectStatement = (TSelectSqlStatement) statement;
				if(selectStatement.isCombinedQuery() == false){
					
					// Handle from first, then select, then where
					
					// FROM clause
					for(int i = 0; i < selectStatement.joins.size(); i++){
						TJoin join = selectStatement.joins.getJoin(i);
						if(join.getKind() == TBaseType.join_source_fake){
							
							String tableName = join.getTable().toString().toUpperCase();
							
							TAliasClause aliasClause = join.getTable().getAliasClause();
							String alias = new String(tableName);
							
							if(aliasClause != null){
								alias = aliasClause.toString().toUpperCase();
							}
							
							UsedTable usedTable = new UsedTable(this, tableName, alias);
							usedTables.put(alias, usedTable);
						}
					}
					
					// WHERE clause
					if (selectStatement.getWhereClause() != null){
						WhereCondition whereCondition = new WhereCondition(selectStatement.getWhereClause().getCondition());
						whereCondition.groupAttributes(groupNos, groupValues, usedTables, usedAttributeTypes, relationships, entities, basicEntities);
					}
					
					// SELECT clause
					for(int i = 0; i < selectStatement.getResultColumnList().size(); i++){
						
						TResultColumn resultColumn = selectStatement.getResultColumnList().getResultColumn(i);
						String columnName = resultColumn.getExpr().toString().toUpperCase();
						
						String[] columnInfo = columnName.split("\\.");
						
						String tableAlias = columnInfo[0];
						if(usedTables.containsKey(tableAlias) == false){
							throw new UserException(tableAlias + " doesn't exist");
						}
						String attributeName = columnInfo[1];
						
						TAliasClause aliasClause = resultColumn.getAliasClause();
						String alias = new String(attributeName);
						if(aliasClause!=null){
							alias = resultColumn.getAliasClause().toString().toUpperCase();
						}
						selectedColumns.add(columnName);
						selectedColumnsAlias.add(alias);
						
						usedTables.get(tableAlias).addAttribute(usedAttributeTypes, columnName, relationships, entities, basicEntities);
						
						if(groupNos.containsKey(columnName) == false){
							groupNos.put(columnName, Collections.max(groupNos.values())+1);
						}
						
					}
					
					System.out.println("group nos = " + groupNos);
					
					for(UsedTable usedTable : usedTables.values()){
						usedTable.addKeyAttributes(usedAttributeTypes, relationships, entities, basicEntities);
						
						usedTable.print();
					}
					
				}else{
					System.out.println("Currently we only support simple SELECT-FROM-WHERE statement");
				}
				
			}else{
				System.out.println("Currently we only support simple SELECT-FROM-WHERE statement");
			}
		}
		
		System.out.println("used attribute types = " + usedAttributeTypes);
		
		return cqlQuery;
	}
	
	public String translate(){
		String cql = "";
		
		String selectClause = "";
		String fromClause = "";
		String whereClause = "";
		
		// select
		/* 				
		 * int entity1GroupIndex = groupNos.get(entity1FullName);
		 * String entity1Alias = groupToTypes.get(entity1GroupIndex) + String.valueOf(entity1GroupIndex);
		 * 
		 */
		
		
		// FROM clause
		for(Entry<Integer, String> groupToType : groupToTypes.entrySet()){
			int index = groupToType.getKey();
			if(groupValues.containsKey(index)==false){
				fromClause += (" #" + groupToTypes.get(index) + " AS " + "#" + groupToTypes.get(index) + String.valueOf(index) + ",");
			}
		}
		if(fromClause.length()!=0){
			fromClause = "FROM" + fromClause.substring(0, fromClause.length()-1);
		}
		System.out.println(fromClause);
		
		
		
		// WHERE clause
		List<String> patterns = new ArrayList<String>();
		for(UsedTable usedTable : usedTables.values()){
			// relationship or entities or basicEntities
			String tableType = usedTable.getTableType();
			// The alias for current table
			String alias = usedTable.getAlias();
			// The name of current table
			String name = usedTable.getTableName();
						
			if(tableType.equals("relationships")){	// only deal with the two key attributes
				Relationship relationship = relationships.get(name);
				String pattern0 = "[";
				
				String entity1Name = relationship.getEntity1Name();
				String entity1Type = relationship.getEntity1Type();
				String entity1FullName = alias + "." + entity1Name;
				
				// check whether the value for entity1 is assigned
				int entity1GroupIndex = groupNos.get(entity1FullName);
				if(groupValues.containsKey(entity1GroupIndex)){	// If so, we just need to value when constructing the relationship pattern
					String assignedValue = groupValues.get(entity1GroupIndex);
					pattern0 += assignedValue + " ";
				}else{
					// Otherwise
					String entity1Alias = groupToTypes.get(entity1GroupIndex) + String.valueOf(entity1GroupIndex);
					if(entities.containsKey(entity1Type)){	// If it's a compound entity, also need the entity description pattern
						Entity entity1TypeSchema = entities.get(entity1Type); 
						String pattern1 = "[#";
						pattern1 += entity1Alias;
						for(String context : entity1TypeSchema.getContexts()){
							pattern1 += " " + context;
						}
						pattern1 += "]<20>";
						patterns.add(pattern1);
						
						pattern0 += "#" + entity1Alias + " ";
					}else if(basicEntities.containsKey(entity1Type)){						
						pattern0 += "#" + entity1Alias + " ";
					}
				}
				
				String entity2Name = relationship.getEntity2Name();
				String entity2Type = relationship.getEntity2Type();
				String entity2FullName = alias + "." + entity2Name;
				
				// check whether the value for entity1 is assigned
				int entity2GroupIndex = groupNos.get(entity2FullName);
				if(groupValues.containsKey(entity2GroupIndex)){	// If so, we just need to value when constructing the relationship pattern
					String assignedValue = groupValues.get(entity2GroupIndex);
					pattern0 += assignedValue +" ";
				}else{
					// Otherwise
					String entity2Alias = groupToTypes.get(entity2GroupIndex) + String.valueOf(entity2GroupIndex);
					if(entities.containsKey(entity2Type)){	// If it's a compound entity, also need the entity description pattern
						Entity entity2TypeSchema = entities.get(entity2Type); 
						String pattern2 = "[#";
						pattern2 += entity2Alias;
						for(String context : entity2TypeSchema.getContexts()){
							pattern2 += " " + context;
						}
						pattern2 += "]<20>";
						patterns.add(pattern2);
						
						pattern0 += "#" + entity2Alias +" ";
					}else if(basicEntities.containsKey(entity2Type)){						
						pattern0 += "#" + entity2Alias +" ";
					}
				}
				
				String context = relationship.getContext();
				pattern0 += context + "]<20>";
				patterns.add(pattern0);
			}else if(tableType.equals("basicEntities")){	// don't need to do anything for basic entities 
				
			}else if(tableType.equals("entities")){	//
				// add key attribute first
				
				
				// add every attribute used
				
			}
			
		}
		// combine patterns into where clause
		System.out.println(patterns.toString());
		
		
		
		
		return cql;
	}
	
	public void groupToTypes(){
		for(Entry<String, Integer> groupNo : groupNos.entrySet()){
			int index = groupNo.getValue();
			if(groupToTypes.containsKey(index) == false){
				groupToTypes.put(index, usedAttributeTypes.get(groupNo.getKey()));
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		
		String query = "SELECT p.person1, p.person2 FROM FRIEND AS p WHERE p.person1 = \"A\"".toUpperCase();
		BasicEntity newBasicEntity = new BasicEntity("PERSON", "NAME");
		basicEntities.put("PERSON", newBasicEntity);
		Relationship newRelationship = new Relationship("FRIEND", "PERSON1", "PERSON", "PERSON2", "PERSON", "FRIEND");
		relationships.put("FRIEND", newRelationship);
		new SQLParser().parse(query);
	}
	
}
