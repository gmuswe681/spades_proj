-- Table: public.moves

-- DROP TABLE public.moves;

CREATE TABLE public.moves
(
    move_id integer NOT NULL,
    game_id integer NOT NULL,
    user_id integer NOT NULL,
    round_id integer NOT NULL,
    card_played character(3) NOT NULL,
    CONSTRAINT move_pk PRIMARY KEY (move_id, game_id, round_id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.moves
    OWNER to postgres;