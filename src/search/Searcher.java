package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;

import parser.SQLParser;
/*import query.parser.ParserResult;
import query.parser.StatementParserManager;
import query.tree.QueryTreeManager;
import query.tree.stmt.Node;*/
import tables.BasicEntity;
import tables.Entity;
import tables.Relationship;

public class Searcher {

	
	public void SearchQuery(String query, HashMap<String, Relationship> relationships, HashMap<String, Entity> entities, HashMap<String, BasicEntity> basicEntities) throws Exception{
		SQLParser sqlParser = new SQLParser(relationships, entities, basicEntities);
		
		String cqlQuery = sqlParser.parse(query);
		System.out.println(cqlQuery);
		
		
	/*	File[] files=new File("/home/jiahui/workspace/CQS/datatype/").listFiles();
		HashMap<String,String> views=new HashMap<String,String>();
		for (int i=0;i<files.length;i++){
			String content = "";
			if (!files[i].isFile()) continue;
			BufferedReader reader=new BufferedReader(new FileReader(files[i]));
			String line = "";
			while ((line=reader.readLine())!=null)
				content=content+line+"\n";
			views.put(files[i].getName(),content);
			reader.close();
		}
		
		ParserResult ps=StatementParserManager.parseQuery(cqlQuery, views);
		Node n=QueryTreeManager.translate(ps);
		int c=0;
		while (n.nextDataInfo()){
			System.out.println(n.getCurrentDataInfo());
			c++;			
		}
		System.out.println(c);*/
	}
	
}
