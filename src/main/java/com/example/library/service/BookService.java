package com.example.library.service;

import com.example.library.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();

    Book addBook(Book book);

    void deleteBook(String id);
}
