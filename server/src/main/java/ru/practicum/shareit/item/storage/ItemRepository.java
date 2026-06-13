package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> search(@Param("text") String text);

    @Query("SELECT i FROM Item i WHERE i.request.id IN :requestIds")
    List<Item> findByRequestIdIn(@Param("requestIds") List<Long> requestIds);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId")
    List<Item> findByRequestId(@Param("requestId") Long requestId);
}