CREATE OR REPLACE VIEW v_event_member_bass AS
  SELECT
    ev_member_int.event_id       AS event_id,
    COUNT(ev_member_int.id)      AS count_bass,
    GROUP_CONCAT(person.first_name,
                 ' ',
                 person.last_name
                 SEPARATOR ', ') AS bass_name
  FROM m_member_event ev_member_int
    INNER JOIN members member
      ON ev_member_int.member_id = member.id
    INNER JOIN c_list_of_value voice
      ON member.voice_id = voice.id
    INNER JOIN c_list_of_value invitation_status
      ON ev_member_int.invitation_status_id = invitation_status.id
    INNER JOIN persons person
      ON member.person_id = person.id
  WHERE
    voice.identifier = 'BASS' AND
    invitation_status.identifier = 'ACCEPTED'
  GROUP BY ev_member_int.event_id