CREATE TABLE enrollment (
    id SERIAL PRIMARY KEY, 
    code VARCHAR(50) NOT NULL , 
    name VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
);

-- Add comments for the table and its columns
COMMENT ON TABLE enrollment IS 'Stores information about enrollment records.';
COMMENT ON COLUMN enrollment.id IS 'Unique identifier for the enrollment record.';
COMMENT ON COLUMN enrollment.code IS 'Unique code assigned to the enrollment.';
COMMENT ON COLUMN enrollment.name IS 'The name or description of the enrollment.';
COMMENT ON COLUMN enrollment.creation_date IS 'Timestamp indicating when the record was created.';
COMMENT ON COLUMN enrollment.update_date IS 'Timestamp indicating when the record was last updated.';

CREATE TABLE regional (
    id SERIAL PRIMARY KEY, 
    name VARCHAR(100) NOT NULL, 
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP  
);

-- Add comments for the table and its columns
COMMENT ON TABLE regional IS 'Stores information about different regional entities.';
COMMENT ON COLUMN regional.id IS 'Unique identifier for the regional entity.';
COMMENT ON COLUMN regional.name IS 'The name of the regional entity.';
COMMENT ON COLUMN regional.creation_date IS 'Timestamp indicating when the record was created.';
COMMENT ON COLUMN regional.update_date IS 'Timestamp indicating when the record was last updated.';

CREATE TABLE MODALITY (
    id SERIAL PRIMARY KEY,  
    name VARCHAR(255) NOT NULL, 
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP  
);

-- Adding comments to describe the table and its columns
COMMENT ON TABLE MODALITY IS 'Table that stores different modalities for courses or programs.';
COMMENT ON COLUMN MODALITY.id IS 'Primary key, unique identifier for each modality.';
COMMENT ON COLUMN MODALITY.name IS 'Name of the modality.';
COMMENT ON COLUMN MODALITY.creation_date IS 'Date when the modality record was created.';
COMMENT ON COLUMN MODALITY.update_date IS 'Date when the modality record was last updated.';CREATE TABLE month (
    id SERIAL PRIMARY KEY, 
    name VARCHAR(50) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE course (
    id SERIAL PRIMARY KEY, 
    id_modality INT NOT NULL,
    name VARCHAR(255) NOT NULL, 
    start_date_training TIMESTAMP NOT NULL, 
    end_date_training TIMESTAMP NOT NULL, 
    value DECIMAL(10,2) NOT NULL, 
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP , 
	
    CONSTRAINT fk_course_modality FOREIGN KEY (id_modality) REFERENCES modality(id)
);

-- Add comments for the table and its columns
COMMENT ON TABLE course IS 'Stores information about courses and their details.';
COMMENT ON COLUMN course.id IS 'Unique identifier for the course.';
COMMENT ON COLUMN course.id_modality IS 'Reference to the modality associated with the course.';
COMMENT ON COLUMN course.name IS 'The name of the course.';
COMMENT ON COLUMN course.start_date_training IS 'The date when the course training starts.';
COMMENT ON COLUMN course.end_date_training IS 'The date when the course training ends.';
COMMENT ON COLUMN course.value IS 'The cost or value of the course.';
COMMENT ON COLUMN course.creation_date IS 'Timestamp indicating when the record was created.';
COMMENT ON COLUMN course.update_date IS 'Timestamp indicating when the record was last updated.';

CREATE TABLE project (
    id SERIAL PRIMARY KEY, 
    id_course INT NOT NULL, 
    name VARCHAR(100) NOT NULL, 
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
	
    CONSTRAINT fk_project_course FOREIGN KEY (id_course) REFERENCES course(id) ON DELETE CASCADE
);

-- Add comments for the table and its columns
COMMENT ON TABLE project IS 'Stores information about projects.';
COMMENT ON COLUMN project.id IS 'Unique identifier for the project.';
COMMENT ON COLUMN project.id_course IS 'Foreign key referencing the course associated with the project.';
COMMENT ON COLUMN project.name IS 'The name of the project.';
COMMENT ON COLUMN project.creation_date IS 'Timestamp indicating when the record was created.';
COMMENT ON COLUMN project.update_date IS 'Timestamp indicating when the record was last updated.';


CREATE TABLE users (
    id SERIAL PRIMARY KEY, 
    id_course INT NOT NULL, 
    id_project INT NOT NULL, 
    id_regional INT NOT NULL, 
    id_enrollment INT NOT NULL, 
    identification_number VARCHAR(50) NOT NULL , 
    name VARCHAR(100) NOT NULL, 
    email VARCHAR(150) NOT NULL,
    status VARCHAR(50) ,
    course_fee DECIMAL(10,2), 
    description VARCHAR(150),   
    cellphone VARCHAR(20), 
    advance_course DECIMAL(5,2) CHECK (advance_course BETWEEN 0 AND 100), 
    training_date DATE, 
    charge_date DATE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP , 
	
    CONSTRAINT fk_user_course FOREIGN KEY (id_course) REFERENCES course(id),
    CONSTRAINT fk_user_project FOREIGN KEY (id_project) REFERENCES project(id),
    CONSTRAINT fk_user_regional FOREIGN KEY (id_regional) REFERENCES regional(id),
    CONSTRAINT fk_user_enrollment FOREIGN KEY (id_enrollment) REFERENCES enrollment(id)
);

-- Add comments for the table and its columns
COMMENT ON TABLE users IS 'Stores information about users, including their assigned course, project, and regional details.';
COMMENT ON COLUMN users.id IS 'Unique identifier for the user.';
COMMENT ON COLUMN users.id_course IS 'Foreign key referencing the course the user is enrolled in.';
COMMENT ON COLUMN users.id_project IS 'Foreign key referencing the project associated with the user.';
COMMENT ON COLUMN users.id_regional IS 'Foreign key referencing the regional assignment.';
COMMENT ON COLUMN users.id_enrollment IS 'Foreign key referencing the enrollment type.';
COMMENT ON COLUMN users.identification_number IS 'Unique identification number for the user.';
COMMENT ON COLUMN users.name IS 'Full name of the user.';
COMMENT ON COLUMN users.email IS 'User’s email address (must be unique).';
COMMENT ON COLUMN users.cellphone IS 'User’s cellphone number.';
COMMENT ON COLUMN users.advance_course IS 'Percentage of course progress (0 to 100).';
COMMENT ON COLUMN users.training_date IS 'Date when the user started training.';
COMMENT ON COLUMN users.charge_date IS 'Date when the user was assigned a charge.';
COMMENT ON COLUMN users.creation_date IS 'Timestamp indicating when the record was created.';
COMMENT ON COLUMN users.update_date IS 'Timestamp indicating when the record was last updated.';
COMMENT ON COLUMN users.status IS 'Current status of the training rate. ';
COMMENT ON COLUMN users.course_fee IS 'Monetary value representing the course fee.';
COMMENT ON COLUMN users.description IS 'Brief description or additional details about the training rate.';

CREATE TABLE billing_accounts (
    billing_account_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    billing_account_type VARCHAR(50) NOT NULL,
    billing_account_name VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    document_word_url TEXT,
    document_excel_url TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE billing_accounts IS 'Stores billing account details associated with a user.';
COMMENT ON COLUMN billing_accounts.billing_account_id IS 'Unique identifier for each billing account.';
COMMENT ON COLUMN billing_accounts.user_id IS 'Reference to the associated user.';
COMMENT ON COLUMN billing_accounts.billing_account_type IS 'Type of billing account.';
COMMENT ON COLUMN billing_accounts.billing_account_name IS 'Name of the billing account.';
COMMENT ON COLUMN billing_accounts.creation_date IS 'Timestamp when the billing account was created.';
COMMENT ON COLUMN billing_accounts.document_word_url IS 'URL to download the billing account document in Word format.';
COMMENT ON COLUMN billing_accounts.document_excel_url IS 'URL to download the billing account document in Excel format.';

CREATE TABLE billing_approvals (
    approval_id SERIAL PRIMARY KEY,
    billing_account_id INT NOT NULL,
    approval_date TIMESTAMP NOT NULL,
    email_sent_date TIMESTAMP,
    FOREIGN KEY (billing_account_id) REFERENCES billing_accounts(billing_account_id) ON DELETE CASCADE
);


COMMENT ON TABLE billing_approvals IS 'Tracks approvals for billing accounts, including email notifications.';
COMMENT ON COLUMN billing_approvals.approval_id IS 'Unique identifier for each billing approval.';
COMMENT ON COLUMN billing_approvals.billing_account_id IS 'Reference to the associated billing account.';
COMMENT ON COLUMN billing_approvals.approval_date IS 'Date when the billing account was approved.';
COMMENT ON COLUMN billing_approvals.email_sent_date IS 'Date when the approval email was sent.';





-- Add comments for the table and its columns
COMMENT ON TABLE month IS 'Stores information about months.';
COMMENT ON COLUMN month.id IS 'Unique identifier for the month.';
COMMENT ON COLUMN month.name IS 'The name of the month (e.g., January, February).';
COMMENT ON COLUMN month.creation_date IS 'Timestamp indicating when the record was created.';
COMMENT ON COLUMN month.update_date IS 'Timestamp indicating when the record was last updated.';



CREATE TABLE training_rates (
    id SERIAL PRIMARY KEY, 
    training_line VARCHAR(255) NOT NULL, 
    training_process VARCHAR(255) NOT NULL, 
    hours INT NOT NULL, 
    certifier VARCHAR(255), 
    population VARCHAR(255),
    min_quota INT NOT NULL, 
    max_quota INT NOT NULL, 
    modality VARCHAR(100) NOT NULL, 
    opening_frequency VARCHAR(100), 
    region VARCHAR(255) NOT NULL, 
    observation TEXT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP , 
    tariff_2025 DECIMAL(10,2) NOT NULL 
);

-- Table comment
COMMENT ON TABLE training_rates IS 'This table stores information about training tariffs, including details on courses, quotas, modalities, and regional availability.';

-- Column comments
COMMENT ON COLUMN training_rates.id IS 'Unique identifier for each training rate.';
COMMENT ON COLUMN training_rates.training_line IS 'Training line or category defining the type of course.';
COMMENT ON COLUMN training_rates.training_process IS 'Specific training process related to the course.';
COMMENT ON COLUMN training_rates.hours IS 'Total number of hours required for the training.';
COMMENT ON COLUMN training_rates.certifier IS 'Name of the entity responsible for certification.';
COMMENT ON COLUMN training_rates.population IS 'Target audience or population for the training.';
COMMENT ON COLUMN training_rates.min_quota IS 'Minimum required number of participants for the course to open.';
COMMENT ON COLUMN training_rates.max_quota IS 'Maximum number of participants allowed in the course.';
COMMENT ON COLUMN training_rates.modality IS 'Mode of training delivery (e.g., in-person, virtual).';
COMMENT ON COLUMN training_rates.opening_frequency IS 'How often the course is available for new participants.';
COMMENT ON COLUMN training_rates.region IS 'Geographical region where the training is offered.';
COMMENT ON COLUMN training_rates.observation IS 'Additional comments or special considerations.';
COMMENT ON COLUMN training_rates.tariff_2025 IS 'Training fee applicable for the year 2025.';
COMMENT ON COLUMN training_rates.creation_date IS 'Timestamp indicating when the record was created.';
COMMENT ON COLUMN training_rates.update_date IS 'Timestamp indicating when the record was last updated.';

