-- Table: public.role

-- DROP TABLE public.role;

CREATE TABLE public.role
(
    role_id integer NOT NULL,
    role character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT pk_role PRIMARY KEY (role_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.role
    OWNER to postgres;