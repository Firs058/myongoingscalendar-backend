package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author firs
 */
public enum RatingDB {
    ANIDB("AniDB", 1.0),
    MAL("MAL", 0.7),
    ANN("ANN", 0.4);

    private final String dbname;
    private final Double weight;

    private RatingDB(String dbname, Double weight) {
        this.dbname = dbname;
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    @JsonValue
    public String getDbname() {
        return dbname;
    }
}
