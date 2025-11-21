CREATE TABLE IF NOT EXISTS provider
(
    id numeric(20,0) NOT NULL,
    name character varying(100),
    creation_date timestamp without time zone,
    update_date timestamp without time zone,
    CONSTRAINT provider_pkey PRIMARY KEY (id)
);

INSERT INTO provider VALUES (1, 'Desarollo empresarial', '2025-06-13 17:45:17.153833', NULL);
INSERT INTO provider VALUES (2, 'Educacion', '2025-06-13 17:45:56.489207', NULL);
INSERT INTO provider VALUES (3, 'Fomento - TH Fosfec', '2025-07-17 17:43:04.569412', NULL);



CREATE TABLE IF NOT EXISTS typification (
    id numeric(5,0) NOT NULL,
    location text,
    cebe text,
    account text,
    assignment text,
    type text
);


INSERT INTO typification VALUES (1, 'BUENAVENTURA', 'F050011900', '2705950132', 'ACTIVOS - EMPRENDI', 'activos-empresarial');
INSERT INTO typification VALUES (2, 'BUGA', 'F050011900', '2705950132', 'ACTIVOS - EMPRENDI', 'activos-empresarial');
INSERT INTO typification VALUES (3, 'CARTAGO', 'F050011900', '2705950132', 'ACTIVOS - EMPRENDI', 'activos-empresarial');
INSERT INTO typification VALUES (4, 'PALMIRA', 'F050011900', '2705950132', 'ACTIVOS - EMPRENDI', 'activos-empresarial');
INSERT INTO typification VALUES (5, 'TULUA', 'F050011900', '2705950132', 'ACTIVOS - EMPRENDI', 'activos-empresarial');
INSERT INTO typification VALUES (6, 'CALI', 'F050011900', '2705950133', 'ACTIVOS - EMPRENDI', 'activos-empresarial');
INSERT INTO typification VALUES (7, 'CALI', 'F050011900', '2705950133', 'CESANTES- EMPLEABILIDAD', 'cesantes-empresarial');
INSERT INTO typification VALUES (8, 'BUENAVENTURA', 'F050011900', '2705950132', 'CESANTES- EMPLEABILIDAD', 'cesantes-empresarial');
INSERT INTO typification VALUES (9, 'CALI', 'F010090900', '4165951003', 'FOMENTO-TH-FOSFEC', 'fomento-th-fosfec');



CREATE SEQUENCE modality_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE IF NOT EXISTS modality (
    id integer PRIMARY KEY DEFAULT nextval('modality_id_seq'::regclass),
    name character varying(255) NOT NULL,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO modality VALUES (1, 'VIRTUAL', '2025-04-14 23:40:07.530873', '2025-04-14 23:40:07.530873');
INSERT INTO modality VALUES (2, 'PRESENCIAL', '2025-06-12 18:46:14.238075', '2025-06-12 18:46:14.238075');
INSERT INTO modality VALUES (3, 'HIBRIDO', '2025-06-12 18:46:31.365357', '2025-06-12 18:46:31.365357');
INSERT INTO modality VALUES (4, 'BOOTCAMP', '2025-06-13 16:59:12.06844', '2025-06-13 16:59:12.06844');

CREATE TABLE IF NOT EXISTS countable_data (
    id numeric(20,0) NOT NULL,
    regional character varying(20),
    profile character varying(20),
    "group" numeric(1,0),
    internal_group numeric(1,0),
    account character varying(20),
    beneficiary character varying(20),
    is_cebe boolean,
    is_account boolean,
    countable_key numeric(3,0)
);


INSERT INTO countable_data VALUES (1, 'CALI', 'activos-empresarial', 1, 1, NULL, NULL, true, true, 50);
INSERT INTO countable_data VALUES (7, 'CALI', 'cesantes-empresarial', 1, 1, NULL, NULL, true, true, 50);
INSERT INTO countable_data VALUES (2, 'CALI', 'activos-empresarial', 1, 2, '1810355003', 'A010010100', false, false, 40);
INSERT INTO countable_data VALUES (3, 'CALI', 'activos-empresarial', 2, 1, '2858290105', 'W010010100', false, false, 40);
INSERT INTO countable_data VALUES (4, 'CALI', 'activos-empresarial', 2, 2, '2868290101', 'W010010100', false, false, 50);
INSERT INTO countable_data VALUES (5, 'CALI', 'activos-empresarial', 3, 1, '2868290102', 'W010010100', false, false, 40);
INSERT INTO countable_data VALUES (6, 'CALI', 'activos-empresarial', 3, 2, '1810355003', 'A010010100', false, false, 50);
INSERT INTO countable_data VALUES (8, 'CALI', 'cesantes-empresarial', 1, 2, '1810355003', 'A010010100', false, false, 40);
INSERT INTO countable_data VALUES (9, 'CALI', 'cesantes-empresarial', 2, 1, '2858290106', 'W010010100', false, false, 40);
INSERT INTO countable_data VALUES (10, 'CALI', 'cesantes-empresarial', 2, 2, '2868290101', 'W010010100', false, false, 50);
INSERT INTO countable_data VALUES (12, 'CALI', 'cesantes-empresarial', 3, 2, '1810355003', 'A010010100', false, false, 50);
INSERT INTO countable_data VALUES (11, 'CALI', 'cesantes-empresarial', 3, 1, '2868290102', 'W010010100', false, false, 40);
INSERT INTO countable_data VALUES (13, 'CALI', 'fomento-th-fosfec', 1, 1, NULL, NULL, true, true, 50);
INSERT INTO countable_data VALUES (14, 'CALI', 'fomento-th-fosfec', 1, 2, '5110350102', '', false, false, 40);


CREATE SEQUENCE regional_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS regional (
    id integer PRIMARY KEY DEFAULT nextval('regional_id_seq'::regclass),
    name character varying(100) NOT NULL,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO regional VALUES (1, 'Compromiso Valle', '2025-04-14 23:45:40.306369', '2025-04-14 23:45:40.306369');
INSERT INTO regional VALUES (2, 'MiPymes', '2025-06-12 21:47:11.140306', '2025-06-12 21:47:11.140306');
INSERT INTO regional VALUES (3, 'Trabajadores activos', '2025-06-12 21:47:11.140306', '2025-06-12 21:47:11.140306');
INSERT INTO regional VALUES (4, 'Fosfec', '2025-06-12 21:47:11.140306', '2025-06-12 21:47:11.140306');
INSERT INTO regional VALUES (5, 'Beneficiarios Ley 2069', '2025-06-12 21:47:11.140306', '2025-06-12 21:47:11.140306');
INSERT INTO regional VALUES (6, 'Corporativo Empresas', '2025-06-12 21:50:13.99128', '2025-06-12 21:50:13.99128');


CREATE SEQUENCE billing_accounts_billing_account_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE IF NOT EXISTS billing_accounts (
    billing_account_id integer PRIMARY KEY DEFAULT nextval('billing_accounts_billing_account_id_seq'::regclass),
    billing_account_type character varying(50) NOT NULL,
    billing_account_name character varying(100) NOT NULL,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    document_word_url text,
    document_excel_url text,
    law text
);


CREATE SEQUENCE billing_approvals_approval_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS billing_approvals (
    approval_id integer  PRIMARY KEY DEFAULT nextval('billing_approvals_approval_id_seq'::regclass),
    billing_account_id integer NOT NULL,
    approval_date timestamp without time zone NOT NULL,
    email_sent_date timestamp without time zone,
    identification_number numeric,
    user_type text,
    approval text,
    observation text
);

CREATE SEQUENCE course_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS course (
    id integer PRIMARY KEY DEFAULT nextval('course_id_seq'::regclass),
    id_modality integer NOT NULL,
    name character varying(255) NOT NULL,
    start_date_training timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    end_date_training timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    value numeric(10,2) NOT NULL,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    id_provider numeric(20,0),
	CONSTRAINT fk_course_modality FOREIGN KEY (id_modality) REFERENCES modality(id),
	CONSTRAINT fk_course_provider FOREIGN KEY (id_provider) REFERENCES provider(id)
);

CREATE SEQUENCE users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS users (
    id integer PRIMARY KEY DEFAULT nextval('users_id_seq'::regclass),
    id_course integer NOT NULL,
    id_regional integer NOT NULL,
    identification_number character varying(50) NOT NULL,
    name character varying(100) NOT NULL,
    email character varying(150) NOT NULL,
    status character varying(50),
    course_fee numeric(10,2),
    description character varying(150),
    cellphone character varying(20),
    advance_course numeric(5,2),
    training_date date,
    charge_date date,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    account_id numeric(20,0),
    identification_type character varying(20),
    training_end_date date,
    CONSTRAINT users_advance_course_check CHECK (((advance_course >= (0)::numeric) AND (advance_course <= (100)::numeric))),
	CONSTRAINT fk_user_course FOREIGN KEY (id_course) REFERENCES course(id),
	CONSTRAINT fk_user_regional FOREIGN KEY (id_regional) REFERENCES regional(id)
);


CREATE SEQUENCE workshop_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE IF NOT EXISTS workshop (
    id integer PRIMARY KEY DEFAULT nextval('workshop_id_seq'::regclass),
    name character varying(100) NOT NULL,
    value numeric(12,2) NOT NULL,
    year integer NOT NULL
);

CREATE SEQUENCE workshop_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE IF NOT EXISTS workshop_users (
    id integer PRIMARY KEY DEFAULT nextval('workshop_users_id_seq'::regclass),
    id_workshop integer NOT NULL,
    id_modality integer NOT NULL,
    id_regional integer NOT NULL,
    city character varying(100) NOT NULL,
    area character varying(50) NOT NULL,
    identification_number character varying(20) NOT NULL,
    identification_type character varying(20) NOT NULL,
    full_name character varying(100) NOT NULL,
    email character varying(50) NOT NULL,
	phone character varying(20),
    fee_value numeric(10,2),
    orientation_date date,
    appointment_date date NOT NULL,
    validation_date date,
    appointment boolean,
    status character varying(20),
    description character varying(200),
    year integer NOT NULL,
    progress numeric(4,1),
    id_account numeric(20,0),
	CONSTRAINT fk_workshop_user_regional FOREIGN KEY (id_regional) REFERENCES regional(id),
	CONSTRAINT fk_workshop_user_modality FOREIGN KEY (id_modality) REFERENCES modality(id),
	CONSTRAINT fk_workshop_users_workshop FOREIGN KEY (id_workshop) REFERENCES workshop(id) ON DELETE CASCADE
);

CREATE TABLE user_validation_history (
    id text PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    identification_number text NOT NULL,
    identification_type text NOT NULL,
    validation_pass boolean NOT NULL,
    response_data json NOT NULL,
    law text,
    business_name text,
    business_identification text,
    state boolean
);

CREATE SEQUENCE unavailable_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE unavailable_users (
    id integer PRIMARY KEY DEFAULT nextval('unavailable_users_id_seq'::regclass) ,
	identification_number character varying(20),
    identification_type character varying(5) NOT NULL,
    name character varying(100) NOT NULL,
    unavailable_date timestamp without time zone,
    description character varying(100) NOT NULL

);