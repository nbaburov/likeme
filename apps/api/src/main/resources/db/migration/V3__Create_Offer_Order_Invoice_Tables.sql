-- Create offers table
CREATE TABLE offers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    cover_photo_path VARCHAR(255),
    type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by_id BIGINT NOT NULL,
    updated_by_id BIGINT,
    FOREIGN KEY (created_by_id) REFERENCES influencers(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by_id) REFERENCES influencers(id) ON DELETE SET NULL
);

-- Create invoices table
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

-- Create orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offer_id BIGINT NOT NULL,
    post_id VARCHAR(255),
    comment TEXT,
    invoice_id BIGINT NOT NULL ,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ordered_by_id BIGINT NOT NULL,
    updated_by_id BIGINT,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY (ordered_by_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by_id) REFERENCES influencers(id) ON DELETE SET NULL
);
