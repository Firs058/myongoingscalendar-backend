package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author firs
 */
@Repository
public interface UserTitleRepository extends JpaRepository<UserTitleEntity, Long> {
}
