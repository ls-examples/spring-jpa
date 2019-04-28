package ru.lilitweb.books.service;

import ru.lilitweb.books.domain.Book;
import ru.lilitweb.books.domain.Genre;
import ru.lilitweb.books.domain.User;

import java.util.List;
import java.util.Optional;

public interface BookService {
    void add(Book book);
    void update(Book book);
    Optional<Book> getById(long id);

    List<Book> getAll();

    void delete(Book book);
}
