CREATE OR REPLACE VIEW v_events AS
  SELECT
    ev.id                                      AS id,
    ev.name                                    AS name,
    ev.event_type                              AS type,
    ev.description                             AS description,
    ev.start_date                              AS start_date,
    ev.end_date                                AS end_date,
    ad.locality                                AS location,
    gr.name                                    AS cast,
    CONCAT(COALESCE(accepted.count_accepted, 0), '/',
           COALESCE(declined.count_declined, 0), '/',
           COALESCE(pending.count_pending, 0)) AS count_accepted_declined_pending
  FROM events ev
    LEFT JOIN m_address_event ev_address
      ON ev.id = ev_address.event_id
    LEFT JOIN addresses ad
      ON ev_address.address_id = ad.id
    LEFT JOIN m_member_event ev_member
      ON ev.id = ev_member.event_id
    LEFT JOIN v_event_member_accepted accepted
      ON ev.id = accepted.event_id
    LEFT JOIN v_event_member_declined declined
      ON ev.id = declined.event_id
    LEFT JOIN v_event_member_pending pending
      ON ev.id = pending.event_id
    LEFT JOIN m_group_event ev_group
      ON ev.id = ev_group.event_id
    LEFT JOIN groups gr
      ON ev_group.group_id = gr.id
  GROUP BY
    ev.id,
    ev.name,
    ev.event_type,
    ev.start_date,
    ev.end_date,
    ev.description,
    ad.locality,
    gr.name
  ORDER BY ev.start_date ASC;