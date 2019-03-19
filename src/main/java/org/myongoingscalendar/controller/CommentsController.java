package org.myongoingscalendar.controller;

import org.myongoingscalendar.model.*;
import org.myongoingscalendar.model.Comment;
import org.myongoingscalendar.security.JwtUser;
import org.myongoingscalendar.service.CommentServiceCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(value = "/api/title", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class CommentsController {

    private final CommentServiceCustom commentServiceCustom;

    @Autowired
    public CommentsController(CommentServiceCustom commentServiceCustom) {
        this.commentServiceCustom = commentServiceCustom;
    }

    @RolesAllowed("ROLE_USER")
    @RequestMapping(value = "/comments/add")
    public AjaxResponse addComment(@RequestBody Comment comment, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                commentServiceCustom.addComment(comment, user.getId())
        );
    }

    @RequestMapping(value = "/{tid}/comments/{path}/{offset}")
    public AjaxResponse getUserComments(@PathVariable("tid") Long tid, @PathVariable("path") String path, @PathVariable("offset") Integer offset, @AuthenticationPrincipal JwtUser user) {
        Comments comments = user != null
                ? commentServiceCustom.getUserComments(tid, path, offset, user.getId())
                : commentServiceCustom.getComments(tid, path, offset);
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                comments
        );
    }

    @RolesAllowed("ROLE_USER")
    @RequestMapping(value = "/{tid}/comments/{comment_id}/{emotion}/add")
    public AjaxResponse addEmotion(@PathVariable("tid") Long tid, @PathVariable("comment_id") Long comment_id, @PathVariable("emotion") Emotion emotion, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                commentServiceCustom.addEmotion(tid, comment_id, emotion, user.getId())
        );
    }

    @RolesAllowed("ROLE_USER")
    @RequestMapping(value = "/{tid}/comments/{comment_id}/report")
    public AjaxResponse addReport(@PathVariable("tid") Long tid, @PathVariable("comment_id") Long comment_id, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                commentServiceCustom.addReport(tid, comment_id, user.getId())
        );
    }
}

