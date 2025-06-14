--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5 (Debian 17.5-1.pgdg120+1)
-- Dumped by pg_dump version 17.5 (Debian 17.5-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: calculator_; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.calculator_ (
    id_ uuid DEFAULT gen_random_uuid() NOT NULL,
    title_ character varying(255) NOT NULL,
    description_ text,
    config_ jsonb NOT NULL,
    created_at_ timestamp without time zone DEFAULT now() NOT NULL,
    updated_at_ timestamp without time zone DEFAULT now() NOT NULL,
    user_id_ uuid NOT NULL
);


ALTER TABLE public.calculator_ OWNER TO admin;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO admin;

--
-- Name: history_; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.history_ (
    id_ uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id_ uuid NOT NULL,
    calculator_id_ uuid NOT NULL,
    calculator_title_ text NOT NULL,
    accessed_at_ timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.history_ OWNER TO admin;

--
-- Name: session_; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.session_ (
    id_ uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id_ uuid NOT NULL,
    accessed_at_ timestamp without time zone NOT NULL
);


ALTER TABLE public.session_ OWNER TO admin;

--
-- Name: user_; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.user_ (
    id_ uuid DEFAULT gen_random_uuid() NOT NULL,
    username_ character varying(255) NOT NULL,
    password_ character varying(255) NOT NULL,
    roles_ text DEFAULT ',USER,'::text NOT NULL
);


ALTER TABLE public.user_ OWNER TO admin;

--
-- Data for Name: calculator_; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.calculator_ (id_, title_, description_, config_, created_at_, updated_at_, user_id_) FROM stdin;
d0bac504-bf44-4cfc-85f7-053de7a94509	BMI Calculator	Oblicza wskaźnik masy ciała (BMI)	{"inputs": [{"id": "weight", "name": "Waga (kg)", "type": "NUMBER", "order": 1, "number": 70, "precision": 1}, {"id": "height", "name": "Wzrost (cm)", "type": "NUMBER", "order": 1, "number": 175, "precision": 1}], "outputs": [{"name": "Wskaźnik masy ciała (BMI)", "order": 1, "formula": "ROUND_TO_N(${weight} / POW(${height} / 100, 2), 1)", "precision": 1}]}	2025-06-14 14:10:03.916951	2025-06-14 14:10:03.916951	00000000-0000-0000-0000-000000000000
f7d2bee4-abde-4869-94b5-1496184a4a2c	Kalkulator Kredytowy	Oblicza miesięczną ratę kredytu na podstawie kwoty, oprocentowania i okresu spłaty.	{"inputs": [{"id": "loan_amount", "name": "Kwota kredytu (PLN)", "type": "NUMBER", "order": 1, "number": 300000, "precision": 2}, {"id": "interest_rate", "name": "Oprocentowanie roczne (%)", "type": "NUMBER", "order": 2, "number": 8, "precision": 2}, {"id": "years", "name": "Okres (lata)", "type": "NUMBER", "order": 3, "number": 30, "precision": 0}], "outputs": [{"name": "Rata miesięczna", "order": 1, "formula": "ROUND_TO_N(${loan_amount} * (${interest_rate} / 1200) / (1 - POW(1 + (${interest_rate} / 1200), -${years} * 12)), 2)", "precision": 2}]}	2025-06-14 14:10:03.916951	2025-06-14 14:10:03.916951	00000000-0000-0000-0000-000000000000
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	20250611.1335	Create user table	SQL	V20250611.1335__Create_user_table.sql	-741887637	admin	2025-06-14 14:10:03.881264	7	t
2	20250613.1313	Create session table	SQL	V20250613.1313__Create_session_table.sql	1162155543	admin	2025-06-14 14:10:03.904335	3	t
3	20250614.1200	Create calculator table	SQL	V20250614.1200__Create_calculator_table.sql	126928066	admin	2025-06-14 14:10:03.913257	5	t
4	20250615.0900	Create history table	SQL	V20250615.0900__Create_history_table.sql	-691279366	admin	2025-06-14 14:10:03.924379	4	t
\.


--
-- Data for Name: history_; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.history_ (id_, user_id_, calculator_id_, calculator_title_, accessed_at_) FROM stdin;
\.


--
-- Data for Name: session_; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.session_ (id_, user_id_, accessed_at_) FROM stdin;
51a3d5e6-34c5-4792-9b1d-d9a0b4def920	00000000-0000-0000-0000-000000000000	2025-06-14 14:10:24.664028
2130b0e1-3b79-46fd-96e6-5e125ea7513f	00000000-0000-0000-0000-000000000000	2025-06-14 14:10:39.822828
09ef97d0-c396-4b76-bdf7-ead780f6cea6	00000000-0000-0000-0000-000000000000	2025-06-14 14:10:53.279714
d6d7a8e1-7e9c-4402-8871-9eb55e889159	00000000-0000-0000-0000-000000000000	2025-06-14 14:11:02.428602
705fd603-533b-4dae-860f-7179858e3585	00000000-0000-0000-0000-000000000000	2025-06-14 14:11:36.744634
\.


--
-- Data for Name: user_; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.user_ (id_, username_, password_, roles_) FROM stdin;
00000000-0000-0000-0000-000000000000	admin	$2a$12$b9eJGYp/RL/YZDsxFnEFgeDc2cFGmEjBlxq5vv0JZ8TjL8899k5te	,ADMIN,
\.


--
-- Name: calculator_ calculator__pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.calculator_
    ADD CONSTRAINT calculator__pkey PRIMARY KEY (id_);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: history_ history__calculator_id__key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.history_
    ADD CONSTRAINT history__calculator_id__key UNIQUE (calculator_id_);


--
-- Name: history_ history__pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.history_
    ADD CONSTRAINT history__pkey PRIMARY KEY (id_);


--
-- Name: session_ session__pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.session_
    ADD CONSTRAINT session__pkey PRIMARY KEY (id_);


--
-- Name: user_ user__pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_
    ADD CONSTRAINT user__pkey PRIMARY KEY (id_);


--
-- Name: user_ user__username__key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_
    ADD CONSTRAINT user__username__key UNIQUE (username_);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: history_ calculator__fk; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.history_
    ADD CONSTRAINT calculator__fk FOREIGN KEY (calculator_id_) REFERENCES public.calculator_(id_) ON DELETE CASCADE;


--
-- Name: session_ user__fk; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.session_
    ADD CONSTRAINT user__fk FOREIGN KEY (user_id_) REFERENCES public.user_(id_) ON DELETE CASCADE;


--
-- Name: history_ user__fk; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.history_
    ADD CONSTRAINT user__fk FOREIGN KEY (user_id_) REFERENCES public.user_(id_) ON DELETE CASCADE;


--
-- Name: calculator_ user__fl; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.calculator_
    ADD CONSTRAINT user__fl FOREIGN KEY (user_id_) REFERENCES public.user_(id_) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

