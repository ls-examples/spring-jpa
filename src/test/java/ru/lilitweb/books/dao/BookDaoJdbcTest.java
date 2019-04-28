package ru.lilitweb.books.dao;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.lilitweb.books.domain.Book;
import ru.lilitweb.books.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@JdbcTest
@Import({BookDaoJdbc.class, DataSourceAutoConfiguration.class})
public class BookDaoJdbcTest {

    @Autowired
    BookDao bookDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/data/bookDaoJdbc/beforeTestingCount.sql")
    public void count() {
        bookDao.count();
        assertEquals(2, bookDao.count());
    }

    @Test
    public void insert() {
        Book book = new Book(
                "Руслан и Людмила",
                2019,
                "Описание",
                1);
        bookDao.insert(book);

        int countRecords = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "book", String.format(
                "id=%d and title='%s' and year=%d and description='%s' and author_id='%s'",
                book.getId(),
                book.getTitle(),
                book.getYear(),
                book.getDescription(),
                book.getAuthorId()
        ));

        assertTrue(book.getId() > 0);
        assertEquals(1, countRecords);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/data/bookDaoJdbc/beforeTestingUpdate.sql")
    public void update() {
        Book book = bookDao.getById(1);
        book.setTitle("new title");
        book.setDescription("new description");
        book.setYear(book.getYear() + 1);
        bookDao.update(book);
        int countRecords = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "book", String.format(
                "id=%d and title='%s' and year=%d and description='%s' and author_id='%s'",
                book.getId(),
                book.getTitle(),
                book.getYear(),
                book.getDescription(),
                book.getAuthorId()
        ));

        assertEquals(1, countRecords);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/data/bookDaoJdbc/beforeTestingDelete.sql")
    public void delete() {
        Book book = bookDao.getById(1);
        bookDao.delete(1);
        int countRecords = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "book", String.format(
                "id=%d",
                book.getId()
        ));

        assertEquals(0, countRecords);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/data/bookDaoJdbc/beforeTestingGetById.sql")
    public void getById() {
        Book foundedBook = bookDao.getById(1);
        assertEquals(1, foundedBook.getId());
        assertEquals("test book title", foundedBook.getTitle());
        assertEquals(1994, foundedBook.getYear());
        assertEquals("test book description", foundedBook.getDescription());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/data/bookDaoJdbc/beforeTestingGetAll.sql")
    public void getAll() {
        List<Book> books = bookDao.getAll();
        assertEquals(2, books.size());

        assertEquals("test book title(1)", books.get(0).getTitle());
        assertEquals("test book description(2)", books.get(1).getDescription());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/data/bookDaoJdbc/beforeTestingGetAllByGenres.sql")
    public void getAllByGenres() {
        int[] genres = {1, 2};
        List<Book> books = bookDao.getAllByGenres(genres);
        assertEquals(2, books.size());

        assertEquals("test book title(1)", books.get(0).getTitle());
        assertEquals("test book title(2)", books.get(1).getTitle());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/data/bookDaoJdbc/beforeTestingGetAllByAuthorId.sql")
    public void getAllByAuthorId() {
        List<Book> books = bookDao.getAllByAuthorId(1);
        assertEquals(2, books.size());

        assertEquals("test book title(1)", books.get(0).getTitle());
        assertEquals("test book title(2)", books.get(1).getTitle());
    }

    @Test
    public void loadAuthors() {
        List<Book> books = new ArrayList<>();
        Book book = new Book(
                1,
                "Руслан и Людмила",
                2019,
                "Описание",
                1);
        books.add(book);
        UserDao userDao = mock(UserDao.class);

        when(userDao.getByIds(Arrays.asList(1))).thenReturn(Arrays.asList(new User(1, "test name")));
        bookDao.loadAuthors(books, userDao);
        assertEquals("test name", book.getAuthor().getFullName());
    }
}
