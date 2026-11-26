ALTER TABLE event ADD COLUMN organizer VARCHAR(200);
UPDATE event SET organizer = 'olivia@example.com';
