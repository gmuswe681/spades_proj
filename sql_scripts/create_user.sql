-- Table: public."user"

-- DROP TABLE public."user";

CREATE TABLE public."user"
(
    user_id integer NOT NULL,
    active integer,
    email character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT pk_user PRIMARY KEY (user_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public."user"
    OWNER to postgres;