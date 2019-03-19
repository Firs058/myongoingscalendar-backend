package org.myongoingscalendar.service;

import org.myongoingscalendar.model.Status;

/**
 * @author firs
 */
public interface UserServiceCustom {
    Status toggleUserTid(Long tid, Long user_id);
}
