package parser;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import tables.BasicEntity;
import tables.Entity;
import tables.Relationship;
import tables.UsedTable;
import utils.UserException;

public class WhereCondition implements IExpressionVisitor{
	
	// TODO: assumed the where condition is always like "attribute = attribute" or "attribute = value"
	// TODO: only handles 'AND'
	
	private TExpression condition;
	private List<String> columns = new ArrayList<String>();
	private List<String> values = new ArrayList<String>();
	
	Set<String> nodes = new HashSet<String>();
	private HashMap<String, Set<String>> linkedAttributes = new HashMap<String, Set<String>>();
	private HashMap<String, Integer> groupNos = new HashMap<String, Integer>();
	private HashMap<Integer, String> groupValues = new HashMap<Integer, String>();
	
	private HashMap<String, String> usedAttributeTypes = new HashMap<String, String>();
	private HashMap<String, UsedTable> usedTables  = new HashMap<String, UsedTable>();
	
	private HashMap<String, Relationship> relationships = new HashMap<String, Relationship>(); 
	private HashMap<String, Entity> entities = new HashMap<String, Entity>(); 
	private HashMap<String, BasicEntity> basicEntities = new HashMap<String, BasicEntity>();
	
	public WhereCondition(TExpression clause)
	{
		this.condition = clause;
	}
	
	public void groupAttributes(HashMap<String, Integer> groupNos, HashMap<Integer, String> groupValues, HashMap<String, UsedTable> usedTables, HashMap<String, String> usedAttributeTypes, HashMap<String, Relationship> relationships, HashMap<String, Entity> entities, HashMap<String, BasicEntity> basicEntities) throws Exception{
		this.relationships.putAll(relationships);
		this.entities.putAll(entities);
		this.basicEntities.putAll(basicEntities);
		
		this.usedTables.putAll(usedTables);
		
		this.condition.inOrderTraverse(this);
		
		groupAttributes();
		
		groupNos.putAll(this.groupNos);
		groupValues.putAll(this.groupValues);
		usedAttributeTypes.putAll(this.usedAttributeTypes);
	}
	
	public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode){
		
		TExpression lcexpr = (TExpression) pnode;
		if (is_compare_condition(lcexpr.getExpressionType( )))
		{
			TExpression parent = lcexpr.getParentExpr();
			if(parent != null && parent.getExpressionType().toString().equals("logical_and_t") == false){
				try {
					throw new UserException("Only support AND to connect expressions");
				} catch (UserException e) {
					e.printStackTrace();
				}
			}
			
			TExpression leftExpr = (TExpression) lcexpr.getLeftOperand( );
			columns.add(leftExpr.toString().toUpperCase());
			nodes.add(leftExpr.toString().toUpperCase());
			
			try {
				addAttribute(leftExpr.toString().toUpperCase());
			} catch (UserException e1) {
				e1.printStackTrace();
			}
			
			if(lcexpr.getComparisonOperator().toString().equals("=")==false){
				try {
					throw new UserException("Only support = operator");
				} catch (UserException e) {
					e.printStackTrace();
				}
			}
			
			String value = lcexpr.getRightOperand().toString().toUpperCase();
			values.add(value);
			if(value.contains("\"")==false){
				try {
					addAttribute(value);
				} catch (UserException e) {
					e.printStackTrace();
				}
				nodes.add(value);
			}
		}
		return true;
	}
	
	public void groupAttributes() throws Exception{
		
		for(String curNode : nodes){
			Set<String> linkedNodes = new HashSet<String>();
			Set<String> currentLinkedNodes = new HashSet<String>();
			
			currentLinkedNodes.add(curNode);
			
			while(linkedNodes.size()!=currentLinkedNodes.size()){
				linkedNodes.addAll(currentLinkedNodes);
				
				for(int i = 0; i < columns.size(); i++){					
					if(values.get(i).contains("\"")==false){
						
						String column = columns.get(i);
						String value = values.get(i);
						for(String node : linkedNodes){
							if(node.equals(column)){
								currentLinkedNodes.add(value);
							}
							if(node.equals(value)){
								currentLinkedNodes.add(column);
							}
						}
					}
				}
			}
			
			linkedAttributes.put(curNode, currentLinkedNodes);
		}
		
		for(Entry<String, Set<String>> curNode : linkedAttributes.entrySet()){
			Set<String> linkedNodes = curNode.getValue();
			
			int groupNo = -1;
			
			for(String linkedNode : linkedNodes){
				if(groupNos.containsKey(linkedNode)){
					groupNo = groupNos.get(linkedNode);
					break;
				}
			}
			
			if(groupNo == -1){
				if(groupNos.size()==0){
					groupNo = 1;
				}else{
					groupNo = Collections.max(groupNos.values())+1;
				}
			}
			
			groupNos.put(curNode.getKey(), groupNo);
		}
		
		for(int i = 0; i < columns.size(); i++){					
			if(values.get(i).contains("\"")){
				String column = columns.get(i);
				String value = values.get(i);
				value = value.substring(1, value.length()-1).toUpperCase();
				
				int groupNo = groupNos.get(column);
				if(groupValues.containsKey(groupNo) && groupValues.get(groupNo).equals(value)==false){
					throw new Exception("value assigned conflicts");
				}else if(groupValues.containsKey(groupNo) == false){
					groupValues.put(groupNo, value);
				}
			}
		}
		
	}
	
	boolean is_compare_condition(EExpressionType t)
	{
		return ( ( t == EExpressionType.simple_comparison_t )
				|| ( t == EExpressionType.group_comparison_t ) || ( t == EExpressionType.in_t ) );
	}

	public void addAttribute(String attribute) throws UserException{
		String[] attributeInfo = attribute.split("\\.");
				
		String tableAlias = attributeInfo[0];
		String attributeName = attributeInfo[1];
		String tableName = usedTables.get(tableAlias).getTableName();
				
		
		if(entities.containsKey(tableName)){
			Entity entity = entities.get(tableName);
			if(entity.getName().equals(attributeName)){
				usedAttributeTypes.put(attribute, entity.getEntityTypeName());
			}else if(entity.getAllAttributes().containsKey(attributeName)){
				usedAttributeTypes.put(attribute, entity.getAllAttributes().get(attributeName).getBasicType());
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
}
