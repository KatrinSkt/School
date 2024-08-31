CREATE TABLE people
(
    id            SERIAL,
    name          TEXT NOT NULL PRIMARY KEY,
    age           INTEGER CHECK ( age > 17 ),
    driverLicense BOOLEAN,
    car_id        SERIAL REFERENCES cars (id)
);

CREATE TABLE cars
(
    id    SERIAL PRIMARY KEY,
    brand TEXT NOT NULL,
    model TEXT NOT NULL,
    price REAL
);
