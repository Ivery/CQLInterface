package main;

import io.Reader;
import io.Writer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import tables.BasicEntity;
import tables.Entity;
import tables.Relationship;


public class Main {
	
	HashMap<String, Relationship> relationships = new HashMap<String, Relationship>();
	HashMap<String, Entity> entities = new HashMap<String, Entity>();
	HashMap<String, BasicEntity> basicEntities = new HashMap<String, BasicEntity>();
	String defaultSchemaFilePath = "./accessories/savedSchema";
	
	public void intitialization() throws FileNotFoundException{
		// read from file about the tables
		Reader reader = new Reader();
		reader.loadSavedSchema(defaultSchemaFilePath, relationships, entities, basicEntities);
	}
	
	public void readCommand() throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			while(true){
				System.out.println("Do you want to \n(1) Create a new entity type \n(2) Create a new relationship \n(3) Search a query \n(4)* List existing tables \n(5) Load saved schema \n(6) Save current schema \n(0) Exit\n");
				
				int option = Integer.valueOf(br.readLine());
				switch(option){
				case 1:
					new Reader().createTable(relationships, entities, basicEntities);
					break;
				case 2:
					new Reader().createRelationship(relationships, entities, basicEntities);
					break;
				case 3:
					new Reader().searchQuery(relationships, entities, basicEntities);
					break;
				case 4: 
					break;
				case 5:
					System.out.println("What's the path of input schema file?");
					Scanner scanner = new Scanner(System.in);
					new Reader().loadSavedSchema(scanner.nextLine(), relationships, entities, basicEntities);
					scanner.close();
					System.out.println("Loaded");
					break;
				case 6:
					System.out.print("Where do you want to save the schema?");
					Scanner scanner2 = new Scanner(System.in); 
					new Writer().saveCurrentSchema(scanner2.nextLine(), relationships, entities, basicEntities);
					scanner2.close();
					break;
				case 0:return;
				}
			}	
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		Main newRun = new Main();
		newRun.intitialization();
		newRun.readCommand();
	}
	
}
