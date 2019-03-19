package org.myongoingscalendar.model;

public class BlockingDAOImpl implements BlockingDAO {
    @Override
    public PostgresResponse checkIP(String destination, String ip) {
        return null;
    }
/*
    private JdbcTemplate jdbcTemplate;
    private PostgresResponse postgresResponse = new PostgresResponse();

    public BlockingDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    create function p_ip_blocking(input_destination text, input_ip inet) returns TABLE(banned boolean, code integer)
LANGUAGE plpgsql
AS $$
DECLARE
  ban                BOOLEAN;
  global_count_exist INT;
  count_exist        INT;
  temp_id            INT;
  limit_day          TIMESTAMP;
BEGIN
  SELECT
    i.day_count,
    i.id,
    i.global_count,
    i.banned,
    i.date
  INTO count_exist, temp_id, global_count_exist, ban, limit_day
  FROM ipblock i
  WHERE i.ip = input_ip AND i.destination = input_destination;
  IF ban IS TRUE
  THEN
    RETURN QUERY
    SELECT
      TRUE AS "banned",
      2    AS "code";
  ELSE
    IF EXTRACT(EPOCH FROM limit_day) -
       EXTRACT(EPOCH FROM now()) > 86400
    THEN
      UPDATE ipblock
      SET day_count = 1
      WHERE id = temp_id;
    END IF;
    IF count_exist ISNULL AND global_count_exist ISNULL
    THEN
      INSERT INTO ipblock (destination, ip, day_count, global_count, date, banned)
      VALUES (input_destination, input_ip, 1, 1, now(), FALSE);
      RETURN QUERY
      SELECT
        FALSE AS "banned",
        0     AS "code";
    ELSEIF
      count_exist < 10 AND global_count_exist < 4
      THEN
        UPDATE ipblock
        SET day_count = count_exist + 1
        WHERE id = temp_id;
        RETURN QUERY
        SELECT
          FALSE AS "banned",
          0     AS "code";
    ELSEIF
      count_exist >= 10 AND global_count_exist < 4 AND EXTRACT(EPOCH FROM limit_day) -
                                                       EXTRACT(EPOCH FROM now()) > 86400
      THEN
        UPDATE ipblock
        SET global_count = ipblock.global_count + 1
        WHERE id = temp_id;
        RETURN QUERY
        SELECT
          FALSE AS "banned",
          2     AS "code";
    ELSEIF
      count_exist >= 10 AND global_count_exist < 4
      THEN
        RETURN QUERY
        SELECT
          FALSE AS "banned",
          2     AS "code";
    ELSEIF
      global_count_exist >= 4
      THEN
        UPDATE ipblock
        SET banned = TRUE
        WHERE id = temp_id;
        RETURN QUERY
        SELECT
          TRUE AS "banned",
          2    AS "code";
    END IF;
  END IF;
END;
$$;


    @Override
    public PostgresResponse checkIP(String input_destination, String input_ip) {
        String sql = "SELECT * FROM p_ip_blocking(?,? :: INET)";
        return jdbcTemplate.query(sql, new Object[]{input_destination, input_ip}, new int[]{Types.VARCHAR, Types.VARCHAR}, rs -> {
            if (rs.next()) {
                postgresResponse.setBanned(rs.getBoolean("banned"));
                postgresResponse.setCode(rs.getInt("code"));
                return postgresResponse;
            }
            return null;
        });
    }
    */
}