-- Reference schema for analytics-service.
CREATE TABLE IF NOT EXISTS analytics_summaries (
  summary_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  snapshot_date DATE NOT NULL,
  total_income DECIMAL(14,2) NOT NULL DEFAULT 0,
  total_expense DECIMAL(14,2) NOT NULL DEFAULT 0,
  net_savings DECIMAL(14,2) NOT NULL DEFAULT 0,
  savings_rate DECIMAL(7,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
