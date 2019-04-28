package ru.lilitweb.books.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import ru.lilitweb.books.repostory.BookRepository;

@Configuration
public class MessagingConfig {
    @Autowired
    private BookRepository bookRepository;

    @Bean
    public DirectChannel savingBookChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public DirectChannel getAllBookChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public DirectChannel getBookChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public DirectChannel deleteBookChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow getAllBookFlow() {
        return IntegrationFlows.from("getAllBookChannel")
                .handle(bookRepository, "findAll")
                .get();
    }

    @Bean
    public IntegrationFlow deleteBookFlow() {
        return IntegrationFlows.from("deleteBookChannel")
                .handle(bookRepository, "delete")
                .get();
    }

    @Bean
    public IntegrationFlow savingBookFlow() {
        return IntegrationFlows.from("savingBookChannel")
                .handle(bookRepository, "save")
                .get();
    }

    @Bean
    public IntegrationFlow getBookFlow() {
        return IntegrationFlows.from("getBookChannel")
                .handle(bookRepository, "findById")
                .get();
    }
}
