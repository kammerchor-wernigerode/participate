CREATE OR REPLACE VIEW v_participant_car AS
SELECT participant.event_id            AS event_id,
       COUNT(participant.id)           AS count_car,
       SUM(participant.car_seat_count) as count_car_seat
FROM participants participant
WHERE participant.invitation_status = 0
  AND participant.car_seat_count > -1
GROUP BY participant.event_id;

CREATE OR REPLACE VIEW v_participants_tentative AS
SELECT participant.event_id         AS event_id,
       COUNT(participant.id)        AS count_tentative,
       GROUP_CONCAT(person.first_name,
                    ' ',
                    person.last_name
                    SEPARATOR ', ') AS tentative_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE participant.invitation_status = 4
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
       COALESCE(car.count_car, 0)                 AS count_car,
       COALESCE(car.count_car_seat, 0)            AS count_car_seat,
       COALESCE(accepted.count_accepted, 0)       AS count_accepted,
       COALESCE(declined.count_declined, 0)       AS count_declined,
       COALESCE(tentative.count_tentative, 0)     AS count_tentative,
       COALESCE(pending.count_pending, 0)         AS count_pending,
       COALESCE(soprano.count_soprano, 0)         AS count_soprano,
       COALESCE(alto.count_alto, 0)               AS count_alto,
       COALESCE(tenor.count_tenor, 0)             AS count_tenor,
       COALESCE(bass.count_bass, 0)               AS count_bass,
       soprano.soprano_name                       AS soprano,
       alto.alto_name                             AS alto,
       tenor.tenor_name                           AS tenor,
       bass.bass_name                             AS bass,
       tentative.tentative_name                   AS tentative,
       declined.declined_name                     AS declined,
       (COALESCE(accepted.count_accepted, 0) +
        COALESCE(declined.count_declined, 0) +
        COALESCE(pending.count_pending, 0))       AS count_invitations,
       CAST_TO_BIT(ev.is_active)                  AS is_active,
       ev.creation_date                           AS creation_date
FROM events ev
         LEFT JOIN participants participant ON ev.id = participant.event_id
         LEFT JOIN v_participants_accepted accepted ON ev.id = accepted.event_id
         LEFT JOIN v_participants_tentative tentative ON ev.id = tentative.event_id
         LEFT JOIN v_participants_declined declined ON ev.id = declined.event_id
         LEFT JOIN v_participants_pending pending ON ev.id = pending.event_id
         LEFT JOIN v_participants_soprano soprano ON ev.id = soprano.event_id
         LEFT JOIN v_participant_alto alto ON ev.id = alto.event_id
         LEFT JOIN v_participant_tenor tenor ON ev.id = tenor.event_id
         LEFT JOIN v_participant_bass bass ON ev.id = bass.event_id
         LEFT JOIN v_participant_car car ON ev.id = car.event_id
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
