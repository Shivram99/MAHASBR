CREATE SEQUENCE csv_upload_job_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE csv_upload_failed_record_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE csv_upload_success_record_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE csv_upload_job (
    id NUMBER(19, 0) PRIMARY KEY,
    job_id VARCHAR2(64 CHAR) NOT NULL UNIQUE,
    file_name VARCHAR2(255 CHAR) NOT NULL,
    stored_file_name VARCHAR2(255 CHAR) NOT NULL,
    file_size NUMBER(19, 0) DEFAULT 0 NOT NULL,
    total_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    processed_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    success_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    failed_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    pending_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    progress_percentage NUMBER(3, 0) DEFAULT 0 NOT NULL,
    valid_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    invalid_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    duplicate_records NUMBER(10, 0) DEFAULT 0 NOT NULL,
    status VARCHAR2(20 CHAR) NOT NULL,
    preview_ready NUMBER(1, 0) DEFAULT 0 NOT NULL,
    pause_requested NUMBER(1, 0) DEFAULT 0 NOT NULL,
    cancel_requested NUMBER(1, 0) DEFAULT 0 NOT NULL,
    last_processed_valid_record NUMBER(10, 0) DEFAULT 0 NOT NULL,
    preview_page_size NUMBER(10, 0) DEFAULT 100 NOT NULL,
    result_page_size NUMBER(10, 0) DEFAULT 100 NOT NULL,
    total_preview_pages NUMBER(10, 0) DEFAULT 0 NOT NULL,
    total_success_pages NUMBER(10, 0) DEFAULT 0 NOT NULL,
    total_failed_pages NUMBER(10, 0) DEFAULT 0 NOT NULL,
    message VARCHAR2(1000 CHAR),
    started_at TIMESTAMP(6),
    completed_at TIMESTAMP(6),
    paused_at TIMESTAMP(6),
    resumed_at TIMESTAMP(6),
    created_by VARCHAR2(100 CHAR),
    created_date TIMESTAMP(6) NOT NULL,
    updated_date TIMESTAMP(6) NOT NULL
);

CREATE TABLE csv_upload_failed_record (
    id NUMBER(19, 0) PRIMARY KEY,
    job_id VARCHAR2(64 CHAR) NOT NULL,
    row_number NUMBER(10, 0) NOT NULL,
    establishment_name VARCHAR2(500 CHAR),
    brn VARCHAR2(50 CHAR),
    error_message VARCHAR2(2000 CHAR),
    raw_data CLOB,
    created_date TIMESTAMP(6) NOT NULL
);

CREATE TABLE csv_upload_success_record (
    id NUMBER(19, 0) PRIMARY KEY,
    job_id VARCHAR2(64 CHAR) NOT NULL,
    row_number NUMBER(10, 0) NOT NULL,
    brn VARCHAR2(50 CHAR) NOT NULL,
    establishment_name VARCHAR2(500 CHAR),
    raw_data CLOB,
    created_date TIMESTAMP(6) NOT NULL
);

CREATE INDEX idx_csv_upload_job_job_id ON csv_upload_job(job_id);
CREATE INDEX idx_csv_upload_job_status ON csv_upload_job(status);
CREATE INDEX idx_csv_upload_failed_job_id ON csv_upload_failed_record(job_id);
CREATE INDEX idx_csv_upload_failed_brn ON csv_upload_failed_record(brn);
CREATE INDEX idx_csv_upload_success_job_id ON csv_upload_success_record(job_id);
CREATE INDEX idx_csv_upload_success_brn ON csv_upload_success_record(brn);
