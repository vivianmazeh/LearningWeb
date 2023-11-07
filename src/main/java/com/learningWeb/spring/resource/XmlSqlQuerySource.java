package com.learningWeb.spring.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.annotation.PostConstruct;

public class XmlSqlQuerySource implements SqlQuerySource {

	private Map<String, String> sqlQueries = new HashMap<>();
	@Override
	public String getSqlQuery(String queryName) {
		
		return sqlQueries.get(queryName);
	}
	
	//The @PostConstruct annotation in Java is used to specify a method 
	//that should be executed after an instance of a class has been initialized
	@PostConstruct
	public void loadQueries() throws SAXException, IOException {
		
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			 org.w3c.dom.Document document = builder.parse(new ClassPathResource("sql_queries.xml").getInputStream());
	         NodeList queryNodes = ((org.w3c.dom.Document) document).getElementsByTagName("query");
	         
	         for (int i = 0; i < queryNodes.getLength(); i++) {
	                Node queryNode = queryNodes.item(i);
	                String queryName = queryNode.getAttributes().getNamedItem("name").getTextContent();
	                String queryText = queryNode.getTextContent();

	                sqlQueries.put(queryName, queryText);
	            }
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}             
	}
	

}
