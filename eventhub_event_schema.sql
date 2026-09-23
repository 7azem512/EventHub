--
-- PostgreSQL database dump
--

\restrict ud3MgRGf2w0RIbw3nxcAr6eLX13FTOwQlFISEb6UceGbxDoW2ccIHcrPJPlZlb4

-- Dumped from database version 16.15 (Debian 16.15-1.pgdg13+2)
-- Dumped by pg_dump version 16.15 (Debian 16.15-1.pgdg13+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categories (
    id uuid NOT NULL,
    description character varying(500),
    name character varying(100) NOT NULL,
    slug character varying(120) NOT NULL
);


--
-- Name: events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.events (
    id uuid NOT NULL,
    booking_end_date timestamp(6) without time zone NOT NULL,
    booking_start_date timestamp(6) without time zone NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    end_date timestamp(6) without time zone NOT NULL,
    location character varying(255),
    organizer_id uuid NOT NULL,
    start_date timestamp(6) without time zone NOT NULL,
    status character varying(50) NOT NULL,
    title character varying(200) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    category_id uuid NOT NULL,
    CONSTRAINT events_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PENDING_APPROVAL'::character varying, 'PUBLISHED'::character varying, 'REJECTED'::character varying, 'CANCELLED'::character varying, 'COMPLETED'::character varying])::text[])))
);


--
-- Name: ticket_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ticket_types (
    id uuid NOT NULL,
    capacity integer NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    name character varying(100) NOT NULL,
    price numeric(10,2) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    event_id uuid NOT NULL,
    CONSTRAINT ticket_types_capacity_check CHECK ((capacity >= 1))
);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (id);


--
-- Name: ticket_types ticket_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ticket_types
    ADD CONSTRAINT ticket_types_pkey PRIMARY KEY (id);


--
-- Name: categories ukoul14ho7bctbefv8jywp5v3i2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT ukoul14ho7bctbefv8jywp5v3i2 UNIQUE (slug);


--
-- Name: categories ukt8o6pivur7nn124jehx7cygw5; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT ukt8o6pivur7nn124jehx7cygw5 UNIQUE (name);


--
-- Name: ticket_types fkl83j9knh8jrssp3skaeubrrk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ticket_types
    ADD CONSTRAINT fkl83j9knh8jrssp3skaeubrrk FOREIGN KEY (event_id) REFERENCES public.events(id);


--
-- Name: events fko6mla8j1p5bokt4dxrlmgwc28; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT fko6mla8j1p5bokt4dxrlmgwc28 FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- PostgreSQL database dump complete
--

\unrestrict ud3MgRGf2w0RIbw3nxcAr6eLX13FTOwQlFISEb6UceGbxDoW2ccIHcrPJPlZlb4

