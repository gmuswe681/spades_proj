-- Table: public.games

-- DROP TABLE public.games;

CREATE TABLE public.games
(
    game_id integer NOT NULL,
    player1_id integer,
    player2_id integer,
    game_status character(1) COLLATE pg_catalog."default" NOT NULL DEFAULT 'o'::bpchar,
    points_to_win integer NOT NULL,
    winner_id integer,
    CONSTRAINT pk_games PRIMARY KEY (game_id),
    CONSTRAINT fk_games_player1_to_user1 FOREIGN KEY (player1_id)
        REFERENCES public."user" (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_games_player2_to_user2 FOREIGN KEY (player2_id)
        REFERENCES public."user" (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT game_status_valid CHECK (game_status = 'o'::bpchar OR game_status = 'a'::bpchar OR game_status = 'e'::bpchar) NOT VALID,
    CONSTRAINT winner_valid CHECK (winner_id = player1_id OR winner_id = player2_id OR winner_id IS NULL) NOT VALID,
    CONSTRAINT points_to_win_valid CHECK (points_to_win >= 10) NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.games
    OWNER to postgres;