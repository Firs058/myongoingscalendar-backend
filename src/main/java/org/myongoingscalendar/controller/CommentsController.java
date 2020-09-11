package org.myongoingscalendar.controller;

import org.myongoingscalendar.model.*;
import org.myongoingscalendar.model.Comment;
import org.myongoingscalendar.security.JwtUser;
import org.myongoingscalendar.service.CommentServiceCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user/title", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class CommentsController {

    private final CommentServiceCustom commentServiceCustom;

    @Autowired
    public CommentsController(CommentServiceCustom commentServiceCustom) {
        this.commentServiceCustom = commentServiceCustom;
    }

    @RequestMapping(value = "/comments/add")
    public AjaxResponse addComment(@RequestBody Comment comment, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                commentServiceCustom.addComment(comment, user.getId())
        );
    }

    @RequestMapping(value = "/{tid}/comments/{path}/{offset}")
    public AjaxResponse getUserComments(@PathVariable("tid") Long tid, @PathVariable("path") String path, @PathVariable("offset") Integer offset, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(commentServiceCustom.getUserComments(tid, path, offset, user.getId()));
    }

    @RequestMapping(value = "/{tid}/comments/{comment_id}/{emotion}/add")
    public AjaxResponse addEmotion(@PathVariable("tid") Long tid, @PathVariable("comment_id") Long comment_id, @PathVariable("emotion") Emotion emotion, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                commentServiceCustom.addEmotion(tid, comment_id, emotion, user.getId())
        );
    }

    @RequestMapping(value = "/{tid}/comments/{comment_id}/report/add")
    public AjaxResponse addReport(@PathVariable("tid") Long tid, @PathVariable("comment_id") Long comment_id, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                commentServiceCustom.addReport(tid, comment_id, user.getId())
        );
    }
}

