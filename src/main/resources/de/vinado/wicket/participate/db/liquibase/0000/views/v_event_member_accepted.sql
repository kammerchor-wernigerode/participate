CREATE OR REPLACE VIEW v_event_member_accepted AS
  SELECT
    ev_member_int.event_id  AS event_id,
    COUNT(ev_member_int.id) AS count_accepted
  FROM m_member_event ev_member_int
    INNER JOIN members member
      ON ev_member_int.member_id = member.id
    INNER JOIN c_list_of_value invitation_status
      ON ev_member_int.invitation_status_id = invitation_status.id
  WHERE invitation_status.identifier = 'ACCEPTED'
  GROUP BY ev_member_int.event_id