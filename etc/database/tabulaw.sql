--
-- PostgreSQL database dump
--

-- Dumped from database version 9.0.3
-- Dumped by pg_dump version 9.0.3
-- Started on 2011-02-18 01:26:13

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 329 (class 2612 OID 11574)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 1521 (class 1259 OID 16675)
-- Dependencies: 6
-- Name: tw_bundleitem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tw_bundleitem (
    bundleitem_id text NOT NULL,
    bundleitem_quotebundle text NOT NULL,
    bundleitem_quote text NOT NULL
);


--
-- TOC entry 1522 (class 1259 OID 16681)
-- Dependencies: 6
-- Name: tw_caseref; Type: TABLE; Schema: public; Owner: -
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


--
-- TOC entry 1523 (class 1259 OID 16687)
-- Dependencies: 6
-- Name: tw_doc; Type: TABLE; Schema: public; Owner: -
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


--
-- TOC entry 1524 (class 1259 OID 16693)
-- Dependencies: 1806 6
-- Name: tw_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tw_permission (
    permission_id text NOT NULL,
    permission_user text NOT NULL,
    permission_doc text,
    permission_quote text,
    permission_quotebundle text,
    permission_orphanedquotebundle boolean DEFAULT false NOT NULL
);


--
-- TOC entry 1525 (class 1259 OID 16700)
-- Dependencies: 6
-- Name: tw_quote; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tw_quote (
    quote_id text NOT NULL,
    quote_startpage integer,
    quote_endpage integer,
    quote_quote text,
    quote_serializedmark text,
    quote_doc text
);


--
-- TOC entry 1526 (class 1259 OID 16706)
-- Dependencies: 6
-- Name: tw_quotebundle; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tw_quotebundle (
    quotebundle_id text NOT NULL,
    quotebundle_name text,
    quotebundle_description text
);


--
-- TOC entry 1527 (class 1259 OID 16712)
-- Dependencies: 6
-- Name: tw_user; Type: TABLE; Schema: public; Owner: -
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


--
-- TOC entry 1528 (class 1259 OID 16718)
-- Dependencies: 6
-- Name: tw_userstate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tw_userstate (
    userstate_id text NOT NULL,
    userstate_user text NOT NULL,
    userstate_quotebundle text NOT NULL
);


--
-- TOC entry 1808 (class 2606 OID 16794)
-- Dependencies: 1521 1521
-- Name: bundleitem_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_bundleitem
    ADD CONSTRAINT bundleitem_id PRIMARY KEY (bundleitem_id);


--
-- TOC entry 1812 (class 2606 OID 16725)
-- Dependencies: 1522 1522
-- Name: caseref_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_caseref
    ADD CONSTRAINT caseref_id PRIMARY KEY (caseref_id);


--
-- TOC entry 1815 (class 2606 OID 16727)
-- Dependencies: 1523 1523
-- Name: doc_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_doc
    ADD CONSTRAINT doc_id PRIMARY KEY (doc_id);


--
-- TOC entry 1822 (class 2606 OID 16729)
-- Dependencies: 1524 1524
-- Name: permission_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_id PRIMARY KEY (permission_id);


--
-- TOC entry 1825 (class 2606 OID 16731)
-- Dependencies: 1525 1525
-- Name: quote_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_quote
    ADD CONSTRAINT quote_id PRIMARY KEY (quote_id);


--
-- TOC entry 1827 (class 2606 OID 16733)
-- Dependencies: 1526 1526
-- Name: quotebundle_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_quotebundle
    ADD CONSTRAINT quotebundle_id PRIMARY KEY (quotebundle_id);


--
-- TOC entry 1829 (class 2606 OID 16735)
-- Dependencies: 1527 1527
-- Name: user_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_user
    ADD CONSTRAINT user_id PRIMARY KEY (user_id);


--
-- TOC entry 1833 (class 2606 OID 16737)
-- Dependencies: 1528 1528
-- Name: userstate_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_userstate
    ADD CONSTRAINT userstate_id PRIMARY KEY (userstate_id);


--
-- TOC entry 1809 (class 1259 OID 16792)
-- Dependencies: 1521
-- Name: fki_bundleitem_quote; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_bundleitem_quote ON tw_bundleitem USING btree (bundleitem_quote);


--
-- TOC entry 1810 (class 1259 OID 16738)
-- Dependencies: 1521
-- Name: fki_bundleitem_quotebundle; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_bundleitem_quotebundle ON tw_bundleitem USING btree (bundleitem_quotebundle);


--
-- TOC entry 1816 (class 1259 OID 16739)
-- Dependencies: 1523
-- Name: fki_doc_caseref; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_doc_caseref ON tw_doc USING btree (doc_caseref);


--
-- TOC entry 1817 (class 1259 OID 16795)
-- Dependencies: 1524
-- Name: fki_permission_doc; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_permission_doc ON tw_permission USING btree (permission_doc);


--
-- TOC entry 1818 (class 1259 OID 16796)
-- Dependencies: 1524
-- Name: fki_permission_quote; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_permission_quote ON tw_permission USING btree (permission_quote);


--
-- TOC entry 1819 (class 1259 OID 16797)
-- Dependencies: 1524
-- Name: fki_permission_quotebundle; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_permission_quotebundle ON tw_permission USING btree (permission_quotebundle);


--
-- TOC entry 1820 (class 1259 OID 16798)
-- Dependencies: 1524
-- Name: fki_permission_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_permission_user ON tw_permission USING btree (permission_user);


--
-- TOC entry 1823 (class 1259 OID 16799)
-- Dependencies: 1525
-- Name: fki_quote_doc; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_quote_doc ON tw_quote USING btree (quote_doc);


--
-- TOC entry 1830 (class 1259 OID 16740)
-- Dependencies: 1528
-- Name: fki_userstate_quotebundle; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_userstate_quotebundle ON tw_userstate USING btree (userstate_quotebundle);


--
-- TOC entry 1831 (class 1259 OID 16741)
-- Dependencies: 1528
-- Name: fki_userstate_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fki_userstate_user ON tw_userstate USING btree (userstate_user);


--
-- TOC entry 1813 (class 1259 OID 16809)
-- Dependencies: 1522
-- Name: i_caseref_url; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX i_caseref_url ON tw_caseref USING btree (caseref_url);


--
-- TOC entry 1834 (class 2606 OID 16742)
-- Dependencies: 1521 1824 1525
-- Name: bundleitem_quote; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_bundleitem
    ADD CONSTRAINT bundleitem_quote FOREIGN KEY (bundleitem_quote) REFERENCES tw_quote(quote_id) ON DELETE CASCADE;


--
-- TOC entry 1835 (class 2606 OID 16747)
-- Dependencies: 1526 1521 1826
-- Name: bundleitem_quotebundle; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_bundleitem
    ADD CONSTRAINT bundleitem_quotebundle FOREIGN KEY (bundleitem_quotebundle) REFERENCES tw_quotebundle(quotebundle_id) ON DELETE CASCADE;


--
-- TOC entry 1836 (class 2606 OID 16752)
-- Dependencies: 1523 1811 1522
-- Name: doc_caseref; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_doc
    ADD CONSTRAINT doc_caseref FOREIGN KEY (doc_caseref) REFERENCES tw_caseref(caseref_id) ON DELETE CASCADE;


--
-- TOC entry 1837 (class 2606 OID 16757)
-- Dependencies: 1814 1524 1523
-- Name: permission_doc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_doc FOREIGN KEY (permission_doc) REFERENCES tw_doc(doc_id) ON DELETE CASCADE;


--
-- TOC entry 1838 (class 2606 OID 16762)
-- Dependencies: 1524 1824 1525
-- Name: permission_quote; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_quote FOREIGN KEY (permission_quote) REFERENCES tw_quote(quote_id) ON DELETE CASCADE;


--
-- TOC entry 1839 (class 2606 OID 16767)
-- Dependencies: 1826 1526 1524
-- Name: permission_quotebundle; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_quotebundle FOREIGN KEY (permission_quotebundle) REFERENCES tw_quotebundle(quotebundle_id) ON DELETE CASCADE;


--
-- TOC entry 1840 (class 2606 OID 16772)
-- Dependencies: 1828 1527 1524
-- Name: permission_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_permission
    ADD CONSTRAINT permission_user FOREIGN KEY (permission_user) REFERENCES tw_user(user_id) ON DELETE CASCADE;


--
-- TOC entry 1841 (class 2606 OID 16777)
-- Dependencies: 1814 1523 1525
-- Name: quote_doc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_quote
    ADD CONSTRAINT quote_doc FOREIGN KEY (quote_doc) REFERENCES tw_doc(doc_id) ON DELETE CASCADE;


--
-- TOC entry 1842 (class 2606 OID 16782)
-- Dependencies: 1526 1826 1528
-- Name: userstate_quotebundle; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_userstate
    ADD CONSTRAINT userstate_quotebundle FOREIGN KEY (userstate_quotebundle) REFERENCES tw_quotebundle(quotebundle_id) ON DELETE CASCADE;


--
-- TOC entry 1843 (class 2606 OID 16787)
-- Dependencies: 1527 1528 1828
-- Name: userstate_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tw_userstate
    ADD CONSTRAINT userstate_user FOREIGN KEY (userstate_user) REFERENCES tw_user(user_id) ON DELETE CASCADE;


-- Completed on 2011-02-18 01:26:13

--
-- PostgreSQL database dump complete
--

