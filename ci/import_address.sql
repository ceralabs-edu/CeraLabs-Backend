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

COPY raw_address
FROM '/docker-entrypoint-initdb.d/data.csv'
DELIMITER ','
CSV HEADER;

INSERT INTO provinces (code, name, slug, type, full_name)
SELECT DISTINCT
    parent_code,
    ten_tinh,
    ten_tinh_khong_dau,
    loai_tinh,
    full_name_tinh
FROM raw_address
WHERE parent_code IS NOT NULL AND parent_code <> ''
ON CONFLICT (code) DO NOTHING;

INSERT INTO communes (code, name, slug, type, full_name, path, path_with_type, province_code)
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
WHERE code IS NOT NULL AND code <> ''
ON CONFLICT (code) DO NOTHING;