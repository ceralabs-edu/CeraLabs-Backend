CREATE table users
(
    id            serial primary key,
    role_id   smallint                            not null,
    created_at    timestamp default CURRENT_TIMESTAMP not null,
    updated_at    timestamp default CURRENT_TIMESTAMP not null,
    email         varchar(255)                        not null constraint users_pk unique,
    password_hash varchar(255)                        not null,
    verified      boolean   default false             not null,
    otp_id        bigint
);

CREATE table roles
(
    id   smallint primary key,
    name varchar(50) not null
);

INSERT INTO roles (id, name) VALUES
(1, 'admin'),
(2, 'user'),
(3, 'guest'),
(4, 'teacher'),
(5, 'student'),
(6, 'parent'),
(7, 'organization'),
(8, 'superadmin');


CREATE table user_informations
(
   id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    city_code VARCHAR(10), -- Reference to province code
    sub_district_code VARCHAR(10), -- Reference to commune code,
    address_detail VARCHAR(500),
    school VARCHAR(255),
    grade VARCHAR(50),
    favorite_subjects TEXT, -- Array of subjects
    bio TEXT,
    date_of_birth DATE,
    avatar_image VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (city_code) REFERENCES provinces(code),
    FOREIGN KEY (sub_district_code) REFERENCES communes(code)
);


CREATE INDEX idx_user_informations_user_id ON user_informations(user_id);
CREATE INDEX idx_user_informations_city_code ON user_informations(city_code);
CREATE INDEX idx_user_informations_sub_district_code ON user_informations(sub_district_code);