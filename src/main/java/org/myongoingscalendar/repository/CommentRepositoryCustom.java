package org.myongoingscalendar.repository;

import org.myongoingscalendar.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author firs
 */
@Repository
public interface CommentRepositoryCustom {
    List<Comment> getCommentsAuthorized(Long tid, String path, int offset, Long userid);

    List<Comment> getCommentsUnauthorized(Long tid, String path, int offset);
}


