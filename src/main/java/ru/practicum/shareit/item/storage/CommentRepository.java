package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemOrderByCreatedDesc(Item item);

    @Query("SELECT c FROM Comment c WHERE c.item IN :items ORDER BY c.created DESC")
    List<Comment> findByItemsOrderByCreatedDesc(@Param("items") List<Item> items);
}
