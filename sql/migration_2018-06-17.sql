---- CREATING TABLES ----

CREATE TABLE public.metrics (
    id INTEGER PRIMARY KEY,
    host_id INTEGER,
    created_at TIMESTAMP,
    cpu_la1 FLOAT,
    cpu_la5 FLOAT,
    cpu_la15 FLOAT,
    mem_used INTEGER,
    mem_free INTEGER,
    disk_used INTEGER,
    disk_free INTEGER
);

CREATE TABLE public.hosts (
    id INTEGER PRIMARY KEY,
    cpu_nproc INTEGER,
    mem_total INTEGER,
    disk_total INTEGER
);

---- CREATING ROLE AND GRANTING RIGHTS ----

CREATE ROLE hostInfoChecker WITH PASSWORD 'asdQWE123';

GRANT SELECT, UPDATE, INSERT ON metrics TO hostInfoChecker;

GRANT SELECT, UPDATE, INSERT ON hosts TO hostInfoChecker;

ALTER ROLE hostInfoChecker WITH LOGIN;

ALTER ROLE hostInfoChecker WITH PASSWORD 'asdQWE123';

