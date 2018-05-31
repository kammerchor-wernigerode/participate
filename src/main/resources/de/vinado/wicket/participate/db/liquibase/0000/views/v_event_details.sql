CREATE OR REPLACE VIEW v_event_details AS
  SELECT
    ev.id                                AS id,
    ev.name                              AS name,
    ev.type                              AS type,
    ev.description                       AS description,
    ev.start_date                        AS start_date,
    ev.end_date                          AS end_date,
    ev.location                          AS location,
    ev.cast                              AS cast,
    SUM(ev_member.needs_dinner)          AS count_dinner,
    SUM(ev_member.needs_place_to_sleep)  AS count_place_to_sleep,
    COALESCE(accepted.count_accepted, 0) AS count_accepted,
    COALESCE(declined.count_declined, 0) AS count_declined,
    COALESCE(pending.count_pending, 0)   AS count_pending,
    COALESCE(soprano.count_soprano, 0)   AS count_soprano,
    COALESCE(alto.count_alto, 0)         AS count_alto,
    COALESCE(tenor.count_tenor, 0)       AS count_tenor,
    COALESCE(bass.count_bass, 0)         AS count_bass,
    soprano.soprano_name                 AS member_soprano,
    alto.alto_name                       AS member_alto,
    tenor.tenor_name                     AS member_tenor,
    bass.bass_name                       AS member_bass,
    declined.declined_name               AS member_declined,
    (COALESCE(accepted.count_accepted, 0) +
     COALESCE(declined.count_declined, 0) +
     COALESCE(pending.count_pending, 0)) AS count_member
  FROM v_events ev
    LEFT JOIN m_member_event ev_member
      ON ev.id = ev_member.event_id
    LEFT JOIN v_event_member_accepted accepted
      ON ev.id = accepted.event_id
    LEFT JOIN v_event_member_declined declined
      ON ev.id = declined.event_id
    LEFT JOIN v_event_member_pending pending
      ON ev.id = pending.event_id
    LEFT JOIN v_event_member_soprano soprano
      ON ev.id = soprano.event_id
    LEFT JOIN v_event_member_alto alto
      ON ev.id = alto.event_id
    LEFT JOIN v_event_member_tenor tenor
      ON ev.id = tenor.event_id
    LEFT JOIN v_event_member_bass bass
      ON ev.id = bass.event_id
  GROUP BY
    ev.id,
    ev.name,
    ev.type,
    ev.start_date,
    ev.end_date,
    ev.description,
    ev.location,
    ev.cast