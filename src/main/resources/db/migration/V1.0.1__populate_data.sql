INSERT INTO roles (id, name, priority)
VALUES
    (1, 'ROLE_ADMIN', 0),
    (2, 'ROLE_ORGANIZATION', 1),
    (3, 'ROLE_TEACHER', 2),
    (4, 'ROLE_STUDENT', 3);

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
    FROM '${csv_file}'
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


-- =====================================================
-- SEED ADMIN USER
-- =====================================================
-- Insert admin user
-- Password: P@ssw0rd123 (BCrypt hashed with strength 10)
INSERT INTO users (role_id, email, password_hash, verified, created_at, updated_at)
VALUES (
    1,
    'admin@example.com',
    '$2a$10$MfHVIpwEL/c9T/Vgho3S1OPX6iGWNU4T9TdK8J6feqLQ6LOhQ6olu',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- Insert admin user information
INSERT INTO user_information (
    user_id,
    first_name,
    last_name,
    city_code,
    sub_district_code,
    address_detail,
    school,
    grade,
    favorite_subjects,
    bio,
    date_of_birth,
    created_at,
    updated_at
)
SELECT
    u.id,
    'Admin',
    'Nguyen',
    '11',
    '9995',
    'Homeless',
    'University of Engineering and Technology, VNU',
    'Unemployed',
    ARRAY['Math', 'Physics', 'English'],
    'Da Admin',
    '2004-11-29'::date,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'admin@example.com'
ON CONFLICT (user_id) DO NOTHING;


