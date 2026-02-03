ALTER TABLE user_information
    ALTER COLUMN city_code DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;