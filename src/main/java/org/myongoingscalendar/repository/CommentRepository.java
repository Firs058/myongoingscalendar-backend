package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("select count (c.id) from CommentEntity c where c.ongoingEntity.tid = ?1")
    int getCommentsTotal(Long tid);

    @Query(value = "select count (c.id) from comments c where c.tid = ?1 and c.path = CAST(?2 AS ltree)", nativeQuery = true)
    int getCommentsCountFromPath(Long tid, String path);

    @Query(value = "select cast(c.path as TEXT) from comments c where c.id = ?1", nativeQuery = true)
    String gePathtById(Long id);

    Optional<CommentEntity> getByIdAndOngoingEntity_Tid(Long id, Long tid);

    List<CommentEntity> findByUserEntity_Id(Long userid);
}


