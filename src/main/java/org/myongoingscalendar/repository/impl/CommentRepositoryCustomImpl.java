package org.myongoingscalendar.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.repository.CommentRepositoryCustom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.*;

/**
 * @author firs
 */
@Service
@Slf4j
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> getCommentsUnauthorized(Long tid, String path, int offset) {
        String sql = "WITH child AS (SELECT\n" +
                "                 ltree2text(subpath(path, -1, 1)) :: BIGINT AS id,\n" +
                "                 COUNT(id)                                  AS replies\n" +
                "               FROM comments\n" +
                "               WHERE path <> 'root'\n" +
                "               GROUP BY 1),\n" +
                "    emotions AS (SELECT\n" +
                "                   c.id                AS id,\n" +
                "                   count(l.comment_id) AS likes,\n" +
                "                   count(d.comment_id) AS dislikes\n" +
                "                 FROM comments c\n" +
                "                   LEFT JOIN likes l ON c.id = l.comment_id\n" +
                "                   LEFT JOIN dislikes d ON c.id = d.comment_id\n" +
                "                 GROUP BY c.id)\n" +
                "SELECT\n" +
                "  DISTINCT ON (c.id)\n" +
                "  c.id                        AS id,\n" +
                "  c.tid                       AS tid,\n" +
                "  c.path                      AS path,\n" +
                "  EXTRACT(EPOCH FROM c.added) AS added,\n" +
                "  c.text                      AS text,\n" +
                "  s.avatar                    AS avatar,\n" +
                "  s.nickname                  AS nickname,\n" +
                "  n.replies                   AS replies,\n" +
                "  e.likes                     AS likes,\n" +
                "  e.dislikes                  AS dislikes\n" +
                "FROM comments c\n" +
                "  JOIN users u ON c.user_id = u.id\n" +
                "  LEFT JOIN users_settings s ON s.user_id = u.id\n" +
                "  LEFT JOIN child n ON c.id = n.id\n" +
                "  LEFT JOIN emotions e ON c.id = e.id\n" +
                "WHERE c.tid = ?\n" +
                "      AND c.path = ?\n" +
                "\n" +
                "GROUP BY c.id, s.avatar, s.nickname, n.replies, e.likes, e.dislikes\n" +
                "ORDER BY c.id DESC\n" +
                "LIMIT 10\n" +
                "OFFSET ?";
        return new ArrayList<>(jdbcTemplate.query(sql, new Object[]{tid, path, offset}, new int[]{Types.BIGINT, Types.OTHER, Types.INTEGER}, (rs, row) ->
                new Comment()
                        .id(rs.getLong("id"))
                        .tid(rs.getLong("tid"))
                        .path(rs.getString("path"))
                        .added(rs.getLong("added"))
                        .text(rs.getString("text"))
                        .replies(rs.getInt("replies"))
                        .likes(rs.getInt("likes"))
                        .dislikes(rs.getInt("dislikes"))
                        .user(
                                new UserMin()
                                        .avatar(mapAvatar(rs.getString("avatar")))
                                        .nickname(rs.getString("nickname")))
        ));
    }

    @Override
    public List<Comment> getCommentsAuthorized(Long tid, String path, int offset, Long userid) {
        String sql = "WITH child AS (SELECT\n" +
                "                 ltree2text(subpath(path, -1, 1)) :: BIGINT AS id,\n" +
                "                 COUNT(id)                                  AS replies\n" +
                "               FROM comments\n" +
                "               WHERE path <> 'root'\n" +
                "               GROUP BY 1),\n" +
                "    emotions AS (SELECT\n" +
                "                   c.id                AS id,\n" +
                "                   count(l.comment_id) AS likes,\n" +
                "                   count(d.comment_id) AS dislikes\n" +
                "                 FROM comments c\n" +
                "                   LEFT JOIN likes l ON c.id = l.comment_id\n" +
                "                   LEFT JOIN dislikes d ON c.id = d.comment_id\n" +
                "                 GROUP BY c.id),\n" +
                "    actions AS (SELECT\n" +
                "                  c.id          AS id,\n" +
                "                  CASE WHEN l.comment_id ISNULL\n" +
                "                            OR l.user_id != ?\n" +
                "                    THEN FALSE\n" +
                "                  ELSE TRUE END AS liked,\n" +
                "                  CASE WHEN d.comment_id ISNULL\n" +
                "                            OR d.user_id != ?\n" +
                "                    THEN FALSE\n" +
                "                  ELSE TRUE END AS disliked\n" +
                "                FROM comments c\n" +
                "                  JOIN users u ON c.user_id = u.id\n" +
                "                  LEFT JOIN likes l ON c.id = l.comment_id\n" +
                "                  LEFT JOIN dislikes d ON c.id = d.comment_id\n" +
                "                GROUP BY c.id, l.comment_id, l.user_id, d.comment_id, d.user_id, u.id)\n" +
                "SELECT\n" +
                "  DISTINCT ON (c.id)\n" +
                "  c.id                        AS id,\n" +
                "  c.tid                       AS tid,\n" +
                "  c.path                      AS path,\n" +
                "  EXTRACT(EPOCH FROM c.added) AS added,\n" +
                "  c.text                      AS text,\n" +
                "  s.avatar                    AS avatar,\n" +
                "  s.nickname                  AS nickname,\n" +
                "  n.replies                   AS replies,\n" +
                "  l.liked                     AS liked,\n" +
                "  d.disliked                  AS disliked,\n" +
                "  e.likes                     AS likes,\n" +
                "  e.dislikes                  AS dislikes\n" +
                "FROM comments c\n" +
                "  JOIN users u ON c.user_id = u.id\n" +
                "  LEFT JOIN users_settings s ON s.user_id = u.id\n" +
                "  LEFT JOIN child n ON c.id = n.id\n" +
                "  LEFT JOIN actions l ON c.id = l.id and l.liked is true\n" +
                "  LEFT JOIN actions d ON c.id = d.id and d.disliked is true\n" +
                "  LEFT JOIN emotions e ON c.id = e.id\n" +
                "WHERE c.tid = ?\n" +
                "      AND c.path = ?\n" +
                "\n" +
                "GROUP BY c.id, s.avatar, s.nickname, n.replies, e.likes, e.dislikes, l.liked, d.disliked\n" +
                "ORDER BY c.id DESC\n" +
                "LIMIT 10\n" +
                "OFFSET ?";
        return new ArrayList<>(jdbcTemplate.query(sql, new Object[]{userid, userid, tid, path, offset}, new int[]{Types.INTEGER, Types.INTEGER, Types.BIGINT, Types.OTHER, Types.INTEGER}, (rs, row) ->
                new Comment()
                        .id(rs.getLong("id"))
                        .tid(rs.getLong("tid"))
                        .path(rs.getString("path"))
                        .added(rs.getLong("added"))
                        .text(rs.getString("text"))
                        .replies(rs.getInt("replies"))
                        .liked(rs.getBoolean("liked"))
                        .disliked(rs.getBoolean("disliked"))
                        .likes(rs.getInt("likes"))
                        .dislikes(rs.getInt("dislikes"))
                        .user(
                                new UserMin()
                                        .avatar(mapAvatar(rs.getString("avatar")))
                                        .nickname(rs.getString("nickname")))
        ));
    }

    private Image mapAvatar(String avatar) {
        if (avatar != null) {
            try {
                return new ObjectMapper().treeToValue(new ObjectMapper().readTree(avatar), Image.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        return null;
    }
}
