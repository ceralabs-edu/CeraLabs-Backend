CREATE TABLE provinces (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE communes (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    path VARCHAR(500) NOT NULL,
    path_with_type VARCHAR(500) NOT NULL,
    province_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (province_code) REFERENCES provinces(code)
);


CREATE INDEX idx_provinces_code ON provinces(code);
CREATE INDEX idx_provinces_slug ON provinces(slug);
CREATE INDEX idx_communes_code ON communes(code);
CREATE INDEX idx_communes_province_code ON communes(province_code);
CREATE INDEX idx_communes_slug ON communes(slug);