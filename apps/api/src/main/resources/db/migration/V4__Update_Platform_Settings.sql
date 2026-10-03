ALTER TABLE platform_settings 
DROP COLUMN commission_percentage,
ADD COLUMN type VARCHAR(50) NOT NULL,
ADD COLUMN value VARCHAR(255) NOT NULL,
ADD CONSTRAINT uk_platform_settings_type UNIQUE (type); 