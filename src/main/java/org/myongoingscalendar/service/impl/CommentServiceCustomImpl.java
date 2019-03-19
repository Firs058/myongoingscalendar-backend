package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.CommentEntity;
import org.myongoingscalendar.entity.DislikeEntity;
import org.myongoingscalendar.entity.LikeEntity;
import org.myongoingscalendar.entity.ReportEntity;
import org.myongoingscalendar.model.Comment;
import org.myongoingscalendar.model.Comments;
import org.myongoingscalendar.model.Emotion;
import org.myongoingscalendar.model.Status;
import org.myongoingscalendar.repository.CommentRepository;
import org.myongoingscalendar.repository.CommentRepositoryCustom;
import org.myongoingscalendar.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author firs
 */
@Service
public class CommentServiceCustomImpl implements CommentServiceCustom {

    private final CommentRepository commentRepository;
    private final CommentRepositoryCustom commentRepositoryCustom;
    private final UserService userService;
    private final OngoingService ongoingService;
    private final LikeService likeService;
    private final DislikeService dislikeService;
    private final ReportService reportService;

    public CommentServiceCustomImpl(CommentRepository commentRepository, CommentRepositoryCustom commentRepositoryCustom, UserService userService, OngoingService ongoingService, LikeService likeService, DislikeService dislikeService, ReportService reportService) {
        this.commentRepository = commentRepository;
        this.commentRepositoryCustom = commentRepositoryCustom;
        this.userService = userService;
        this.ongoingService = ongoingService;
        this.likeService = likeService;
        this.dislikeService = dislikeService;
        this.reportService = reportService;
    }

    @Transactional
    @Override
    public Status addComment(Comment comment, Long userid) {
        return userService.get(userid)
                .map(u -> {
                    if (u.muted())
                        return new Status(10025, "Your account has muted, you can't add a comment");
                    if (comment.text() != null && !comment.text().equals("")) {
                        final String commentPath = (comment.id() != null)
                                ? String.join(".", commentRepository.gePathtById(comment.id()), String.valueOf(comment.id()))
                                : "root";
                        return ongoingService.get(comment.tid())
                                .map(o -> {
                                    o.commentEntities().add(
                                            new CommentEntity()
                                                    .ongoingEntity(o)
                                                    .userEntity(u)
                                                    .text(comment.text())
                                                    .path(commentPath));
                                    ongoingService.save(o);
                                    return new Status(11011, "Comment added");
                                })
                                .orElseGet(() -> new Status(10026, "Can't add comment"));
                    } else return new Status(10027, "Why null comment?");
                })
                .orElseGet(() -> new Status(10012, "You must be logged"));
    }

    @Override
    public Comments getComments(Long tid, String path, int offset) {
        return new Comments()
                .total(getCommentsTotal(tid))
                .fromPath(getCommentsCountFromPath(tid, path))
                .nodes(commentRepositoryCustom.getCommentsUnauthorized(tid, path, offset));
    }

    @Override
    public Comments getUserComments(Long tid, String path, int offset, Long userid) {
        return new Comments()
                .total(getCommentsTotal(tid))
                .fromPath(getCommentsCountFromPath(tid, path))
                .nodes(commentRepositoryCustom.getCommentsAuthorized(tid, path, offset, userid));
    }

    @Override
    @Transactional
    public Status addEmotion(Long tid, Long comment_id, Emotion emotion, Long userid) {
        return commentRepository.getByIdAndOngoingEntity_TidAndUserEntity_Id(comment_id, tid, userid)
                .map(commentEntity -> {
                    LikeEntity like = new LikeEntity().commentEntity(commentEntity).userEntity(commentEntity.userEntity());
                    DislikeEntity dislike = new DislikeEntity().commentEntity(commentEntity).userEntity(commentEntity.userEntity());
                    switch (emotion) {
                        case like:
                            return likeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(tid, comment_id, userid)
                                    .map(likeEntity -> new Status(10028, "Already liked"))
                                    .orElseGet(() -> {
                                        likeService.save(like);
                                        dislikeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(tid, comment_id, userid)
                                                .ifPresent(dislikeService::delete);
                                        return new Status(11012, "Like added");
                                    });
                        case dislike:
                            return dislikeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(tid, comment_id, userid)
                                    .map(dislikeEntity -> new Status(10029, "Already disliked"))
                                    .orElseGet(() -> {
                                        dislikeService.save(dislike);
                                        likeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(tid, comment_id, userid)
                                                .ifPresent(likeService::delete);
                                        return new Status(11013, "Dislike added");
                                    });
                        default:
                            return new Status(10016, "Server error. What you expect?");
                    }
                })
                .orElse(new Status(10018, "Not found"));
    }

    @Override
    @Transactional
    public Status addReport(Long tid, Long comment_id, Long userid) {
        return commentRepository.getByIdAndOngoingEntity_TidAndUserEntity_Id(comment_id, tid, userid)
                .map(commentEntity -> {
                    ReportEntity report = new ReportEntity().commentEntity(commentEntity).userEntity(commentEntity.userEntity());
                    return reportService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(tid, comment_id, userid)
                            .map(reportEntity -> new Status(10030, "Already reported"))
                            .orElseGet(() -> {
                                reportService.save(report);
                                return new Status(11014, "Thanks for report");
                            });
                })
                .orElse(new Status(10018, "Not found"));
    }

    @Override
    public int getCommentsTotal(Long tid) {
        return commentRepository.getCommentsTotal(tid);
    }

    @Override
    public int getCommentsCountFromPath(Long tid, String path) {
        return commentRepository.getCommentsCountFromPath(tid, path);
    }
}
