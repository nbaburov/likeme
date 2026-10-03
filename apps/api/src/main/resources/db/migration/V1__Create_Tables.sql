-- Create admins table (no dependencies)
CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    permissions VARCHAR(20) NOT NULL,
    profile_photo_path VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_on TIMESTAMP
);

-- Create platform_settings table
CREATE TABLE platform_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    commission_percentage DOUBLE NOT NULL,
    updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT,
    FOREIGN KEY (updated_by) REFERENCES admins(id) ON DELETE SET NULL
);

-- Create influencer_applications table (no dependencies)
CREATE TABLE influencer_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    about TEXT,
    instagram_handle VARCHAR(255) UNIQUE,
    profile_photo_path VARCHAR(255),
    cover_photo_path VARCHAR(255),
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    billing_first_name VARCHAR(50) NOT NULL,
    billing_last_name VARCHAR(50) NOT NULL,
    billing_country VARCHAR(50) NOT NULL,
    billing_street_address VARCHAR(255) NOT NULL,
    billing_city VARCHAR(50) NOT NULL,
    billing_state VARCHAR(50) NOT NULL,
    billing_zip_code VARCHAR(20) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create influencers table
CREATE TABLE influencers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL UNIQUE,
    password VARCHAR(255),
    salt VARCHAR(255),
    is_instagram_connected BOOLEAN NOT NULL DEFAULT FALSE,
    instagram_access_token VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_on TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES influencer_applications(id)
);

-- Create setup_tokens table last (depends on influencer_applications)
CREATE TABLE setup_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES influencer_applications(id)
);