-- Reference schema for recurring-service.
CREATE TABLE IF NOT EXISTS recurring_rules (
  recurring_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  category_id BIGINT,
  title VARCHAR(255) NOT NULL,
  amount DECIMAL(14,2) NOT NULL,
  type VARCHAR(30) NOT NULL,
  frequency VARCHAR(30) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  next_due_date DATE,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
