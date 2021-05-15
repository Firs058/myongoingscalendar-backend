package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.elastic.service.ElasticAnimeService;
import org.myongoingscalendar.entity.*;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.model.Statistics.*;
import org.myongoingscalendar.service.*;
import org.myongoingscalendar.utils.AnimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author firs
 */
@Service
public class UserServiceCustomImpl implements UserServiceCustom {

    private final ElasticAnimeService elasticAnimeService;
    private final UserService userService;
    private final OngoingService ongoingService;
    private final UserTitleDropService userTitleDropService;
    private final UserTitleService userTitleService;
    private final UserTitleScoreService userTitleScoreService;
    private final UserTitleFavoriteService userTitleFavoriteService;
    private final CommentService commentService;

    public UserServiceCustomImpl(ElasticAnimeService elasticAnimeService, UserService userService, OngoingService ongoingService, UserTitleDropService userTitleDropService, UserTitleService userTitleService, UserTitleScoreService userTitleScoreService, UserTitleFavoriteService userTitleFavoriteService, CommentService commentService) {
        this.elasticAnimeService = elasticAnimeService;
        this.userService = userService;
        this.ongoingService = ongoingService;
        this.userTitleDropService = userTitleDropService;
        this.userTitleService = userTitleService;
        this.userTitleScoreService = userTitleScoreService;
        this.userTitleFavoriteService = userTitleFavoriteService;
        this.commentService = commentService;
    }

    @Override
    @Transactional
    public Status toggleUserTid(Long tid, Long user_id) {
        return userService.get(user_id)
                .map(userEntity ->
                        ongoingService.findByTid(tid)
                                .map(ongoingEntity -> {
                                    if (ongoingEntity.syoboiInfoEntity().finished() && ongoingEntity.syoboiOngoingEntity() == null)
                                        return ResponseStatus.S10033.getStatus();

                                    return userTitleService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id)
                                            .map(f -> {
                                                userTitleService.delete(f);
                                                userTitleDropService.save(new UserTitleDropEntity().userEntity(userEntity).ongoingEntity(ongoingEntity).dateAdded(f.dateAdded()));
                                                userTitleScoreService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id).ifPresent(userTitleScoreService::delete);
                                                userTitleFavoriteService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id).ifPresent(userTitleFavoriteService::delete);
                                                return ResponseStatus.S11007.getStatus();
                                            })
                                            .orElseGet(() -> {
                                                userTitleDropService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id).ifPresent(d -> userEntity.userTitleDropEntities().remove(d));
                                                userTitleService.save(new UserTitleEntity().ongoingEntity(ongoingEntity).userEntity(userEntity));
                                                return ResponseStatus.S11008.getStatus();
                                            });
                                })
                                .orElse(ResponseStatus.S10018.getStatus()))
                .orElse(ResponseStatus.S10012.getStatus());
    }

    @Override
    @Transactional
    public Status setUserTitleScore(Long tid, Long user_id, BigDecimal score) {
        if (!AnimeUtil.isScoreInCorrectRange(score))
            return ResponseStatus.S10038.getStatus();
        if (!userTitleService.existsByOngoingEntity_TidAndUserEntity_Id(tid, user_id))
            return ResponseStatus.S10039.getStatus();

        return userService.get(user_id)
                .map(userEntity ->
                        ongoingService.findByTid(tid)
                                .map(ongoingEntity ->
                                        userTitleScoreService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id)
                                                .map(r -> {
                                                    r.score(score);
                                                    return ResponseStatus.S11024.getStatus();
                                                })
                                                .orElseGet(() -> {
                                                    userTitleScoreService.save(
                                                            new UserTitleScoreEntity()
                                                                    .ongoingEntity(ongoingEntity)
                                                                    .userEntity(userEntity)
                                                                    .score(score));
                                                    return ResponseStatus.S11023.getStatus();
                                                }))
                                .orElse(ResponseStatus.S10018.getStatus()))
                .orElse(ResponseStatus.S10012.getStatus());
    }

    @Override
    public Status removeUserTitleScore(Long tid, Long user_id) {
        return userService.get(user_id)
                .map(userEntity ->
                        userTitleScoreService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id)
                                .map(ts -> {
                                    userTitleScoreService.delete(ts);
                                    return ResponseStatus.S11025.getStatus();
                                })
                                .orElseGet(ResponseStatus.S10018::getStatus))
                .orElse(ResponseStatus.S10012.getStatus());
    }

    @Override
    @Transactional
    public Status toggleUserTitleFavorite(Long tid, Long user_id) {
        if (!userTitleService.existsByOngoingEntity_TidAndUserEntity_Id(tid, user_id))
            return ResponseStatus.S10039.getStatus();

        return userService.get(user_id)
                .map(userEntity ->
                        ongoingService.findByTid(tid)
                                .map(ongoingEntity ->
                                        userTitleFavoriteService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id)
                                                .map(tf -> {
                                                    userTitleFavoriteService.delete(tf);
                                                    return ResponseStatus.S11022.getStatus();
                                                })
                                                .orElseGet(() -> {
                                                    userTitleFavoriteService.save(
                                                            new UserTitleFavoriteEntity()
                                                                    .ongoingEntity(ongoingEntity)
                                                                    .userEntity(userEntity));
                                                    return ResponseStatus.S11021.getStatus();
                                                }))
                                .orElse(ResponseStatus.S10018.getStatus()))
                .orElse(ResponseStatus.S10012.getStatus());
    }

    @Override
    @Transactional
    public Statistics getUserStatistics(Long user_id) {
        List<UserTitleScoreEntity> allScores = userTitleScoreService.findByUserEntity_Id(user_id);

        BigDecimal avgScore = AnimeUtil.getAvgScore(
                allScores.stream()
                        .map(UserTitleScoreEntity::score)
                        .map(Objects::requireNonNull)
                        .toList(),
                RoundingMode.HALF_UP);

        List<Long> addedTids = userTitleService.getAllOngoingsTidsAddedByUser(user_id);
        List<Long> droppedTids = userTitleDropService.getAllOngoingsTidsDroppedByUser(user_id);
        List<Long> favoriteTids = userTitleFavoriteService.getAllOngoingsTidsFavoriteByUser(user_id);

        List<Long> allTids = Stream.concat(addedTids.stream(), droppedTids.stream()).distinct().toList();

        List<ElasticAnime> elasticAnimes = elasticAnimeService.findByTids(allTids);

        List<ElasticAnime> filledElasticAnimes = AnimeUtil.fillWatchingStatusAndFavorite(elasticAnimes, addedTids, droppedTids, favoriteTids);

        elasticAnimes.forEach(e -> e.score(allScores.stream().filter(entity -> entity.ongoingEntity().tid().equals(e.tid())).findFirst().map(UserTitleScoreEntity::score).orElse(null)));

        List<StatisticsGenre> genres = elasticAnimes.stream()
                .filter(f -> f.genres() != null)
                .flatMap(e -> e.genres().stream())
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new StatisticsGenre(e.getValue(), e.getKey().id(), e.getKey().name()))
                .sorted(Comparator.comparing(StatisticsGenre::count, Comparator.reverseOrder()))
                .toList();

        List<Map.Entry<String, Long>> elasticStatuses = filledElasticAnimes.stream()
                .collect(Collectors.groupingBy(e -> e.watchingStatus().name(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .toList();

        List<StatisticsWatching> statisticsWatching = new ArrayList<>();

        long allCount = elasticStatuses.stream().mapToLong(Map.Entry::getValue).sum();
        statisticsWatching.add(new StatisticsWatching(allCount, "ALL"));
        elasticStatuses.forEach(e -> statisticsWatching.add(new StatisticsWatching(e.getValue(), e.getKey())));
        if (allCount != 0L) statisticsWatching.add(new StatisticsWatching((long) favoriteTids.size(), "FAVORITES"));

        Map<WatchingStatus, List<ElasticAnime>> anime = filledElasticAnimes.stream()
                .collect(Collectors.groupingBy(ElasticAnime::watchingStatus, Collectors.toList()));

        List<CommentEntity> allComments = commentService.findByUserEntity_Id(user_id);
        int commentsCount = allComments.size();

        List<StatisticsComments> comments = allComments.stream()
                .collect(Collectors.groupingBy(e -> e.ongoingEntity().tid(), LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(e -> new StatisticsComments(
                        elasticAnimes.stream().filter(f -> f.tid().equals(e.getKey())).findAny().orElse(null),
                        e.getValue().size(),
                        e.getValue().stream()
                                .sorted(Comparator.comparing(CommentEntity::added, Comparator.nullsLast(Comparator.reverseOrder())))
                                .map(commentEntity -> {
                                    int likes = commentEntity.likeEntities().size();
                                    int dislikes = commentEntity.dislikeEntities().size();
                                    return new BaseComment()
                                            .added(commentEntity.added().getTime() / 1000)
                                            .text(commentEntity.text())
                                            .karma(likes - dislikes)
                                            .likes(likes)
                                            .dislikes(dislikes);
                                })
                                .toList()
                ))
                .sorted(Comparator.comparing(s -> s.comments().get(0).added(), Comparator.reverseOrder()))
                .toList();

        return new Statistics(
                new ScoresSection(avgScore),
                new GenresSection(genres),
                new AnimeSection(statisticsWatching, anime),
                new CommentsSection(commentsCount, comments)
        );
    }
}
