-- Table: public.rounds

-- DROP TABLE public.rounds;

CREATE TABLE public.rounds
(
    game_id integer NOT NULL,
    round_number integer NOT NULL,
    player1_id integer NOT NULL,
    player1_bid integer,
    player1_actual integer,
    player2_id integer NOT NULL,
    player2_bid integer,
    player2_actual integer,
    CONSTRAINT pk_rounds PRIMARY KEY (game_id, round_number),
    CONSTRAINT uk_rounds UNIQUE (game_id, round_number)
,
    CONSTRAINT fk_games FOREIGN KEY (game_id)
        REFERENCES public.games (game_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.rounds
    OWNER to postgres;