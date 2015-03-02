package search;

import java.util.HashMap;

import parser.SQLParser;
import tables.BasicEntity;
import tables.Entity;
import tables.Relationship;

public class Searcher {

	
	public void SearchQuery(String query, HashMap<String, Relationship> relationships, HashMap<String, Entity> entities, HashMap<String, BasicEntity> basicEntities) throws Exception{
		SQLParser sqlParser = new SQLParser(relationships, entities, basicEntities);
		
		String cqlQuery = sqlParser.parse(query);
		System.out.println(cqlQuery);
		
	}
	
}
