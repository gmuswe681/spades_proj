-- Table: public.moves

-- DROP TABLE public.moves;

CREATE TABLE public.moves
(
    game_id integer NOT NULL,
    round_number integer NOT NULL,
    move_number integer NOT NULL,
    CONSTRAINT pk_moves PRIMARY KEY (game_id, round_number, move_number),
    CONSTRAINT fk_moves FOREIGN KEY (game_id, round_number)
        REFERENCES public.rounds (game_id, round_number) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.moves
    OWNER to postgres;