package org.myongoingscalendar.service;

import org.myongoingscalendar.model.Comment;
import org.myongoingscalendar.model.Comments;
import org.myongoingscalendar.model.Emotion;
import org.myongoingscalendar.model.Status;

/**
 * @author firs
 */
public interface CommentServiceCustom {
    Status addComment(Comment comment, Long userid);

    Comments getComments(Long tid, String path, int offset);

    Comments getUserComments(Long tid, String path, int offset, Long userid);

    Status addEmotion(Long tid, Long comment_id, Emotion emotion, Long userid);

    Status addReport(Long tid, Long comment_id, Long userid);

    int getCommentsTotal(Long tid);

    int getCommentsCountFromPath(Long tid, String path);
}
