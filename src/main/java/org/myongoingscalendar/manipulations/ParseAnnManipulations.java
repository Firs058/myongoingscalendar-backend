package org.myongoingscalendar.manipulations;

import lombok.extern.slf4j.Slf4j;
import org.myongoingscalendar.entity.NewsEntity;
import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.entity.RatingEntity;
import org.myongoingscalendar.model.ANN.Ann;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.utils.AnimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ParseAnnManipulations {

    @Value("${parse.ann.path}")
    private String annPath;
    private final OngoingService ongoingService;

    @Autowired
    public ParseAnnManipulations(OngoingService ongoingService) {
        this.ongoingService = ongoingService;
    }

    @Transactional
    public void parseAnnForCurrentOngoings() {
        parse(ongoingService.getCurrentOngoings().stream().map(OngoingEntity::annid).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private void parse(List<Long> annids) {
        for (Long annid : annids) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Ann.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                Ann.Anime ann = ((Ann) jaxbUnmarshaller.unmarshal(new URL(annPath + annid))).getAnime();
                ongoingService.findByAnnid(annid).ifPresent(ongoing -> {

                    if (ann.getNews() != null) {
                        ann.getNews().forEach(e -> {
                            NewsEntity newsEntity = new NewsEntity()
                                    .datetime(e.getDatetime().toGregorianCalendar().getTime())
                                    .href(e.getHref())
                                    .headline(e.getValue().replaceAll("(<\\/?(\\s|\\S)*?>)", ""))
                                    .lang(Locale.ENGLISH)
                                    .source("https://animenewsnetwork.com")
                                    .ongoingEntity(ongoing);
                            if (ongoing.newsEntities().stream().noneMatch(i -> i.datetime().compareTo(newsEntity.datetime()) == 0))
                                ongoing.newsEntities().add(newsEntity);
                        });
                    }

                    if (ann.getRatings() != null) {
                        BigDecimal weightedScore = ann.getRatings().getWeightedScore().setScale(2, BigDecimal.ROUND_DOWN);

                        Optional<RatingEntity> ratingsEntity = ongoing.ratingEntities().stream()
                                .max(Comparator.comparing(RatingEntity::added));
                        if (ratingsEntity.isPresent() && AnimeUtil.daysBetween(ratingsEntity.get().added(), new Date()) == 0)
                            ratingsEntity.get().ann(weightedScore);
                        else ongoing.ratingEntities().add(
                                new RatingEntity()
                                        .ongoingEntity(ongoing)
                                        .ann(weightedScore));
                    }

                    ongoingService.save(ongoing);
                });
                Thread.sleep(3000);
            } catch (JAXBException | InterruptedException | MalformedURLException e) {
                log.error("Error parse ann " + annid, e);
            }
        }
    }
}
