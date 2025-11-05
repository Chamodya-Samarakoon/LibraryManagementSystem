package com.example.library.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.library.model.Book;
import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByPublicationYear(int publicationYear);
}
