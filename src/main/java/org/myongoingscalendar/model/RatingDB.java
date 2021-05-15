package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigDecimal;

/**
 * @author firs
 */
public enum RatingDB {
    ANIDB("AniDB", new BigDecimal("1.0")),
    MAL("MAL", new BigDecimal("0.7")),
    ANN("ANN", new BigDecimal("0.2"));

    private final String dbname;
    private final BigDecimal weight;

    RatingDB(String dbname, BigDecimal weight) {
        this.dbname = dbname;
        this.weight = weight;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    @JsonValue
    public String getDbname() {
        return dbname;
    }
}
