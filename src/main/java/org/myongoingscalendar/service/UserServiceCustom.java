package org.myongoingscalendar.service;

import org.myongoingscalendar.model.Statistics.Statistics;
import org.myongoingscalendar.model.Status;

import java.math.BigDecimal;

/**
 * @author firs
 */
public interface UserServiceCustom {
    Status toggleUserTid(Long tid, Long user_id);

    Status setUserTitleScore(Long tid, Long user_id, BigDecimal score);

    Status removeUserTitleScore(Long tid, Long user_id);

    Status toggleUserTitleFavorite(Long tid, Long user_id);

    Statistics getUserStatistics(Long user_id);
}
