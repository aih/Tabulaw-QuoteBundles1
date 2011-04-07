--
-- PostgreSQL database dump
--

-- Dumped from database version 9.0.3
-- Dumped by pg_dump version 9.0.3
-- Started on 2011-03-11 16:49:27

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 329 (class 2612 OID 11574)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1521 (class 1259 OID 243967)
-- Dependencies: 5
-- Name: tw_bundleitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_bundleitem (
    bundleitem_id text NOT NULL,
    bundleitem_quotebundle text NOT NULL,
    bundleitem_quote text NOT NULL
);


ALTER TABLE public.tw_bundleitem OWNER TO postgres;

--
-- TOC entry 1522 (class 1259 OID 243973)
-- Dependencies: 5
-- Name: tw_caseref; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_caseref (
    caseref_id text NOT NULL,
    caseref_reftoken text,
    caseref_parties text,
    caseref_docloc text,
    caseref_court text,
    caseref_url text,
    caseref_firstpagenumber integer,
    caseref_lastpagenumber integer,
    caseref_year integer
);


ALTER TABLE public.tw_caseref OWNER TO postgres;

--
-- TOC entry 1523 (class 1259 OID 243979)
-- Dependencies: 5
-- Name: tw_doc; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_doc (
    doc_id text NOT NULL,
    doc_htmlcontent text,
    doc_pagesxpath text,
    doc_firstpagenumber integer,
    doc_title text,
    doc_date date,
    doc_caseref text,
    doc_referencedoc boolean
);


ALTER TABLE public.tw_doc OWNER TO postgres;

--
-- TOC entry 1524 (class 1259 OID 243985)
-- Dependencies: 5
-- Name: tw_permission; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_permission (
    permission_id text NOT NULL,
    permission_user text NOT NULL,
    permission_doc text,
    permission_quote text,
    permission_quotebundle text
);


ALTER TABLE public.tw_permission OWNER TO postgres;

--
-- TOC entry 1525 (class 1259 OID 243992)
-- Dependencies: 5
-- Name: tw_quote; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_quote (
    quote_id text NOT NULL,
    quote_startpage integer,
    quote_endpage integer,
    quote_quote text,
    quote_serializedmark text,
    quote_doc text
);


ALTER TABLE public.tw_quote OWNER TO postgres;

--
-- TOC entry 1526 (class 1259 OID 243998)
-- Dependencies: 5
-- Name: tw_quotebundle; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_quotebundle (
    quotebundle_id text NOT NULL,
    quotebundle_name text,
    quotebundle_description text
);


ALTER TABLE public.tw_quotebundle OWNER TO postgres;

--
-- TOC entry 1527 (class 1259 OID 244004)
-- Dependencies: 5
-- Name: tw_user; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_user (
    user_id text NOT NULL,
    user_name text,
    user_emailaddress text,
    user_password text,
    user_locked boolean,
    user_enabled boolean,
    user_expires date,
    user_roles text,
    user_appfeatures text,
    user_oauthparameters text,
    user_oauthparametersextra text
);


ALTER TABLE public.tw_user OWNER TO postgres;

--
-- TOC entry 1528 (class 1259 OID 244010)
-- Dependencies: 5
-- Name: tw_userstate; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tw_userstate (
    userstate_id text NOT NULL,
    userstate_user text NOT NULL,
    userstate_quotebundle text,
    userstate_allquotebundle text
);


ALTER TABLE public.tw_userstate OWNER TO postgres;

--
-- TOC entry 1807 (class 2606 OID 244017)
-- Dependencies: 1521 1521
-- Name: bundleitem_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_bundleitem
    ADD CONSTRAINT bundleitem_id PRIMARY KEY (bundleitem_id);


--
-- TOC entry 1811 (class 2606 OID 244019)
-- Dependencies: 1522 1522
-- Name: caseref_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_caseref
    ADD CONSTRAINT caseref_id PRIMARY KEY (caseref_id);


--
-- TOC entry 1814 (class 2606 OID 244021)
-- Dependencies: 1523 1523
-- Name: doc_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_doc
    ADD CONSTRAINT doc_id PRIMARY KEY (doc_id);


--
-- TOC entry 1821 (class 2606 OID 244023)
-- Dependencies: 1524 1524
-- Name: permission_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_id PRIMARY KEY (permission_id);


--
-- TOC entry 1824 (class 2606 OID 244025)
-- Dependencies: 1525 1525
-- Name: quote_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_quote
    ADD CONSTRAINT quote_id PRIMARY KEY (quote_id);


--
-- TOC entry 1826 (class 2606 OID 244027)
-- Dependencies: 1526 1526
-- Name: quotebundle_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_quotebundle
    ADD CONSTRAINT quotebundle_id PRIMARY KEY (quotebundle_id);


--
-- TOC entry 1828 (class 2606 OID 244029)
-- Dependencies: 1527 1527
-- Name: user_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_user
    ADD CONSTRAINT user_id PRIMARY KEY (user_id);


--
-- TOC entry 1833 (class 2606 OID 244031)
-- Dependencies: 1528 1528
-- Name: userstate_id; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tw_userstate
    ADD CONSTRAINT userstate_id PRIMARY KEY (userstate_id);


--
-- TOC entry 1808 (class 1259 OID 244032)
-- Dependencies: 1521
-- Name: fki_bundleitem_quote; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_bundleitem_quote ON tw_bundleitem USING btree (bundleitem_quote);


--
-- TOC entry 1809 (class 1259 OID 244033)
-- Dependencies: 1521
-- Name: fki_bundleitem_quotebundle; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_bundleitem_quotebundle ON tw_bundleitem USING btree (bundleitem_quotebundle);


--
-- TOC entry 1815 (class 1259 OID 244034)
-- Dependencies: 1523
-- Name: fki_doc_caseref; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_doc_caseref ON tw_doc USING btree (doc_caseref);


--
-- TOC entry 1816 (class 1259 OID 244035)
-- Dependencies: 1524
-- Name: fki_permission_doc; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_permission_doc ON tw_permission USING btree (permission_doc);


--
-- TOC entry 1817 (class 1259 OID 244036)
-- Dependencies: 1524
-- Name: fki_permission_quote; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_permission_quote ON tw_permission USING btree (permission_quote);


--
-- TOC entry 1818 (class 1259 OID 244037)
-- Dependencies: 1524
-- Name: fki_permission_quotebundle; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_permission_quotebundle ON tw_permission USING btree (permission_quotebundle);


--
-- TOC entry 1819 (class 1259 OID 244038)
-- Dependencies: 1524
-- Name: fki_permission_user; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_permission_user ON tw_permission USING btree (permission_user);


--
-- TOC entry 1822 (class 1259 OID 244039)
-- Dependencies: 1525
-- Name: fki_quote_doc; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_quote_doc ON tw_quote USING btree (quote_doc);


--
-- TOC entry 1829 (class 1259 OID 244093)
-- Dependencies: 1528
-- Name: fki_userstate_allquotebundle; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX fki_userstate_allquotebundle ON tw_userstate USING btree (userstate_allquotebundle);


--
-- TOC entry 1830 (class 1259 OID 244040)
-- Dependencies: 1528
-- Name: fki_userstate_quotebundle; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_userstate_quotebundle ON tw_userstate USING btree (userstate_quotebundle);


--
-- TOC entry 1831 (class 1259 OID 244041)
-- Dependencies: 1528
-- Name: fki_userstate_user; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_userstate_user ON tw_userstate USING btree (userstate_user);


--
-- TOC entry 1812 (class 1259 OID 244042)
-- Dependencies: 1522
-- Name: i_caseref_url; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_caseref_url ON tw_caseref USING btree (caseref_url);


--
-- TOC entry 1834 (class 2606 OID 244043)
-- Dependencies: 1521 1525 1823
-- Name: bundleitem_quote; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_bundleitem
    ADD CONSTRAINT bundleitem_quote FOREIGN KEY (bundleitem_quote) REFERENCES tw_quote(quote_id) ON DELETE CASCADE;


--
-- TOC entry 1835 (class 2606 OID 244048)
-- Dependencies: 1521 1526 1825
-- Name: bundleitem_quotebundle; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_bundleitem
    ADD CONSTRAINT bundleitem_quotebundle FOREIGN KEY (bundleitem_quotebundle) REFERENCES tw_quotebundle(quotebundle_id) ON DELETE CASCADE;


--
-- TOC entry 1836 (class 2606 OID 244053)
-- Dependencies: 1810 1523 1522
-- Name: doc_caseref; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_doc
    ADD CONSTRAINT doc_caseref FOREIGN KEY (doc_caseref) REFERENCES tw_caseref(caseref_id) ON DELETE CASCADE;


--
-- TOC entry 1837 (class 2606 OID 244058)
-- Dependencies: 1524 1813 1523
-- Name: permission_doc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_doc FOREIGN KEY (permission_doc) REFERENCES tw_doc(doc_id) ON DELETE CASCADE;


--
-- TOC entry 1838 (class 2606 OID 244063)
-- Dependencies: 1524 1823 1525
-- Name: permission_quote; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_quote FOREIGN KEY (permission_quote) REFERENCES tw_quote(quote_id) ON DELETE CASCADE;


--
-- TOC entry 1839 (class 2606 OID 244068)
-- Dependencies: 1524 1526 1825
-- Name: permission_quotebundle; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_quotebundle FOREIGN KEY (permission_quotebundle) REFERENCES tw_quotebundle(quotebundle_id) ON DELETE CASCADE;


--
-- TOC entry 1840 (class 2606 OID 244073)
-- Dependencies: 1827 1524 1527
-- Name: permission_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_user FOREIGN KEY (permission_user) REFERENCES tw_user(user_id) ON DELETE CASCADE;


--
-- TOC entry 1841 (class 2606 OID 244078)
-- Dependencies: 1523 1525 1813
-- Name: quote_doc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_quote
    ADD CONSTRAINT quote_doc FOREIGN KEY (quote_doc) REFERENCES tw_doc(doc_id) ON DELETE CASCADE;


--
-- TOC entry 1844 (class 2606 OID 244099)
-- Dependencies: 1526 1825 1528
-- Name: userstate_allquotebundle; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_userstate
    ADD CONSTRAINT userstate_allquotebundle FOREIGN KEY (userstate_allquotebundle) REFERENCES tw_quotebundle(quotebundle_id) ON DELETE CASCADE;


--
-- TOC entry 1842 (class 2606 OID 244083)
-- Dependencies: 1526 1528 1825
-- Name: userstate_quotebundle; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_userstate
    ADD CONSTRAINT userstate_quotebundle FOREIGN KEY (userstate_quotebundle) REFERENCES tw_quotebundle(quotebundle_id) ON DELETE CASCADE;


--
-- TOC entry 1843 (class 2606 OID 244088)
-- Dependencies: 1528 1827 1527
-- Name: userstate_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tw_userstate
    ADD CONSTRAINT userstate_user FOREIGN KEY (userstate_user) REFERENCES tw_user(user_id) ON DELETE CASCADE;


--
-- TOC entry 1849 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2011-03-11 16:49:27

--
-- PostgreSQL database dump complete
--

