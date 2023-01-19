package ru.clevertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.clevertec.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByNameIgnoreCase(String tagName);

    @Query(value = "SELECT tag.id, tag.name "
            + "FROM tag "
            + "         JOIN certificate_tag ct ON tag.id = ct.tag_id "
            + "         JOIN orders o ON ct.certificate_id = o.certificate_id "
            + "WHERE o.user_id = "
            + "      (SELECT user_id "
            + "       FROM orders "
            + "       GROUP BY user_id "
            + "       ORDER BY SUM(cost) DESC "
            + "       LIMIT 1) "
            + "GROUP BY tag.id, tag.name "
            + "ORDER BY COUNT(tag.id) DESC "
            + "LIMIT 1", nativeQuery = true)
    Optional<Tag> findMostWidelyUsedTag();

    @Query(value = "SELECT last_value FROM tag_id_seq", nativeQuery = true)
    Integer findLastSequenceValue();
}
