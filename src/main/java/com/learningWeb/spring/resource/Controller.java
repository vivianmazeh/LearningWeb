package com.learningWeb.spring.resource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// export PATH=/usr/local/Cellar/maven/3.9.5/bin:$PATH
import org.xml.sax.SAXException;

import com.learningWeb.spring.model.Book;

@RestController
@RequestMapping("/education")

public class Controller {

	// @Autowired: used for automatic dependency injection
	@Autowired 
	private JdbcTemplate jdbctemplate;
	private XmlSqlQuerySource queries = new XmlSqlQuerySource() ;
	
	public Controller() {
		try {
			queries.loadQueries();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@GetMapping("/allbooks")
	public ResponseEntity < List<Book>> findAllBook(){
		
		List<Book> books = null;
		
		String sql = queries.getSqlQuery("selectAllBooks");		
		
		 books = jdbctemplate.query(sql, (re, rowNum)-> new Book(re.getString("name"),
		  re.getInt("id"), re.getFloat("price")));;
				
		return ResponseEntity.ok(books);
		
	}

}
