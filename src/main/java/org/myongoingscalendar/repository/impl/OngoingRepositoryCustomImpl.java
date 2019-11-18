package org.myongoingscalendar.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.transform.Transformers;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.entity.MalTitleGenreEntity;
import org.myongoingscalendar.entity.RatingEntity;
import org.myongoingscalendar.entity.SyoboiTimetableEntity;
import org.myongoingscalendar.repository.OngoingRepositoryCustom;
import org.myongoingscalendar.service.CommentServiceCustom;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.service.UserService;
import org.myongoingscalendar.utils.AnimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author firs
 */
@Service
@Slf4j
public class OngoingRepositoryCustomImpl implements OngoingRepositoryCustom {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    private final OngoingService ongoingService;
    private final UserService userService;
    private final CommentServiceCustom commentServiceCustom;
    @Value("${links.anime.anidb}")
    private String anidbAnimeUrlPath;
    @Value("${links.anime.mal}")
    private String malAnimeUrlPath;
    @Value("${links.anime.ann}")
    private String annAnimeUrlPath;

    public OngoingRepositoryCustomImpl(OngoingService ongoingService, UserService userService, CommentServiceCustom commentServiceCustom) {
        this.ongoingService = ongoingService;
        this.userService = userService;
        this.commentServiceCustom = commentServiceCustom;
    }

    @Override
    @Cacheable("getOngoingsFull")
    public List<ReturnTitleGrids> getOngoingsFull(String userTimezone, Locale locale) {
        return AnimeUtil.sort(getTitlesForTimezone(userTimezone, locale));
    }

    @Override
    @Cacheable("getOngoingsMin")
    public List<ReturnTitleGrids> getOngoingsMin(String userTimezone, Locale locale) {
        return AnimeUtil.sort(getTitlesForTimezoneMin(userTimezone, locale));
    }

    @Override
    public List<ReturnTitleGrids> getUserOngoingsFull(String userTimezone, Long userid, Locale locale) {
        return AnimeUtil.sort(getTitlesForUser(userTimezone, userid, locale));
    }

    @Override
    public List<ReturnTitleGrids> getUserOngoingsMin(String userTimezone, Long userid, Locale locale) {
        return AnimeUtil.sort(getTitlesForUserMin(userTimezone, userid, locale));
    }

    @Override
    @Transactional
    public UserTitle getOngoingData(Long tid, String timezone, Locale locale) {
        return new UserTitle()
                .marked(false)
                .broadcast(createTitleBroadcast(timezone, tid, locale))
                .title(getTitleData(tid))
                .comments(commentServiceCustom.getComments(tid, "root", 0));
    }

    @Override
    @Transactional
    public UserTitle getUserOngoingData(Long tid, String timezone, Long userid, Locale locale) {
        return userService.get(userid)
                .map(u -> new UserTitle()
                        .marked(u.usersTitleEntities().stream().anyMatch(t -> t.ongoingEntity().tid().equals(tid)))
                        .broadcast(createTitleBroadcast(u.userSettingsEntity().timezone(), tid, locale))
                        .title(getTitleData(tid))
                        .comments(commentServiceCustom.getUserComments(tid, "root", 0, userid)))
                .orElse(new UserTitle());
    }

    private Title getTitleData(Long tid) {
        return ongoingService.findByTid(tid)
                .map(ongoing -> {
                    Title title = new Title()
                            .tid(tid)
                            .outdated(ongoing.syoboiInfoEntity().outdated() && ongoing.syoboiOngoingEntity() == null);

                    if (ongoing.syoboiInfoEntity() != null)
                        title
                                .ja(ongoing.syoboiInfoEntity().title())
                                .firstYear(ongoing.syoboiInfoEntity().firstYear())
                                .firstMonth(ongoing.syoboiInfoEntity().firstMonth());

                    if (ongoing.anidbEntity() != null)
                        title
                                .en(ongoing.anidbEntity().titleEN())
                                .image(new Image(
                                        AnimeUtil.createImagePath(ongoing.anidbEntity().image(), ImageType.full, ongoing.aid()),
                                        AnimeUtil.createImagePath(ongoing.anidbEntity().image(), ImageType.thumbnail, ongoing.aid()),
                                        AnimeUtil.createHEX(ongoing.anidbEntity().vibrant())
                                ))
                                .episodes(ongoing.anidbEntity().episodeCount())
                                .links().addAll(AnimeUtil.createLinks(
                                new Object[]{"AniDB", anidbAnimeUrlPath, ongoing.aid(), "/images/anidb.png"},
                                new Object[]{"Official site", null, ongoing.anidbEntity().url(), "/images/official.png"}
                        ));
                    else title.image(new Image("/images/noimage.svg", "/images/noimage.svg", null));

                    if (ongoing.malEntity() != null)
                        title
                                .description(ongoing.malEntity().description())
                                .trailer(ongoing.malEntity().trailerUrl())
                                .genres(ongoing.malTitleGenreEntities().stream().map(MalTitleGenreEntity::genreEntity).collect(Collectors.toList()))
                                .links().addAll(AnimeUtil.createLinks(new Object[]{"MAL", malAnimeUrlPath, ongoing.malid(), "/images/mal.png"}));

                    if (ongoing.annid() != null)
                        title
                                .links().addAll(AnimeUtil.createLinks(new Object[]{"ANN", annAnimeUrlPath, ongoing.annid(), "/images/ann.png"}));

                    if (ongoing.ratingEntities() != null) {
                        List<Datasets> datasets = new ArrayList<>();

                        List<Double> anidbData = ongoing.ratingEntities().stream().sorted(Comparator.comparing(RatingEntity::added)).map(RatingEntity::anidbTemporary).collect(Collectors.toList());
                        if (anidbData.stream().filter(Objects::nonNull).count() > 2)
                            datasets.add(new Datasets()
                                    .label("AniDB")
                                    .borderColor("#791B26")
                                    .backgroundColor("rgba(121, 27, 38, 0.5)")
                                    .data(anidbData.toArray()));

                        List<Double> malData = ongoing.ratingEntities().stream().sorted(Comparator.comparing(RatingEntity::added)).map(RatingEntity::mal).collect(Collectors.toList());
                        if (malData.stream().filter(Objects::nonNull).count() > 2)
                            datasets.add(new Datasets()
                                    .label("MAL")
                                    .borderColor("#2e51a2")
                                    .backgroundColor("rgba(46, 81, 163, 0.5)")
                                    .data(malData.toArray()));

                        List<Double> annData = ongoing.ratingEntities().stream().sorted(Comparator.comparing(RatingEntity::added)).map(RatingEntity::ann).collect(Collectors.toList());
                        if (annData.stream().filter(Objects::nonNull).count() > 2)
                            datasets.add(new Datasets()
                                    .label("ANN")
                                    .borderColor("#016192")
                                    .backgroundColor("rgba(1, 98, 147, 0.5)")
                                    .data(annData.toArray()));

                        title
                                .ratings(AnimeUtil.createRatings(
                                        new Object[]{"AniDB", ongoing.ratingEntities().stream().sorted(Comparator.comparing(RatingEntity::added)).filter(e -> Objects.nonNull(e.anidbTemporary())).reduce((first, second) -> second).map(RatingEntity::anidbTemporary).orElse((double) 0)},
                                        new Object[]{"MAL", ongoing.ratingEntities().stream().sorted(Comparator.comparing(RatingEntity::added)).filter(e -> Objects.nonNull(e.mal())).reduce((first, second) -> second).map(RatingEntity::mal).orElse((double) 0)},
                                        new Object[]{"ANN", ongoing.ratingEntities().stream().sorted(Comparator.comparing(RatingEntity::added)).filter(e -> Objects.nonNull(e.ann())).reduce((first, second) -> second).map(RatingEntity::ann).orElse((double) 0)}
                                ))
                                .chartData(
                                        new ChartData()
                                                .labels(ongoing.ratingEntities().stream().sorted(Comparator.comparing(RatingEntity::added)).map(e -> new SimpleDateFormat("dd/MM/yyyy").format(e.added())).toArray())
                                                .datasets(datasets)
                                );

                        title.avgRating(title.ratings().stream().mapToDouble(Ratings::score).average().getAsDouble());
                    }

                    return title;
                })
                .orElse(new Title());
    }

    @Override
    @Transactional
    public List<ElasticAnime> getElasticData() {
        return ongoingService.getAll().stream()
                .filter(o -> Objects.nonNull(o.syoboiInfoEntity()))
                .map(ongoing -> {
                    ElasticAnime elasticAnime = new ElasticAnime()
                            .tid(ongoing.tid())
                            .ja(ongoing.syoboiInfoEntity().title())
                            .dateStart(AnimeUtil.createDateStart(ongoing.syoboiInfoEntity().firstYear(), ongoing.syoboiInfoEntity().firstMonth()))
                            .outdated(ongoing.syoboiInfoEntity().outdated() && ongoing.syoboiOngoingEntity() == null);

                    if (ongoing.anidbEntity() != null)
                        elasticAnime
                                .en(ongoing.anidbEntity().titleEN())
                                .image(new Image(
                                        AnimeUtil.createImagePath(ongoing.anidbEntity().image(), ImageType.full, ongoing.aid()),
                                        AnimeUtil.createImagePath(ongoing.anidbEntity().image(), ImageType.thumbnail, ongoing.aid()),
                                        AnimeUtil.createHEX(ongoing.anidbEntity().vibrant())
                                ))
                                .episodes(ongoing.anidbEntity().episodeCount());
                    else elasticAnime.image(new Image("/images/noimage.svg", "/images/noimage.svg", null));

                    if (ongoing.malEntity() != null)
                        elasticAnime
                                .description(ongoing.malEntity().description())
                                .genres(ongoing.malTitleGenreEntities().stream().map(MalTitleGenreEntity::genreEntity).collect(Collectors.toList()));

                    if (ongoing.ratingEntities() != null) {
                        Double anidbTemporaryRating = ongoing.ratingEntities().stream()
                                .sorted(Comparator.comparing(RatingEntity::added))
                                .filter(e -> Objects.nonNull(e.anidbTemporary()))
                                .reduce((first, second) -> second)
                                .map(RatingEntity::anidbTemporary)
                                .orElse((double) 0);

                        Double malRating = ongoing.ratingEntities().stream()
                                .sorted(Comparator.comparing(RatingEntity::added))
                                .filter(e -> Objects.nonNull(e.mal()))
                                .reduce((first, second) -> second)
                                .map(RatingEntity::mal)
                                .orElse((double) 0);

                        Double annRating = ongoing.ratingEntities().stream()
                                .sorted(Comparator.comparing(RatingEntity::added))
                                .filter(e -> Objects.nonNull(e.ann()))
                                .reduce((first, second) -> second)
                                .map(RatingEntity::ann)
                                .orElse((double) 0);

                        elasticAnime
                                .recommended(AnimeUtil.createRecommended(anidbTemporaryRating, malRating))
                                .ratings(AnimeUtil.createRatings(
                                        new Object[]{"AniDB", anidbTemporaryRating},
                                        new Object[]{"MAL", malRating},
                                        new Object[]{"ANN", annRating}
                                ));
                    }
                    if (ongoing.syoboiTimetableEntities() != null)
                        elasticAnime
                                .channels(ongoing.syoboiTimetableEntities().stream().map(SyoboiTimetableEntity::channelEntity).distinct().collect(Collectors.toList()));

                    return elasticAnime;
                })
                .collect(Collectors.toList());
    }

    private Broadcast createTitleBroadcast(String timezone, Long tid, Locale locale) {
        List<Tabs> tabs = new ArrayList<>();
        tabs.add(new Tabs().name("next").items(new ArrayList<>()));
        tabs.add(new Tabs().name("prev").items(new ArrayList<>()));

        List<TitleBroadcast> titleBroadcasts = getTitleBroadcastForTimezone(timezone, tid, locale);

        if (titleBroadcasts != null) {
            tabs.get(0).items(
                    titleBroadcasts
                            .stream()
                            .filter(el -> !el.elapsed())
                            .collect(Collectors.toList())
            );
            tabs.get(1).items(
                    titleBroadcasts
                            .stream()
                            .filter(TitleBroadcast::elapsed)
                            .collect(Collectors.toList())
            );
        }
        return new Broadcast(tabs);
    }

    @SuppressWarnings("unchecked")
    private List<TitleBroadcast> getTitleBroadcastForTimezone(String timezone, Long tid, Locale locale) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String query = "SELECT\n" +
                    "  s.date_start         AS datestart,\n" +
                    "  COALESCE(h.en, h.ja) AS channel,\n" +
                    "  s.shift              AS shift,\n" +
                    "  s.episode            AS episode,\n" +
                    "  s.episode_name       AS episodename,\n" +
                    "  CASE\n" +
                    "  WHEN s.date_start <= date_trunc('min', now())\n" +
                    "    THEN TRUE\n" +
                    "  ELSE FALSE\n" +
                    "  END                  AS elapsed\n" +
                    "FROM syoboi_timetable s\n" +
                    "  LEFT JOIN syoboi_ongoings o ON o.tid = s.tid\n" +
                    "  LEFT JOIN channels h ON h.id = s.ch\n" +
                    "  LEFT JOIN ongoings z ON z.tid = s.tid\n" +
                    "WHERE s.tid = :tid\n" +
                    "ORDER BY s.date_start ASC";
            List<TitleBroadcast> result = entityManager
                    .createNativeQuery(query)
                    .setParameter("tid", tid)
                    .unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(TitleBroadcast.class))
                    .getResultList();
            result.forEach(e -> {
                e.date(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 3, locale));
                e.time(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 2, locale));
            });
            return result;
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Anime> getTitlesForTimezone(String timezone, Locale locale) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String query = "SELECT\n" +
                    "  s.tid                                 AS tid,\n" +
                    "  s.date_start                          AS datestart,\n" +
                    "  COALESCE(h.en, h.ja)                  AS channel,\n" +
                    "  s.shift                               AS shift,\n" +
                    "  i.title                               AS title,\n" +
                    "  a.title_en                            AS titleen,\n" +
                    "  s.episode                             AS episode,\n" +
                    "  CASE\n" +
                    "  WHEN a.image = TRUE\n" +
                    "    THEN '/images/anime/thumbnails/' || CAST(z.aid AS TEXT) || '.jpg'\n" +
                    "  ELSE '/images/noimage.svg'\n" +
                    "  END                                   AS image\n" +
                    "FROM syoboi_timetable s\n" +
                    "  LEFT JOIN syoboi_ongoings o ON o.tid = s.tid\n" +
                    "  LEFT JOIN syoboi_info i ON o.tid = i.tid\n" +
                    "  LEFT JOIN channels h ON h.id = s.ch\n" +
                    "  LEFT JOIN ongoings z ON z.tid = s.tid\n" +
                    "  LEFT JOIN anidb a ON a.tid = z.tid\n" +
                    "WHERE\n" +
                    "  s.date_start BETWEEN date_trunc('day', now()) AND date_trunc('day', now()) + INTERVAL '2 week'\n" +
                    "ORDER BY s.date_start ASC";
            List<Anime> result = entityManager
                    .createNativeQuery(query)
                    .unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(Anime.class))
                    .getResultList();
            result.forEach(e -> {
                e.day(AnimeUtil.makeDaySupport(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone)));
                e.day().date(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 1, locale));
                e.time(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 2, locale));
            });
            return result;
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Anime> getTitlesForUser(String timezone, Object userid, Locale locale) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String query = "SELECT\n" +
                    "  s.tid                                 AS tid,\n" +
                    "  s.date_start                          AS datestart,\n" +
                    "  COALESCE(h.en, h.ja)                  AS channel,\n" +
                    "  s.shift                               AS shift,\n" +
                    "  i.title                               AS title,\n" +
                    "  a.title_en                            AS titleen,\n" +
                    "  s.episode                             AS episode,\n" +
                    "  CASE\n" +
                    "  WHEN a.image = TRUE\n" +
                    "    THEN '/images/anime/thumbnails/' || CAST(z.aid AS TEXT) || '.jpg'\n" +
                    "  ELSE '/images/noimage.svg'\n" +
                    "  END                                   AS image\n" +
                    "FROM syoboi_timetable s\n" +
                    "  LEFT JOIN syoboi_ongoings o ON o.tid = s.tid\n" +
                    "  LEFT JOIN syoboi_info i ON o.tid = i.tid\n" +
                    "  LEFT JOIN channels h ON h.id = s.ch\n" +
                    "  LEFT JOIN ongoings z ON z.tid = s.tid\n" +
                    "  LEFT JOIN anidb a ON a.tid = z.tid\n" +
                    "  INNER JOIN users_titles u ON u.tid = o.tid\n" +
                    "WHERE\n" +
                    "  s.date_start BETWEEN date_trunc('day', now()) AND date_trunc('day', now()) + INTERVAL '2 week'\n" +
                    "  AND u.user_id = :id\n" +
                    "ORDER BY S.date_start ASC";
            List<Anime> result = entityManager
                    .createNativeQuery(query)
                    .setParameter("id", userid)
                    .unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(Anime.class))
                    .getResultList();
            result.forEach(e -> {
                e.day(AnimeUtil.makeDaySupport(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone)));
                e.day().date(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 1, locale));
                e.time(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 2, locale));
            });
            return result;
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Anime> getTitlesForTimezoneMin(String timezone, Locale locale) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String query = "SELECT\n" +
                    "  s2.tid                                 AS tid,\n" +
                    "  s2.datestart                           AS datestart,\n" +
                    "  COALESCE(h.en, h.ja)                   AS channel,\n" +
                    "  s2.shift                               AS shift,\n" +
                    "  i.title                                AS title,\n" +
                    "  a.title_en                             AS titleen,\n" +
                    "  s2.episode                             AS episode,\n" +
                    "  CASE\n" +
                    "  WHEN a.image = TRUE\n" +
                    "    THEN '/images/anime/thumbnails/' || CAST(z.aid AS TEXT) || '.jpg'\n" +
                    "  ELSE '/images/noimage.svg'\n" +
                    "  END                                    AS image\n" +
                    "FROM (\n" +
                    "       WITH dates AS (\n" +
                    "           SELECT\n" +
                    "             min(date_trunc('week', date_start)) AS startw,\n" +
                    "             max(date_trunc('week', date_start)) AS endw\n" +
                    "           FROM syoboi_timetable\n" +
                    "       ),\n" +
                    "           weeks AS (\n" +
                    "             SELECT generate_series(startw, endw, '7 days') AS weekstart\n" +
                    "             FROM dates\n" +
                    "         ),\n" +
                    "           first AS (\n" +
                    "             SELECT DISTINCT ON (s.tid)\n" +
                    "               min(s.episode) AS first,\n" +
                    "               s.tid\n" +
                    "             FROM syoboi_timetable s\n" +
                    "             GROUP BY s.tid\n" +
                    "         ),\n" +
                    "           series AS (\n" +
                    "             SELECT\n" +
                    "               max(s.episode) AS series,\n" +
                    "               s.tid\n" +
                    "             FROM syoboi_timetable s\n" +
                    "               INNER JOIN first l ON l.tid = s.tid\n" +
                    "             WHERE s.date_start < date_trunc('day', now())\n" +
                    "                   OR s.episode = l.first\n" +
                    "             GROUP BY s.tid\n" +
                    "         )\n" +
                    "       SELECT DISTINCT ON (s.episode, s.tid)\n" +
                    "         min(s.date_start) AS datestart,\n" +
                    "         s.tid,\n" +
                    "         s.ch,\n" +
                    "         s.shift,\n" +
                    "         CASE\n" +
                    "         WHEN EXISTS(SELECT 1\n" +
                    "                     FROM syoboi_timetable q\n" +
                    "                     WHERE s.tid = q.tid AND s.episode = q.episode AND q.date_start < date_trunc('day', now()))\n" +
                    "           THEN NULL\n" +
                    "         ELSE s.episode\n" +
                    "         END              AS episode\n" +
                    "       FROM weeks w\n" +
                    "         LEFT JOIN syoboi_timetable s ON date_trunc('week', s.date_start) = w.weekstart\n" +
                    "         LEFT JOIN series f ON f.tid = s.tid\n" +
                    "       WHERE s.date_start NOTNULL\n" +
                    "             AND s.date_start BETWEEN date_trunc('day', now()) AND date_trunc('day', now()) + INTERVAL '2 week'\n" +
                    "             AND s.episode >= f.series\n" +
                    "       GROUP BY w.weekstart, s.episode, s.tid, s.ch, s.shift, s.episode, s.episode_name, s.date_start, f.series\n" +
                    "       ORDER BY s.episode, s.tid, min(s.date_start) ASC\n" +
                    "     ) s2\n" +
                    "  LEFT JOIN syoboi_ongoings o ON o.tid = s2.tid\n" +
                    "  LEFT JOIN syoboi_info i ON o.tid = i.tid\n" +
                    "  LEFT JOIN channels h ON h.id = s2.ch\n" +
                    "  LEFT JOIN ongoings z ON z.tid = s2.tid\n" +
                    "  LEFT JOIN anidb a ON a.tid = z.tid\n" +
                    "WHERE s2.episode NOTNULL\n" +
                    "ORDER BY s2.datestart, s2.episode ASC";
            List<Anime> result = entityManager
                    .createNativeQuery(query)
                    .unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(Anime.class))
                    .getResultList();
            result.forEach(e -> {
                e.day(AnimeUtil.makeDaySupport(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone)));
                e.day().date(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 1, locale));
                e.time(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 2, locale));
            });
            return result;
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Anime> getTitlesForUserMin(String timezone, Long userid, Locale locale) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String query = "SELECT\n" +
                    "  s2.tid                                 AS tid,\n" +
                    "  s2.datestart                           AS datestart,\n" +
                    "  COALESCE(h.en, h.ja)                   AS channel,\n" +
                    "  s2.shift                               AS shift,\n" +
                    "  i.title                                AS title,\n" +
                    "  a.title_en                             AS titleen,\n" +
                    "  s2.episode                             AS episode,\n" +
                    "  CASE\n" +
                    "  WHEN a.image = TRUE\n" +
                    "    THEN '/images/anime/thumbnails/' || CAST(z.aid AS TEXT) || '.jpg'\n" +
                    "  ELSE '/images/noimage.svg'\n" +
                    "  END                                    AS image\n" +
                    "FROM (\n" +
                    "       WITH dates AS (\n" +
                    "           SELECT\n" +
                    "             min(date_trunc('week', date_start)) AS startw,\n" +
                    "             max(date_trunc('week', date_start)) AS endw\n" +
                    "           FROM syoboi_timetable\n" +
                    "       ),\n" +
                    "           weeks AS (\n" +
                    "             SELECT generate_series(startw, endw, '7 days') AS weekstart\n" +
                    "             FROM dates\n" +
                    "         ),\n" +
                    "           first AS (\n" +
                    "             SELECT DISTINCT ON (s.tid)\n" +
                    "               min(s.episode) AS first,\n" +
                    "               s.tid\n" +
                    "             FROM syoboi_timetable s\n" +
                    "             GROUP BY s.tid\n" +
                    "         ),\n" +
                    "           series AS (\n" +
                    "             SELECT\n" +
                    "               max(s.episode) AS series,\n" +
                    "               s.tid\n" +
                    "             FROM syoboi_timetable s\n" +
                    "               INNER JOIN first l ON l.tid = s.tid\n" +
                    "             WHERE s.date_start < date_trunc('day', now())\n" +
                    "                   OR s.episode = l.first\n" +
                    "             GROUP BY s.tid\n" +
                    "         )\n" +
                    "       SELECT DISTINCT ON (s.episode, s.tid)\n" +
                    "         min(s.date_start) AS datestart,\n" +
                    "         s.tid,\n" +
                    "         s.ch,\n" +
                    "         s.shift,\n" +
                    "         CASE\n" +
                    "         WHEN EXISTS(SELECT 1\n" +
                    "                     FROM syoboi_timetable q\n" +
                    "                     WHERE s.tid = q.tid AND s.episode = q.episode AND q.date_start < date_trunc('day', now()))\n" +
                    "           THEN NULL\n" +
                    "         ELSE s.episode\n" +
                    "         END              AS episode\n" +
                    "       FROM weeks w\n" +
                    "         LEFT JOIN syoboi_timetable s ON date_trunc('week', s.date_start) = w.weekstart\n" +
                    "         LEFT JOIN series f ON f.tid = s.tid\n" +
                    "         INNER JOIN users_titles u ON u.tid = s.tid\n" +
                    "       WHERE s.date_start NOTNULL\n" +
                    "             AND s.date_start BETWEEN date_trunc('day', now()) AND date_trunc('day', now()) + INTERVAL '2 week'\n" +
                    "             AND s.episode >= f.series\n" +
                    "             AND u.user_id = :id\n" +
                    "       GROUP BY w.weekstart, s.episode, s.tid, s.ch, s.shift, s.episode, s.episode_name, s.date_start, f.series\n" +
                    "       ORDER BY s.episode, s.tid, min(s.date_start) ASC\n" +
                    "     ) s2\n" +
                    "  LEFT JOIN syoboi_ongoings o ON o.tid = s2.tid\n" +
                    "  LEFT JOIN syoboi_info i ON o.tid = i.tid\n" +
                    "  LEFT JOIN channels h ON h.id = s2.ch\n" +
                    "  LEFT JOIN ongoings z ON z.tid = s2.tid\n" +
                    "  LEFT JOIN anidb a ON a.tid = z.tid\n" +
                    "WHERE s2.episode NOTNULL\n" +
                    "ORDER BY s2.datestart, s2.episode ASC";
            List<Anime> result = entityManager
                    .createNativeQuery(query)
                    .setParameter("id", userid)
                    .unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(Anime.class))
                    .getResultList();
            result.forEach(e -> {
                e.day(AnimeUtil.makeDaySupport(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone)));
                e.day().date(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 1, locale));
                e.time(AnimeUtil.ZonesManipulations(e.datestart().toInstant().getEpochSecond(), ZoneId.of(timezone), 2, locale));
            });
            return result;
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
