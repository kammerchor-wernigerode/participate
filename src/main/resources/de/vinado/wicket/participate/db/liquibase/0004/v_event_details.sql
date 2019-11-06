CREATE OR REPLACE VIEW v_event_details AS
SELECT ev.id                                      AS id,
       CONCAT(COALESCE(accepted.count_accepted, 0), '/',
              COALESCE(declined.count_declined, 0), '/',
              COALESCE(pending.count_pending, 0)) AS count_accepted_declined_pending,
       SUM(participant.need_catering)             AS count_catering,
       SUM(participant.need_accommodation)        AS count_accommodation,
       COALESCE(accepted.count_accepted, 0)       AS count_accepted,
       COALESCE(declined.count_declined, 0)       AS count_declined,
       COALESCE(pending.count_pending, 0)         AS count_pending,
       COALESCE(soprano.count_soprano, 0)         AS count_soprano,
       COALESCE(alto.count_alto, 0)               AS count_alto,
       COALESCE(tenor.count_tenor, 0)             AS count_tenor,
       COALESCE(bass.count_bass, 0)               AS count_bass,
       soprano.soprano_name                       AS soprano,
       alto.alto_name                             AS alto,
       tenor.tenor_name                           AS tenor,
       bass.bass_name                             AS bass,
       declined.declined_name                     AS declined,
       (COALESCE(accepted.count_accepted, 0) +
        COALESCE(declined.count_declined, 0) +
        COALESCE(pending.count_pending, 0))       AS count_invitations
FROM events ev
         LEFT JOIN participants participant ON ev.id = participant.event_id
         LEFT JOIN v_participants_accepted accepted ON ev.id = accepted.event_id
         LEFT JOIN v_participants_declined declined ON ev.id = declined.event_id
         LEFT JOIN v_participants_pending pending ON ev.id = pending.event_id
         LEFT JOIN v_participants_soprano soprano ON ev.id = soprano.event_id
         LEFT JOIN v_participant_alto alto ON ev.id = alto.event_id
         LEFT JOIN v_participant_tenor tenor ON ev.id = tenor.event_id
         LEFT JOIN v_participant_bass bass ON ev.id = bass.event_id
WHERE ev.is_active IS TRUE
  AND ev.end_date > CURRENT_TIMESTAMP
GROUP BY ev.id,
         ev.name,
         ev.event_type,
         ev.start_date,
         ev.end_date,
         ev.description,
         ev.location
ORDER BY ev.start_date;
