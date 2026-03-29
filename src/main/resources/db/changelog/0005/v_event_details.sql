CREATE OR REPLACE VIEW v_participants_accepted AS
SELECT participant.event_id AS event_id, COUNT(participant.id) AS count_accepted
FROM participants participant
         INNER JOIN singers s ON participant.singer_id = s.id
WHERE participant.invitation_status = 0
GROUP BY participant.event_id;


CREATE OR REPLACE VIEW v_participants_declined AS
SELECT participant.event_id         AS event_id,
       COUNT(participant.id)        AS count_declined,
       GROUP_CONCAT(person.first_name,
                    ' ',
                    person.last_name
                    SEPARATOR ', ') AS declined_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE participant.invitation_status = 1
GROUP BY participant.event_id;


CREATE OR REPLACE VIEW v_participants_pending AS
SELECT participant.event_id AS event_id, COUNT(participant.id) AS count_pending
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
WHERE participant.invitation_status = 2
   OR participant.invitation_status = 3
GROUP BY participant.event_id;


CREATE OR REPLACE VIEW v_participants_soprano AS
SELECT participant.event_id         AS event_id,
       COUNT(participant.id)        AS count_soprano,
       GROUP_CONCAT(person.first_name,
                    ' ',
                    person.last_name
                    SEPARATOR ', ') AS soprano_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 0
  AND participant.invitation_status = 0
GROUP BY participant.event_id;


CREATE OR REPLACE VIEW v_participant_alto AS
SELECT participant.event_id         AS event_id,
       COUNT(participant.id)        AS count_alto,
       GROUP_CONCAT(person.first_name,
                    ' ',
                    person.last_name
                    SEPARATOR ', ') AS alto_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 1
  AND participant.invitation_status = 0
GROUP BY participant.event_id;


CREATE OR REPLACE VIEW v_participant_tenor AS
SELECT participant.event_id         AS event_id,
       COUNT(participant.id)        AS count_tenor,
       GROUP_CONCAT(person.first_name,
                    ' ',
                    person.last_name
                    SEPARATOR ', ') AS tenor_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 2
  AND participant.invitation_status = 0
GROUP BY participant.event_id;


CREATE OR REPLACE VIEW v_participant_bass AS
SELECT participant.event_id         AS event_id,
       COUNT(participant.id)        AS count_bass,
       GROUP_CONCAT(person.first_name,
                    ' ',
                    person.last_name
                    SEPARATOR ', ') AS bass_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 3
  AND participant.invitation_status = 0
GROUP BY participant.event_id;

CREATE OR REPLACE VIEW v_event_details AS
SELECT ev.id                                      AS id,
       ev.name                                    AS name,
       ev.event_type                              AS type,
       ev.description                             AS description,
       ev.start_date                              AS start_date,
       ev.end_date                                AS end_date,
       ev.location                                AS location,
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
        COALESCE(pending.count_pending, 0))       AS count_invitations,
       CAST_TO_BIT(ev.is_active)                  AS is_active,
       ev.creation_date                           AS creation_date
FROM events ev
         LEFT JOIN participants participant ON ev.id = participant.event_id
         LEFT JOIN v_participants_accepted accepted ON ev.id = accepted.event_id
         LEFT JOIN v_participants_declined declined ON ev.id = declined.event_id
         LEFT JOIN v_participants_pending pending ON ev.id = pending.event_id
         LEFT JOIN v_participants_soprano soprano ON ev.id = soprano.event_id
         LEFT JOIN v_participant_alto alto ON ev.id = alto.event_id
         LEFT JOIN v_participant_tenor tenor ON ev.id = tenor.event_id
         LEFT JOIN v_participant_bass bass ON ev.id = bass.event_id
GROUP BY ev.id,
         ev.name,
         ev.event_type,
         ev.start_date,
         ev.end_date,
         ev.description,
         ev.location,
         CAST_TO_BIT(ev.is_active),
         ev.creation_date
ORDER BY ev.start_date;