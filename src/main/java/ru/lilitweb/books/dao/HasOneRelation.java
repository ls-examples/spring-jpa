package ru.lilitweb.books.dao;

import lombok.Builder;
import ru.lilitweb.books.domain.Entity;

import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Builder
public class HasOneRelation<E, R extends Entity> {
    private List<E> entities;
    private ToIntFunction<E> foreignKeyGetter;
    private RelationSetter<E, R> relationSetter;
    private RelationLoader<R> relationLoader;

    public void load() {
        List<Integer> relationsIds = entities.stream().
                mapToInt(foreignKeyGetter).
                distinct().
                boxed().
                collect(Collectors.toList());

        List<R> relations = relationLoader.getByIds(relationsIds);

        entities.forEach(e -> relations.stream().
                filter(r -> foreignKeyGetter.applyAsInt(e) == r.getId()).
                findFirst().ifPresent(r -> relationSetter.apply(e, r)));
    }

    @FunctionalInterface
    public interface RelationSetter<E, R> {
        void apply(E e, R r);
    }
}