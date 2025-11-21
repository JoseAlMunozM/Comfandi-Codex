ALTER TABLE portfolio ADD COLUMN down_payment_3 NUMERIC(12,2);

ALTER TABLE billing_account_amortization ADD COLUMN course_id INTEGER NOT NULL;
ALTER TABLE billing_account_amortization ADD COLUMN portfolio_id INTEGER NOT NULL;


ALTER TABLE billing_accounts DROP CONSTRAINT fk_billing_account_portfolio;

ALTER TABLE billing_accounts DROP COLUMN portfolio_id;

ALTER TABLE course ADD COLUMN id_portfolio INTEGER;

ALTER TABLE course ADD CONSTRAINT fk_course_portfolio FOREIGN KEY (id_portfolio) REFERENCES portfolio(id);