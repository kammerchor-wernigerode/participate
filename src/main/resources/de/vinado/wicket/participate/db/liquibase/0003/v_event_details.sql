CREATE OR REPLACE VIEW v_event_member_accepted AS
    SELECT
        ev_member_int.event_id  AS event_id,
        COUNT(ev_member_int.id) AS count_accepted
    FROM m_member_event ev_member_int
        INNER JOIN members member ON ev_member_int.member_id = member.id
    WHERE ev_member_int.invitation_status = 0
    GROUP BY ev_member_int.event_id;


CREATE OR REPLACE VIEW v_event_member_declined AS
    SELECT
        ev_member_int.event_id       AS event_id,
        COUNT(ev_member_int.id)      AS count_declined,
        GROUP_CONCAT(person.first_name,
                     ' ',
                     person.last_name
                     SEPARATOR ', ') AS declined_name
    FROM m_member_event ev_member_int
        INNER JOIN members member
            ON ev_member_int.member_id = member.id
        INNER JOIN persons person
            ON member.person_id = person.id
    WHERE ev_member_int.invitation_status = 1
    GROUP BY ev_member_int.event_id;


CREATE OR REPLACE VIEW v_event_member_pending AS
    SELECT
        ev_member_int.event_id  AS event_id,
        COUNT(ev_member_int.id) AS count_pending
    FROM m_member_event ev_member_int
        INNER JOIN members member
            ON ev_member_int.member_id = member.id
    WHERE ev_member_int.invitation_status = 2
        OR ev_member_int.invitation_status = 3
    GROUP BY ev_member_int.event_id;


CREATE OR REPLACE VIEW v_event_member_soprano AS
    SELECT
        ev_member_int.event_id       AS event_id,
        COUNT(ev_member_int.id)      AS count_soprano,
        GROUP_CONCAT(person.first_name,
                     ' ',
                     person.last_name
                     SEPARATOR ', ') AS soprano_name
    FROM m_member_event ev_member_int
        INNER JOIN members member
            ON ev_member_int.member_id = member.id
        INNER JOIN persons person
            ON member.person_id = person.id
    WHERE
        member.voice = 0 AND
        ev_member_int.invitation_status = 0
    GROUP BY ev_member_int.event_id;


CREATE OR REPLACE VIEW v_event_member_alto AS
    SELECT
        ev_member_int.event_id       AS event_id,
        COUNT(ev_member_int.id)      AS count_alto,
        GROUP_CONCAT(person.first_name,
                     ' ',
                     person.last_name
                     SEPARATOR ', ') AS alto_name
    FROM m_member_event ev_member_int
        INNER JOIN members member
            ON ev_member_int.member_id = member.id
        INNER JOIN persons person
            ON member.person_id = person.id
    WHERE
        member.voice = 1 AND
        ev_member_int.invitation_status = 0
    GROUP BY ev_member_int.event_id;


CREATE OR REPLACE VIEW v_event_member_tenor AS
    SELECT
        ev_member_int.event_id       AS event_id,
        COUNT(ev_member_int.id)      AS count_tenor,
        GROUP_CONCAT(person.first_name,
                     ' ',
                     person.last_name
                     SEPARATOR ', ') AS tenor_name
    FROM m_member_event ev_member_int
        INNER JOIN members member
            ON ev_member_int.member_id = member.id
        INNER JOIN persons person
            ON member.person_id = person.id
        WHERE
            member.voice = 2 AND
            ev_member_int.invitation_status = 0
    GROUP BY ev_member_int.event_id;


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
        INNER JOIN persons person
            ON member.person_id = person.id
    WHERE
        member.voice = 3 AND
        ev_member_int.invitation_status = 0
    GROUP BY ev_member_int.event_id;

CREATE OR REPLACE VIEW v_event_details AS
    SELECT
        ev.id                                      AS id,
        ev.name                                    AS name,
        ev.event_type                              AS type,
        ev.description                             AS description,
        ev.start_date                              AS start_date,
        ev.end_date                                AS end_date,
        ev.location                                AS location,
        CONCAT(COALESCE(accepted.count_accepted, 0), '/',
               COALESCE(declined.count_declined, 0), '/',
               COALESCE(pending.count_pending, 0)) AS count_accepted_declined_pending,
        SUM(ev_member.needs_dinner)                AS count_dinner,
        SUM(ev_member.needs_place_to_sleep)        AS count_place_to_sleep,
        COALESCE(accepted.count_accepted, 0)       AS count_accepted,
        COALESCE(declined.count_declined, 0)       AS count_declined,
        COALESCE(pending.count_pending, 0)         AS count_pending,
        COALESCE(soprano.count_soprano, 0)         AS count_soprano,
        COALESCE(alto.count_alto, 0)               AS count_alto,
        COALESCE(tenor.count_tenor, 0)             AS count_tenor,
        COALESCE(bass.count_bass, 0)               AS count_bass,
        soprano.soprano_name                       AS member_soprano,
        alto.alto_name                             AS member_alto,
        tenor.tenor_name                           AS member_tenor,
        bass.bass_name                             AS member_bass,
        declined.declined_name                     AS member_declined,
        (COALESCE(accepted.count_accepted, 0) +
         COALESCE(declined.count_declined, 0) +
         COALESCE(pending.count_pending, 0))       AS count_member
    FROM events ev
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
        ev.event_type,
        ev.start_date,
        ev.end_date,
        ev.description,
             ev.location
    ORDER BY ev.start_date ASC;