package org.myongoingscalendar.model;

public interface BlockingDAO {
    PostgresResponse checkIP(String destination, String ip);
}
