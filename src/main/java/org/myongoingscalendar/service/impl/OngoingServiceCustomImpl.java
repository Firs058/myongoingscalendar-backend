package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.model.ReturnTitleGrids;
import org.myongoingscalendar.model.UserTitle;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.repository.OngoingRepositoryCustom;
import org.myongoingscalendar.service.OngoingServiceCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * @author firs
 */
@Service
public class OngoingServiceCustomImpl implements OngoingServiceCustom {

    private final OngoingRepositoryCustom ongoingRepositoryCustom;

    @Autowired
    public OngoingServiceCustomImpl(OngoingRepositoryCustom ongoingRepositoryCustom) {
        this.ongoingRepositoryCustom = ongoingRepositoryCustom;
    }

    @Override
    public List<ReturnTitleGrids> getOngoingsFull(String userTimezone, Locale locale) {
        return ongoingRepositoryCustom.getOngoingsFull(userTimezone, locale);
    }

    @Override
    public List<ReturnTitleGrids> getOngoingsMin(String userTimezone, Locale locale) {
        return ongoingRepositoryCustom.getOngoingsMin(userTimezone, locale);
    }

    @Override
    public List<ReturnTitleGrids> getUserOngoingsFull(String userTimezone, Long userid, Locale locale) {
        return ongoingRepositoryCustom.getUserOngoingsFull(userTimezone, userid, locale);
    }

    @Override
    public List<ReturnTitleGrids> getUserOngoingsMin(String userTimezone, Long userid, Locale locale) {
        return ongoingRepositoryCustom.getUserOngoingsMin(userTimezone, userid, locale);
    }

    @Override
    public UserTitle getOngoingData(Long tid, String timezone, Locale locale) {
        return ongoingRepositoryCustom.getOngoingData(tid, timezone, locale);
    }

    @Override
    public UserTitle getUserOngoingData(Long tid, String timezone, Long userid, Locale locale) {
        return ongoingRepositoryCustom.getUserOngoingData(tid, timezone, userid, locale);
    }

    @Override
    public List<ElasticAnime> getElasticData() {
        return ongoingRepositoryCustom.getElasticData();
    }
}
