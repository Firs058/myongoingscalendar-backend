package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.*;
import org.myongoingscalendar.model.*;
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
                        return ResponseStatus.S10025.getStatus();
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
                                    return ResponseStatus.S11011.getStatus();
                                })
                                .orElse(ResponseStatus.S10026.getStatus());
                    } else return ResponseStatus.S10027.getStatus();
                })
                .orElse(ResponseStatus.S10012.getStatus());
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
        return userService.get(userid)
                .map(u -> commentRepository.getByIdAndOngoingEntity_Tid(comment_id, tid)
                        .map(commentEntity -> {
                            LikeEntity like = new LikeEntity().commentEntity(commentEntity).userEntity(u);
                            DislikeEntity dislike = new DislikeEntity().commentEntity(commentEntity).userEntity(u);
                            return switch (emotion) {
                                case like -> likeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(tid, comment_id)
                                        .map(likeEntity -> ResponseStatus.S10028.getStatus())
                                        .orElseGet(() -> {
                                            likeService.save(like);
                                            dislikeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(tid, comment_id)
                                                    .ifPresent(dislikeService::delete);
                                            return ResponseStatus.S11012.getStatus();
                                        });
                                case dislike -> dislikeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(tid, comment_id)
                                        .map(dislikeEntity -> ResponseStatus.S10029.getStatus())
                                        .orElseGet(() -> {
                                            dislikeService.save(dislike);
                                            likeService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(tid, comment_id)
                                                    .ifPresent(likeService::delete);
                                            return ResponseStatus.S11013.getStatus();
                                        });
                                default -> ResponseStatus.S10016.getStatus();
                            };
                        })
                        .orElse(ResponseStatus.S10018.getStatus()))
                .orElse(ResponseStatus.S10012.getStatus());
    }

    @Override
    @Transactional
    public Status addReport(Long tid, Long comment_id, Long userid) {
        return userService.get(userid)
                .map(u -> commentRepository.getByIdAndOngoingEntity_Tid(comment_id, tid)
                        .map(commentEntity -> {
                            ReportEntity report = new ReportEntity().commentEntity(commentEntity).userEntity(u);
                            return reportService.findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(tid, comment_id)
                                    .map(reportEntity -> ResponseStatus.S10030.getStatus())
                                    .orElseGet(() -> {
                                        reportService.save(report);
                                        return ResponseStatus.S11014.getStatus();
                                    });
                        })
                        .orElse(ResponseStatus.S10018.getStatus()))
                .orElse(ResponseStatus.S10012.getStatus());
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
