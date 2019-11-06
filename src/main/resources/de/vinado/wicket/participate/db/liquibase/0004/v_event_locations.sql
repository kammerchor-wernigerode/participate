CREATE OR REPLACE VIEW v_event_locations AS
SELECT ev.location
FROM events ev
WHERE ev.location IS NOT NULL
GROUP BY ev.location
ORDER BY count(*) DESC;
