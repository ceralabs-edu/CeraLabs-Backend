-- Trigger: update users.updated_at when user_information is updated
-- Assumes PostgreSQL syntax

CREATE OR REPLACE FUNCTION update_user_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE users SET updated_at = NOW() WHERE id = NEW.user_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_user_info_update ON user_information;

CREATE TRIGGER trg_user_info_update
AFTER UPDATE ON user_information
FOR EACH ROW
EXECUTE FUNCTION update_user_updated_at();

