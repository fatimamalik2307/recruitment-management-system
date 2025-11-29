-- Table for shortlisting criteria per job
CREATE TABLE public.shortlisting_criteria (
    id SERIAL PRIMARY KEY,
    job_id INTEGER NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    min_experience INTEGER,
    required_qualification TEXT,
    required_skills TEXT,
    optional_location VARCHAR(150),
    optional_grade VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Optional: table linking shortlisted applications (if you want to mark which applications were shortlisted)
CREATE TABLE public.shortlist (
    id SERIAL PRIMARY KEY,
    criteria_id INTEGER NOT NULL REFERENCES shortlisting_criteria(id) ON DELETE CASCADE,
    application_id INTEGER NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    shortlisted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
