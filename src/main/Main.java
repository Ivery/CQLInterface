package main;

import io.Reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import tables.BasicEntity;
import tables.Entity;
import tables.Relationship;


public class Main {
	
	HashMap<String, Relationship> relationships = new HashMap<String, Relationship>();
	HashMap<String, Entity> entities = new HashMap<String, Entity>();
	HashMap<String, BasicEntity> basicEntities = new HashMap<String, BasicEntity>();
	String defaultSchemaFilePath = "./accessories/savedSchema";
	
	public void intitialization(){
		// read from file about the tables
		Reader reader = new Reader();
		reader.loadSavedSchema(defaultSchemaFilePath, relationships, entities, basicEntities);
	}
	
	public void readCommand(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			while(true){
				System.out.println("Do you want to \n(1) Create a new entity type \n(2) Create a new relationship \n(3) Search a query \n(4) List existing tables \n(5) Load saved schema \n(6) Save current schema \n(0) Exit\n");
				
				int option = Integer.valueOf(br.readLine());
				switch(option){
				case 1:
					new Reader().createTable(relationships, entities, basicEntities);
					break;
				case 2:
					new Reader().createRelationship(relationships, entities, basicEntities);
					break;
				case 3:break;
				case 4: 
					new Reader().searchQuery(relationships, entities, basicEntities);
					break;
				case 5:break;
				case 6:break;
				case 0:return;
				}
			}	
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		new Main().readCommand();
	}
	
}
