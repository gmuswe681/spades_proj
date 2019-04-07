-- Table: public.user_role

-- DROP TABLE public.user_role;

CREATE TABLE public.user_role
(
    user_id integer NOT NULL,
    role_id integer NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT uk_it77eq964jhfqtu54081ebtio UNIQUE (role_id)
,
    CONSTRAINT fk_role FOREIGN KEY (role_id)
        REFERENCES public.role (role_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES public."user" (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.user_role
    OWNER to postgres;