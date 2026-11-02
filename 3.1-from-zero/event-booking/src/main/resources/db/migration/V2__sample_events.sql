-- Chapter 3.2: Flyway runs this script once, after V1.
INSERT INTO event (title, city, starts_at, capacity, price) VALUES
    ('Jazz Night',              'Vienna', '2027-03-12 20:00', 120, 39.00),
    ('Spring Boot Meetup',      'Graz',   '2027-03-18 18:30',  80,  0.00),
    ('String Quartet in Concert', 'Linz', '2027-04-02 19:30', 300, 55.00);
