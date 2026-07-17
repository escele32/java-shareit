package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

   List<Item> findByOwnerId(Long ownerId);

    @Query(value = """
    SELECT * FROM items
    WHERE is_available = TRUE
      AND (
          name IS NOT NULL AND name ILIKE concat('%', :text, '%')
          OR description IS NOT NULL AND description ILIKE concat('%', :text, '%')
      )
    """, nativeQuery = true)
    List<Item> search(@Param("text") String text);

}
