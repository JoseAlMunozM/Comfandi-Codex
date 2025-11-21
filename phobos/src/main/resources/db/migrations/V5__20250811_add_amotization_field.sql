ALTER TABLE billing_accounts ADD COLUMN amortizable BOOLEAN default false;
ALTER TABLE billing_accounts ADD COLUMN complete BOOLEAN default false;