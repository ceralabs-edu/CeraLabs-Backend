-- =====================================================
-- ROLES
-- =====================================================
CREATE TABLE IF NOT EXISTS roles (
                                     id SMALLSERIAL PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE,
                                     priority SMALLINT NOT NULL
);

INSERT INTO roles (name, priority)
VALUES
    ('ROLE_ADMIN', 0),
    ('ROLE_ORGANIZATION', 1),
    ('ROLE_TEACHER', 2),
    ('ROLE_STUDENT', 3)
ON CONFLICT (name) DO NOTHING;


-- =====================================================
-- PROVINCES
-- =====================================================
CREATE TABLE IF NOT EXISTS provinces (
                                         id SERIAL PRIMARY KEY,

                                         code VARCHAR(10) NOT NULL UNIQUE,
                                         name TEXT NOT NULL,
                                         slug TEXT NOT NULL,
                                         type VARCHAR(50) NOT NULL,
                                         full_name TEXT NOT NULL,

                                         created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                         updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_provinces_code
    ON provinces (code);

CREATE INDEX IF NOT EXISTS idx_provinces_slug
    ON provinces (slug);


-- =====================================================
-- COMMUNES
-- =====================================================
CREATE TABLE IF NOT EXISTS communes (
                                        id SERIAL PRIMARY KEY,

                                        code VARCHAR(10) NOT NULL UNIQUE,
                                        name TEXT NOT NULL,
                                        slug TEXT NOT NULL,
                                        type VARCHAR(50) NOT NULL,
                                        full_name TEXT NOT NULL,

                                        path VARCHAR(500) NOT NULL,
                                        path_with_type VARCHAR(500) NOT NULL,

                                        province_code VARCHAR(10) NOT NULL,

                                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                        updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                        CONSTRAINT fk_commune_province
                                            FOREIGN KEY (province_code)
                                                REFERENCES provinces (code)
                                                ON UPDATE CASCADE
                                                ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_communes_code
    ON communes (code);

CREATE INDEX IF NOT EXISTS idx_communes_slug
    ON communes (slug);

CREATE INDEX IF NOT EXISTS idx_communes_province_code
    ON communes (province_code);


-- =====================================================
-- RAW ADDRESS (TEMP)
-- =====================================================
DROP TABLE IF EXISTS raw_address;

CREATE TEMP TABLE raw_address (
                                  id TEXT,
                                  ten_tinh TEXT,
                                  ten_tinh_khong_dau TEXT,
                                  loai_tinh TEXT,
                                  full_name_tinh TEXT,

                                  ten_xa_phuong TEXT,
                                  loai_xa TEXT,

                                  slug TEXT,
                                  name_with_type TEXT,
                                  path TEXT,
                                  path_with_type TEXT,

                                  code TEXT,
                                  parent_code TEXT
);

-- =====================================================
-- IMPORT CSV
-- =====================================================
COPY raw_address
    FROM '/docker-entrypoint-initdb.d/data.csv'
    DELIMITER ','
    CSV HEADER;


-- =====================================================
-- POPULATE PROVINCES
-- =====================================================
INSERT INTO provinces (code, name, slug, type, full_name)
SELECT DISTINCT
    parent_code            AS code,
    ten_tinh               AS name,
    ten_tinh_khong_dau     AS slug,
    loai_tinh              AS type,
    full_name_tinh         AS full_name
FROM raw_address
WHERE parent_code IS NOT NULL
  AND parent_code <> ''
ON CONFLICT (code) DO NOTHING;


-- =====================================================
-- POPULATE COMMUNES
-- =====================================================
INSERT INTO communes (
    code,
    name,
    slug,
    type,
    full_name,
    path,
    path_with_type,
    province_code
)
SELECT
    code,
    ten_xa_phuong,
    slug,
    loai_xa,
    name_with_type,
    path,
    path_with_type,
    parent_code
FROM raw_address
WHERE code IS NOT NULL
  AND code <> ''
  AND parent_code IS NOT NULL
  AND parent_code <> ''
ON CONFLICT (code) DO NOTHING;
