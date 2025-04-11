-- Restaurant
INSERT INTO location (name, latitude, longitude, address, description, image_url, location_type, opening_hours, phone_number, rating, website)
VALUES
    ('Variety Jones', 53.3430, -6.2770, '78 Thomas Street, Dublin 8', 'Michelin-starred restaurant offering contemporary Irish cuisine', 'https://example.com/varietyjones.jpg', 'Restaurant', 'Tue-Sat: 5:30 PM - 10 PM', '+353-1-516-3100', 4.8, 'https://www.varietyjones.ie'),
    ('The Church Café Bar & Restaurant', 53.3480, -6.2670, 'Junction of Mary St & Jervis St, Dublin 1', 'Unique dining experience in a restored church with live music', 'https://example.com/thechurch.jpg', 'Restaurant', 'Mon-Sun: 10:30 AM - 11:30 PM', '+353-1-828-0102', 4.5, 'https://www.thechurch.ie'),
    ('Chapter One', 53.3531, -6.2635, '18-19 Parnell Square N, Rotunda, Dublin 1', 'Michelin-starred restaurant offering contemporary Irish cuisine', 'https://example.com/chapterone.jpg', 'Restaurant', 'Tue-Sat: 6 PM - 10 PM', '+353-1-873-2266', 4.8, 'https://www.chapteronerestaurant.com'),
    ('The Winding Stair', 53.3460, -6.2649, '40 Ormond Quay Lower, North City, Dublin 1', 'Traditional Irish restaurant with views over the River Liffey', 'https://example.com/windingstair.jpg', 'Restaurant', 'Mon-Sun: 12 PM - 10 PM', '+353-1-872-7320', 4.5, 'https://winding-stair.com'),
    ('Sole Seafood & Grill', 53.3408, -6.2588, '18-19 South William Street, Dublin 2', 'Award-winning seafood restaurant in the heart of Dublin', 'https://example.com/sole.jpg', 'Restaurant', 'Mon-Sun: 12 PM - 10 PM', '+353-1-544-2300', 4.7, 'https://www.sole.ie');

-- Hotels
INSERT INTO location (name, latitude, longitude, address, description, image_url, location_type, opening_hours, phone_number, rating, website)
VALUES
    ('The Merrion Hotel', 53.3398, -6.2523, 'Upper Merrion Street, Dublin 2', 'Luxurious hotel set in a restored Georgian building', 'https://example.com/merrionhotel.jpg', 'Hotel', '24/7', '+353-1-603-0600', 4.9, 'https://www.merrionhotel.com'),
    ('The Marker Hotel', 53.3440, -6.2390, 'Grand Canal Square, Docklands, Dublin 2', 'Modern hotel with a rooftop bar offering city views', 'https://example.com/markerhotel.jpg', 'Hotel', '24/7', '+353-1-687-5100', 4.7, 'https://www.themarkerhoteldublin.com'),
    ('The Shelbourne Dublin', 53.3382, -6.2588, '27 St Stephen\'s Green, Dublin 2', 'Historic five-star hotel overlooking St Stephen\'s Green', 'https://example.com/shelbourne.jpg', 'Hotel', '24/7', '+353-1-663-4500', 4.7, 'https://www.theshelbourne.com'),
    ('The Westbury', 53.3418, -6.2603, 'Balfe Street, Dublin 2', 'Luxury hotel situated between Trinity College and St Stephen’s Green', 'https://example.com/westbury.jpg', 'Hotel', '24/7', '+353-1-679-1122', 4.6, 'https://www.doylecollection.com/hotels/the-westbury-hotel'),
    ('The Dean Dublin', 53.3378, -6.2648, '33 Harcourt Street, Dublin 2', 'Boutique hotel with a vibrant atmosphere and rooftop bar', 'https://example.com/deandublin.jpg', 'Hotel', '24/7', '+353-1-607-8110', 4.5, 'https://www.thedean.ie');

-- Churches
INSERT INTO location (name, latitude, longitude, address, description, image_url, location_type, opening_hours, phone_number, rating, website)
VALUES
    ('St. Patrick\'s Cathedral', 53.3382, -6.2710, 'St Patrick\'s Close, Dublin 8', 'Ireland\'s largest cathedral, built in honor of Saint Patrick', 'https://example.com/stpatrick.jpg', 'Church', 'Mon-Sat: 9:30 AM - 5 PM; Sun: 9 AM - 10:30 AM', '+353-1-453-9472', 4.6, 'https://www.stpatrickscathedral.ie'),
('Christ Church Cathedral', 53.3434, -6.2711, 'Christchurch Pl, Wood Quay, Dublin 8', 'Historic cathedral founded in 1030, known for its medieval architecture', 'https://example.com/christchurch.jpg', 'Church', 'Mon-Sat: 9:30 AM - 5 PM; Sun: 12:30 PM - 2:30 PM', '+353-1-677-8099', 4.5, 'https://christchurchcathedral.ie'),
('St. Michan\'s Church', 53.3483, -6.2723, 'Church Street, Dublin 7', 'Historic church known for its mummified remains in the crypt', 'https://example.com/stmichans.jpg', 'Church', 'Mon-Sat: 10 AM - 4 PM', '+353-1-872-4154', 4.4, 'https://www.stmichans.com'),
('St. Audoen\'s Church', 53.3435, -6.2735, 'High Street, Dublin 8', 'Medieval church and the oldest parish church in Dublin', 'https://example.com/staudoens.jpg', 'Church', 'Mon-Fri: 9 AM - 5 PM', '+353-1-677-0088', 4.3, 'https://www.heritageireland.ie/places-to-visit/st-audoens-church'),
('St. Ann\'s Church', 53.3410, -6.2580, 'Dawson Street, Dublin 2', 'Historic Church of Ireland parish church known for its architecture', 'https://example.com/stanns.jpg', 'Church', 'Mon-Fri: 9 AM - 5 PM; Sun: 9 AM - 1 PM', '+353-1-676-7727', 4.5, 'https://www.stannschurch.ie');

-- Colleges
INSERT INTO location (name, latitude, longitude, address, description, image_url, location_type, opening_hours, phone_number, rating, website)
VALUES
    ('Trinity College Dublin', 53.3438, -6.2546, 'College Green, Dublin 2', 'Ireland\'s oldest university, renowned for its historic campus and the Book of Kells', 'https://example.com/trinity.jpg', 'College', 'Mon-Sun: 8 AM - 10 PM', '+353-1-896-1000', 4.7, 'https://www.tcd.ie'),
('Dublin Institute of Technology (DIT)', 53.3244, -6.2659, '143-149 Rathmines Rd Lower, Rathmines, Dublin 6', 'One of Ireland\'s largest higher education institutions, offering a wide range of programs', 'https://example.com/dit.jpg', 'College', 'Mon-Fri: 8 AM - 9 PM', '+353-1-402-3000', 4.4, 'https://www.dit.ie'),
    ('National College of Ireland', 53.3468, -6.2405, 'Mayor Street, IFSC, Dublin 1', 'Leading college offering business and technology courses', 'https://example.com/nci.jpg', 'College', 'Mon-Fri: 8 AM - 8 PM', '+353-1-449-8500', 4.5, 'https://www.ncirl.ie'),
    ('Griffith College Dublin', 53.3304, -6.2757, 'South Circular Road, Dublin 8', 'Private college offering a range of undergraduate and postgraduate programs', 'https://example.com/griffith.jpg', 'College', 'Mon-Fri: 9 AM - 6 PM', '+353-1-415-0400', 4.6, 'https://www.griffith.ie'),
    ('Royal College of Surgeons in Ireland (RCSI)', 53.3375, -6.2622, '123 St Stephen\'s Green, Dublin 2', 'Renowned medical college and research institution', 'https://example.com/rcsi.jpg', 'College', 'Mon-Fri: 8 AM - 6 PM', '+353-1-402-2100', 4.8, 'https://www.rcsi.com');
