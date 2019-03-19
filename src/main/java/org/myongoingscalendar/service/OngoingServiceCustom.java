package org.myongoingscalendar.service;

import org.myongoingscalendar.model.ReturnTitleGrids;
import org.myongoingscalendar.model.UserTitle;
import org.myongoingscalendar.elastic.model.ElasticAnime;

import java.util.List;
import java.util.Locale;

/**
 * @author firs
 */
public interface OngoingServiceCustom {
    List<ReturnTitleGrids> getOngoingsFull(String userTimezone, Locale locale);

    List<ReturnTitleGrids> getOngoingsMin(String userTimezone, Locale locale);

    List<ReturnTitleGrids> getUserOngoingsFull(String userTimezone, Long userid, Locale locale);

    List<ReturnTitleGrids> getUserOngoingsMin(String userTimezone, Long userid, Locale locale);

    UserTitle getOngoingData(Long tid, String timezone, Locale locale);

    UserTitle getUserOngoingData(Long tid, String timezone, Long userid, Locale locale);

    List<ElasticAnime> getElasticData();
}
