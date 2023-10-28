package com.learningWeb.spring.resource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// export PATH=/usr/local/Cellar/maven/3.9.5/bin:$PATH

import com.learningWeb.spring.model.Book;

@RestController
@RequestMapping("/education")

public class Controller {
	
	@GetMapping("/allbooks")
	public ResponseEntity < List<Book>> findAllBook(){
		
		List<Book> books = Stream.of(new Book("Vivian", 1,15.50), new Book("Ali", 2, 23.00),
				 new Book("Tong", 3, 23), new Book("Elias", 4, 34), new Book("Mia", 5, 54))
		.collect(Collectors.toList());	
			
		return ResponseEntity.ok(books);
		
	}

}
