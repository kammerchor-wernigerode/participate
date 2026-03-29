CREATE
OR REPLACE VIEW v_participants_accepted AS
SELECT participant.event_id AS event_id, COUNT(participant.id) AS count_accepted
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
WHERE participant.invitation_status = 'ACCEPTED'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participants_declined AS
SELECT participant.event_id                                               AS event_id,
       COUNT(participant.id)                                              AS count_declined,
       STRING_AGG(CONCAT(person.first_name, ' ', person.last_name), ', ') AS declined_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE participant.invitation_status = 'DECLINED'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participants_pending AS
SELECT participant.event_id AS event_id, COUNT(participant.id) AS count_pending
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
WHERE participant.invitation_status = 'PENDING'
   OR participant.invitation_status = 'UNINVITED'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participants_tentative AS
SELECT participant.event_id                                               AS event_id,
       COUNT(participant.id)                                              AS count_tentative,
       STRING_AGG(CONCAT(person.first_name, ' ', person.last_name), ', ') AS tentative_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE participant.invitation_status = 'TENTATIVE'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participants_soprano AS
SELECT participant.event_id                                               AS event_id,
       COUNT(participant.id)                                              AS count_soprano,
       STRING_AGG(CONCAT(person.first_name, ' ', person.last_name), ', ') AS soprano_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 'SOPRANO'
  AND participant.invitation_status = 'ACCEPTED'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participant_alto AS
SELECT participant.event_id                                               AS event_id,
       COUNT(participant.id)                                              AS count_alto,
       STRING_AGG(CONCAT(person.first_name, ' ', person.last_name), ', ') AS alto_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 'ALTO'
  AND participant.invitation_status = 'ACCEPTED'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participant_tenor AS
SELECT participant.event_id                                               AS event_id,
       COUNT(participant.id)                                              AS count_tenor,
       STRING_AGG(CONCAT(person.first_name, ' ', person.last_name), ', ') AS tenor_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 'TENOR'
  AND participant.invitation_status = 'ACCEPTED'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participant_bass AS
SELECT participant.event_id                                               AS event_id,
       COUNT(participant.id)                                              AS count_bass,
       STRING_AGG(CONCAT(person.first_name, ' ', person.last_name), ', ') AS bass_name
FROM participants participant
         INNER JOIN singers singer ON participant.singer_id = singer.id
         INNER JOIN persons person ON singer.id = person.id
WHERE singer.voice = 'BASS'
  AND participant.invitation_status = 'ACCEPTED'
GROUP BY participant.event_id;


CREATE
OR REPLACE VIEW v_participant_car AS
SELECT participant.event_id            AS event_id,
       COUNT(participant.id)           AS count_car,
       SUM(participant.car_seat_count) AS count_car_seat
FROM participants participant
WHERE participant.invitation_status = 'ACCEPTED'
  AND participant.car_seat_count > -1
GROUP BY participant.event_id;
