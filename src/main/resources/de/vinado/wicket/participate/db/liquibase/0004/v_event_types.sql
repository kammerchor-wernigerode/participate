CREATE OR REPLACE VIEW v_event_types AS
SELECT ev.event_type
FROM events ev
WHERE ev.event_type IS NOT NULL
GROUP BY ev.event_type
ORDER BY count(*) DESC;
