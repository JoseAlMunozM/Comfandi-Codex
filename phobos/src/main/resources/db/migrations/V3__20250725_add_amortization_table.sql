CREATE SEQUENCE billing_account_amortization_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE billing_account_amortization (
    id integer PRIMARY KEY DEFAULT nextval('billing_account_amortization_id_seq'::regclass),
    billing_account_id integer NOT NULL,
    amortization_date DATE,
    amortization_number integer,
    year integer,
    month integer,
    percent numeric(4,1),
    value numeric(10,2),
    CONSTRAINT fk_amortization_account FOREIGN KEY (billing_account_id) REFERENCES billing_accounts(billing_account_id)
);

CREATE SEQUENCE portfolio_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE portfolio (
    id integer PRIMARY KEY DEFAULT nextval('portfolio_id_seq'::regclass),
    name VARCHAR(200) NOT NULL,
    modality_id integer NOT NULL,
    value_fee NUMERIC(12,2) NOT NULL,
    account VARCHAR(20) NOT NULL,
    down_payment_1 NUMERIC(12,2),
    down_payment_2 NUMERIC(12,2),
    CONSTRAINT fk_portfolio_modality FOREIGN KEY (modality_id) REFERENCES modality(id)
);

ALTER TABLE billing_accounts ADD COLUMN portfolio_id integer;

ALTER TABLE billing_accounts ADD CONSTRAINT fk_billing_account_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolio(id);