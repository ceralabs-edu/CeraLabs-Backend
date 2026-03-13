-- Add ON DELETE CASCADE to user_ai_instance_usages foreign key constraint
ALTER TABLE user_ai_instance_usages
DROP CONSTRAINT IF EXISTS fk_user_ai_instance_usage_ai_package_instance;
ALTER TABLE user_ai_instance_usages
ADD CONSTRAINT fk_user_ai_instance_usage_ai_package_instance
FOREIGN KEY (instance_id)
REFERENCES ai_packages_instances(id)
ON DELETE CASCADE;
-- End of migration
