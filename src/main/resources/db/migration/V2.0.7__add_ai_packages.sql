-- Add Pro AI Package and Starter AI Package
INSERT INTO ai_packages (
    name,
    model,
    description,
    api_keys,
    total_token,
    price,
    token_rate_limit,
    duration_days,
    created_at,
    updated_at
) VALUES
(
    'Pro AI Package',
    'gemini-2.5-pro',
    'Gói AI nâng cao.',
    ARRAY['${api_key}'],
    2000000,
    19.99,
    50000,
    90,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'Starter AI Package',
    'gemini-2.5-flash',
    'Gói AI cơ bản dành cho sinh viên và người mới bắt đầu, phù hợp cho các tác vụ hỏi đáp và phân tích đơn giản.',
    ARRAY['${api_key}'],
    500000,
    3.60,
    10000,
    30,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
