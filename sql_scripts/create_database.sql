-- Database Creation File for the Spades Game--
-- Create User table --
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

--- Create Role Table --
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

--Create user_role table--
CREATE TABLE public.user_role
(
    user_id integer NOT NULL,
    role_id integer NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
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

--Create games table--
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

--Create moves table --
CREATE TABLE public.moves
(
    move_id integer NOT NULL,
    game_id integer NOT NULL,
    user_id integer NOT NULL,
    round_id integer NOT NULL,
    card_played character(3) NOT NULL,
    CONSTRAINT move_pk PRIMARY KEY (move_id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.moves
    OWNER to postgres;

--Create Rounds table --
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
    round_status character(1) COLLATE pg_catalog."default" NOT NULL DEFAULT 'b'::bpchar,
    forfeit_status boolean DEFAULT false,
    CONSTRAINT pk_rounds PRIMARY KEY (game_id, round_number),
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

--- insert two user roles (Admin and User) in to the roles table --
insert into public.role values(1, 'ADMIN');
insert into public.role values(2, 'USER');