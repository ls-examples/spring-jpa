package ru.lilitweb.books.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.lilitweb.books.domain.Book;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    @Qualifier("savingBookChannel")
    private DirectChannel savingBookChannel;

    @Autowired
    @Qualifier("getAllBookChannel")
    private DirectChannel getAllBookChannel;

    @Autowired
    @Qualifier("getBookChannel")
    private DirectChannel getBookChannel;

    @Autowired
    @Qualifier("deleteBookChannel")
    private DirectChannel deleteBookChannel;

    @Override
    public void add(Book book) {
        QueueChannel responseChannel = MessageChannels.queue(1).get();
        Message<Book> message =
                MessageBuilder.withPayload(book)
                        .setHeader(MessageHeaders.REPLY_CHANNEL, responseChannel)
                        .build();
        savingBookChannel.send(message);
        responseChannel.receive();
    }

    @Override
    public void update(Book book) {
        QueueChannel responseChannel = MessageChannels.queue(1).get();
        Message<Book> message =
                MessageBuilder.withPayload(book)
                        .setHeader(MessageHeaders.REPLY_CHANNEL, responseChannel)
                        .build();
        savingBookChannel.send(message);
        responseChannel.receive();
    }

    @Override
    public Optional<Book> getById(long id) {
        QueueChannel responseChannel = MessageChannels.queue(1).get();
        Message<Long> message =
                MessageBuilder.withPayload(id)
                        .setHeader(MessageHeaders.REPLY_CHANNEL, responseChannel)
                        .build();
        getBookChannel.send(message);
        Message reply = responseChannel.receive();
        return (Optional<Book>) reply.getPayload();
    }

    @Override
    public List<Book> getAll() {
        QueueChannel responseChannel = MessageChannels.queue(1).get();
        Message message =
                MessageBuilder.withPayload("")
                        .setHeader(MessageHeaders.REPLY_CHANNEL, responseChannel)
                        .build();
        getAllBookChannel.send(message);
        Message reply = responseChannel.receive();
        return (List<Book>) reply.getPayload();
    }

    @Override
    public void delete(Book book) {
        Message<Book> message =
                MessageBuilder.withPayload(book)
                        .build();
        deleteBookChannel.send(message);
    }
}
