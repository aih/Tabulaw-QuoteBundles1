/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.service.entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.BundleUserBinding;
import com.tabulaw.model.CaseRef;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.DocUserBinding;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.Reference;
import com.tabulaw.model.User;
import com.tabulaw.model.UserState;
import com.tabulaw.service.sanitizer.ISanitizer;
import com.tabulaw.util.UUID;
import com.thoughtworks.xstream.XStream;

/**
 * Manages the persistence of user related data that is not part of the user
 * entity.
 *
 * @author jpk
 */
public class UserDataService {
	private class QuoteBundleRowMapper implements ParameterizedRowMapper<QuoteBundle> {

		public QuoteBundle mapRow(ResultSet rs, int rownum) throws SQLException {
            QuoteBundle ret = new QuoteBundle();
            ret.setId(rs.getString("quotebundle_id"));
            ret.setName(rs.getString("quotebundle_name"));
            ret.setDescription(rs.getString("quotebundle_description"));
            return ret;
		}
	}
	
	private class DocRefWithCaseRefRowMapper implements ParameterizedRowMapper<DocRef> {

		public DocRef mapRow(ResultSet rs, int rownum) throws SQLException {
			DocRef ret = new DocRef();
			ret.setId(rs.getString("doc_id"));
			ret.setTitle(rs.getString("doc_title"));
			ret.setDate(rs.getDate("doc_date"));
			ret.setReferenceDoc(rs.getBoolean("doc_referencedoc"));

			String docCaseRef = rs.getString("doc_caseref");
			if (docCaseRef != null && !docCaseRef.isEmpty()) {
				CaseRef caseRef = new CaseRef();
				caseRef.setId(rs.getString("caseref_id"));
				caseRef.setCourt(rs.getString("caseref_court"));
				caseRef.setDocLoc(rs.getString("caseref_docloc"));
				caseRef.setFirstPageNumber(rs.getInt("caseref_firstpagenumber"));
				caseRef.setLastPageNumber(rs.getInt("caseref_lastpagenumber"));
				caseRef.setParties(rs.getString("caseref_parties"));
				caseRef.setReftoken(rs.getString("caseref_reftoken"));
				caseRef.setUrl(rs.getString("caseref_url"));
				caseRef.setYear(rs.getInt("caseref_year"));
				ret.setReference(caseRef);
			}
			return ret;
		}
	}
	
	private class DocContentRowMapper implements ParameterizedRowMapper<DocContent> {

		public DocContent mapRow(ResultSet rs, int rownum) throws SQLException {
            DocContent ret = new DocContent();
            ret.setId(rs.getString("doc_id"));
            ret.setHtmlContent(rs.getString("doc_htmlcontent"));
            ret.setPagesXPath((List<int[]>) xs.fromXML(rs.getString("doc_pagesxpath")));
            ret.setFirstPageNumber(rs.getInt("doc_firstpagenumber"));
            return ret;
        }
    }
	
	private class UserStateRowMapper implements ParameterizedRowMapper<UserState> {

		public UserState mapRow(ResultSet rs, int rownum) throws SQLException {
            UserState ret = new UserState();
            ret.setId(rs.getString("userstate_id"));
            String currentQuoteBundleId = rs.getString("userstate_quotebundle");
            if (currentQuoteBundleId!=null) {
            	ret.setCurrentQuoteBundleId(currentQuoteBundleId);
            }
            ret.setUserId(rs.getString("userstate_user"));
            ret.setAllQuoteBundleId(rs.getString("userstate_allquotebundle"));
            return ret;
        }
    }
	
	private class QuoteRowMapper implements ParameterizedRowMapper<Quote> {

		public Quote mapRow(ResultSet rs, int rownum) throws SQLException {
            Quote ret = new Quote();
            ret.setId(rs.getString("quote_id"));
            ret.setEndPage(rs.getInt("quote_endpage"));
            //TODO set quote doc
//            ret.setDocument(loadDocRefWithCaseRef(rs));
            ret.setQuote(rs.getString("quote_quote"));
            ret.setSerializedMark(rs.getString("quote_serializedmark"));
            ret.setStartPage(rs.getInt("quote_startpage"));
            return ret;
    }
	
	}
	

    /**
     * A simple way to provide a list of bundles in addition to conveying which of
     * them is the orphan qoute container.
     *
     * @author jpk
     */
    public static class BundleContainer {

        private final List<QuoteBundle> bundles;

        public BundleContainer(List<QuoteBundle> bundles) {
            super();
            this.bundles = bundles;
        }

        public List<QuoteBundle> getBundles() {
            return bundles;
        }

    }

    private ISanitizer sanitizer;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private XStream xs;
    

    /**
     * Constructor
     *
     * @param validationFactory
     */
    @Inject
    public UserDataService(ValidatorFactory validationFactory, ISanitizer sanitizer, DataSource ds) {
        this.sanitizer = sanitizer;
        
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(ds);
		
        xs = new XStream();
        xs.alias("role", User.Role.class);
		
        
    }

    public String toXML(Object obj)
    {
        return xs.toXML(obj);
    }

    /**
     * Gets a list of all docs for a given user.
     *
     * @param userId user id
     * @return list of docs
     */
    @Transactional
    public List<DocRef> getDocsForUser(String userId) {
        System.out.println("getDocsForUser " + userId);
        if (userId == null) throw new NullPointerException();
        List<DocRef> ret = this.simpleJdbcTemplate.query(
        		"select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id, tw_permission where permission_doc=doc_id AND permission_user=?",
				new DocRefWithCaseRefRowMapper(),
				userId);        
        return ret;

    }

    /**
     * Provides a list of all doc refs in the system.
     *
     * @return doc list
     */
	@Transactional
    public List<DocRef> getAllDocs() {
        System.out.println("getAllDocs");
        List<DocRef> ret = this.simpleJdbcTemplate.query(
        		"select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id, tw_permission where permission_doc=doc_id AND permission_user=?",
				new DocRefWithCaseRefRowMapper());        
        return ret;
    }

    /**
     * Gets the doc ref given the doc id.
     *
     * @param docId
     * @return to loaded doc ref
     * @throws EntityNotFoundException
     */

	@Transactional
    public DocRef getDoc(String docId) throws EntityNotFoundException {
        System.out.println("getDoc " + docId);
        DocRef doc = this.simpleJdbcTemplate
		.queryForObject(
				"select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id where doc_id=?",
				new DocRefWithCaseRefRowMapper(), docId);
        if (doc == null) {
            throw new EntityNotFoundException("No document found with id: '" + docId);
        }
        return doc;
    }

   /**
     * Gets the doc <em>content</em> given the doc id.
     *
     * @param docId {@link DocRef} id
     * @return to loaded doc content
     * @throws EntityNotFoundException
     */
	@Transactional
    public DocContent getDocContent(String docId) throws EntityNotFoundException {
        System.out.println("getDocContent " + docId);
        DocContent content = this.simpleJdbcTemplate.queryForObject(
        		"select * from tw_doc where doc_id=?",
				new DocContentRowMapper(),
				docId);
        if (content == null) {
        	throw new EntityNotFoundException("No document found with id: '" + docId);
        }
        
        return content;
    }


    /**
     * Gets the user state for the given user id
     *
     * @param userId
     * @return the user's state entity
     * @throws EntityNotFoundException
     */
	@Transactional
    public UserState getUserState(String userId) throws EntityNotFoundException {
        System.out.println("getUserState " + userId);
        UserState userState = this.simpleJdbcTemplate.queryForObject(
        		"select * from tw_userstate where userstate_user=?",
				new UserStateRowMapper(),
				userId);
        if (userState == null) {
            throw new EntityNotFoundException("No user state with user id: '" + userId + "' was found.");
        }
        
        return userState;

    }


    /**
     * Saves user state.
     *
     * @param userState
     * @throws EntityExistsException
     */
	public void saveUserState(UserState userState) throws EntityExistsException {
		System.out.println("saveUserState id:" + userState.getId() + " | bundleId"
				+ userState.getCurrentQuoteBundleId() + " | user id: " + userState.getUserId());
		if (userState == null)
			throw new NullPointerException();

		try {
			getUserState(userState.getId());
			this.simpleJdbcTemplate
					.update(
							"update tw_userstate set userstate_quotebundle=?, userstate_allquotebundle=?  where userstate_id=?",
							userState.getCurrentQuoteBundleId(), userState.getAllQuoteBundleId(), userState.getId());
		} catch (EntityNotFoundException enfe) {
			this.simpleJdbcTemplate
					.update(
							"insert into tw_userstate(userstate_quotebundle, userstate_allquotebundle, userstate_user, userstate_id) values (?,?,?,?)",
							userState.getCurrentQuoteBundleId(), userState.getAllQuoteBundleId(), userState.getId(),
							UUID.uuid());
		}
	}

    /**
     * Gets the sole bundle dedicated to housing all quotes for the given
     * user id.
     * <p/>
     * Auto-creates this bundle if it is found not to exist.
     *
     * @param userId user id
     * @return non-<code>null</code> {@link QuoteBundle} instance
     */
	@Transactional
	public QuoteBundle getAllQuoteBundleForUser(String userId) {
		System.out.println("getAllQuoteBundleForUser " + userId);
		if (userId == null)
			throw new NullPointerException();

		QuoteBundle qb = this.simpleJdbcTemplate
				.queryForObject(
						"select * from tw_quotebundle, tw_userstate where userstate_allquotebundle=quotebundle_id AND userstate_user=?",
						new QuoteBundleRowMapper(), userId);
		if (qb == null) {
			// create all quote bundle container
			QuoteBundle oqc = new QuoteBundle();
			oqc.setId(UUID.uuid());
			oqc.setName("All Quotes");
			oqc.setDescription("All quotes stored there");
			addBundleForUser(userId, oqc);

			UserState us = null;

			try {
				us = getUserState(userId);
			} catch (EntityNotFoundException enf) {
				us = new UserState();
				us.setId(UUID.uuid());
				us.setUserId(userId);
			}
			us.setAllQuoteBundleId(oqc.getId());
			saveUserState(us);
			System.out.println("ALL BUNDLE ID:" + oqc.getId());
			return oqc;

		} else {
			qb.setQuotes(getQuotesWithDocRefWithCaseRef(qb.getId()));
			System.out.println("ALL BUNDLE ID:" + qb.getId());
			return qb;
		}

	}

    private List<Quote> getQuotesWithDocRefWithCaseRef(String bundleId)
    {
    	
        List<Quote> ret = this.simpleJdbcTemplate.query(
        		"select * from tw_quote, tw_doc left outer join tw_caseref on doc_caseref=caseref_id, tw_bundleitem where quote_doc=doc_id and bundleitem_quote=quote_id and bundleitem_quotebundle=?",
				new QuoteRowMapper(), bundleId);        
        return ret;
    }

    /**
     * Gets all bundles for a given user.
     * <p/>
     * Auto-creates an all quote bundle if one doesn't exist for the user.
     *
     * @param userId
     * @return list of quote bundles
     */
    @Transactional
    public BundleContainer getBundlesForUser(String userId) {
        System.out.println("getBundlesForUser " + userId);

        // first ensure an all quotes container exists for user
        getAllQuoteBundleForUser(userId);

        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_quotebundle, tw_permission where permission_quotebundle=quotebundle_id AND permission_user=? order by quotebundle_name", Statement.NO_GENERATED_KEYS);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            List<QuoteBundle> list = new ArrayList<QuoteBundle>();
            while (rs.next()) {
                BundleUserBinding bub = dao.loadBundleUserBinding(rs);
                QuoteBundle qb = dao.loadQuoteBundle(rs);
                qb.setQuotes(getQuotesWithDocRefWithCaseRef(qb.getId()));
                list.add(qb);
            }

            return new BundleContainer(list);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    /**
     * Gets the quote bundle given the bundle id.
     *
     * @param bundleId
     * @return
     * @throws EntityNotFoundException
     */
    public QuoteBundle getQuoteBundle(String bundleId) throws EntityNotFoundException {
        System.out.println("getQuoteBundle " + bundleId);

        if (bundleId == null) throw new NullPointerException();
        
		QuoteBundle qb = this.simpleJdbcTemplate
		.queryForObject(
				"select * from tw_quotebundle where quotebundle_id=?",
				new QuoteBundleRowMapper(), bundleId);
		if (qb == null) {
			throw new EntityNotFoundException("getQuoteBundle " + bundleId);
		}
		qb.setQuotes(getQuotesWithDocRefWithCaseRef(qb.getId()));
		return qb;
    }

    /**
     * Updates the non-relational bundle properties in the given bundle. The
     * bundle must already exist.
     *
     * @param userId
     * @param bundle ref to an existing bundle whose properties are persisted to
     *               that held in the datastore.
     * @throws IllegalArgumentException     When the given bundle is not new
     * @throws ConstraintViolationException When the bundle's properties don't
     *                                      validate
     * @throws EntityNotFoundException      When the quote bundle isn't found in the
     *                                      datastore
     */
    public void updateBundlePropsForUser(String userId, QuoteBundle bundle) throws IllegalArgumentException,
            ConstraintViolationException, EntityNotFoundException {
        if (userId == null || bundle == null) throw new NullPointerException();
        System.out.println("updateBundlePropsForUser " + userId + " bundleId=" + bundle.getId());
        this.simpleJdbcTemplate.update("update tw_quotebundle set quotebundle_name=?, quotebundle_description=? where quotebundle_id=?", bundle.getName(), bundle.getDescription(), bundle.getId());
    }

    /**
     * Updates a quote.
     *
     * @param quote
     * @return the persisted quote
     */
    public Quote updateQuote(Quote quote) {
        System.out.println("updateQuote");
        throw new UnsupportedOperationException();
    }

    /**
     * Creates or updates the given doc.
     *
     * @param doc the doc to save
     * @return the saved doc
     * @throws ConstraintViolationException When the given doc isn't valid
     */
    public DocRef saveDoc(DocRef doc) throws ConstraintViolationException {
        System.out.println("saveDoc id=" + doc.getId());
        if (doc == null) throw new NullPointerException();

        try {
            Reference ref = doc.getReference();
            String referenceId=null;

            if (ref!=null && ref instanceof CaseRef) {
                CaseRef caseRef = (CaseRef)ref;
                caseRef.setId(UUID.uuid());
                referenceId = caseRef.getId();

                PreparedStatement ps0 = dao.getPreparedStatement("insert into tw_caseref(caseref_id, caseref_court, caseref_docloc, caseref_firstpagenumber, caseref_lastpagenumber, caseref_parties, caseref_reftoken, caseref_url, caseref_year) values (?,?,?,?,?,?,?,?,?)", Statement.NO_GENERATED_KEYS);
                ps0.setString(1, caseRef.getId());
                ps0.setString(2, caseRef.getCourt());
                ps0.setString(3, caseRef.getDocLoc());
                ps0.setInt(4, caseRef.getFirstPageNumber());
                ps0.setInt(5, caseRef.getLastPageNumber());
                ps0.setString(6, caseRef.getParties());
                ps0.setString(7, caseRef.getReftoken());
                ps0.setString(8, caseRef.getUrl());
                ps0.setInt(9, caseRef.getYear());
                ps0.executeUpdate();
            }

            doc.setId(UUID.uuid()) ;
            PreparedStatement ps = dao.getPreparedStatement("insert into tw_doc(doc_id, doc_caseref, doc_title, doc_date, doc_referencedoc) values (?,?,?,?,?)", Statement.NO_GENERATED_KEYS);
            ps.setString(1, doc.getId());
            ps.setString(2, doc.getReference()!=null ? referenceId : null);
            ps.setString(3, doc.getTitle());
            ps.setDate(4, new java.sql.Date(doc.getDate().getTime()));
            ps.setBoolean(5, doc.isReferenceDoc());

            ps.executeUpdate();
            System.out.println("id=" + doc.getId());
            return doc;

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }

    public void saveDocContent(DocContent docContent) throws ConstraintViolationException {
        System.out.println("saveDocContent id:" + docContent.getId());

        if (docContent == null) throw new NullPointerException();

        try {
            PreparedStatement ps = dao.getPreparedStatement("update tw_doc set doc_htmlcontent=?, doc_firstpagenumber=?, doc_pagesxpath=? where doc_id=?", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, docContent.getHtmlContent());
            ps.setInt(2, docContent.getFirstPageNumber());
            ps.setString(3, dao.toXML(docContent.getPagesXPath()));
            ps.setString(4, docContent.getId());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }

    private void sanitize(DocContent docContent) throws ConstraintViolationException {
        System.out.println("sanitize");
        throw new UnsupportedOperationException();
    }


    /**
     * Deletes the doc and doc content given its id as well as all doc/user
     * bindings as well as any referenced quotes <em>permanently</em>.
     * <p/>
     * Both the Doc and DocContent entities are deleted.
     * <p/>
     * NOTE: Quotes (which may point to the target doc) are also permanently
     * deleted!
     *
     * @param docId id of the doc to delete
     * @throws EntityNotFoundException when the doc of the given id can't be found
     */
    public void deleteDoc(String docId) throws EntityNotFoundException {
        System.out.println("deleteDoc " + docId);

        try {
            PreparedStatement ps1 = dao.getPreparedStatement("delete from tw_doc where doc_id=?", Statement.NO_GENERATED_KEYS);
            ps1.setString(1, docId);
            ps1.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }

    /**
     * Finds a case type doc by its remote url property.
     *
     * @param remoteUrl the unique remote url
     * @return the found doc
     * @throws EntityNotFoundException
     */
    public DocRef findCaseDocByRemoteUrl(String remoteUrl) throws EntityNotFoundException {
        System.out.println("findCaseDocByRemoteUrl " + remoteUrl);

        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id where caseref_url=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, remoteUrl);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return dao.loadDocRefWithCaseRef(rs);
            } else {
                throw new EntityNotFoundException("remoteUrl "+remoteUrl);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    /**
     * Saves the quote bundle for the given user as well as any referenced quotes
     * doing a full replacement of the bundle with that given as well as the child
     * quotes.
     *
     * @param userId
     * @param bundle
     * @return the saved bundle
     * @throws ConstraintViolationException When the given bundle isn't valid
     */
    public QuoteBundle saveBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
        System.out.println("saveBundleForUser " + userId);
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the given bundle and associates it with the given user.
     * <p/>
     * Any quotes contained in the bundle are ignored.
     *
     * @param userId
     * @param bundle
     * @return the persisted bundle
     * @throws ConstraintViolationException When the givne bundle isn't valid
     */
    public QuoteBundle addBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
        System.out.println("addBundleForUser " + userId + " | bundleId " + bundle.getId());
        if (userId == null || bundle == null) throw new NullPointerException();
        try {
            PreparedStatement ps1 = dao.getPreparedStatement("insert into tw_quotebundle(quotebundle_id, quotebundle_name, quotebundle_description) values (?,?,?)", Statement.NO_GENERATED_KEYS);
            ps1.setString(1, bundle.getId());
            ps1.setString(2, bundle.getName());
            ps1.setString(3, bundle.getDescription());

            ps1.executeUpdate();

            addBundleUserBinding(userId, bundle.getId());
            return bundle;

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }


    }

    /**
     * Deletes a quote bundle and its association to the given user.
     *
     * @param userId
     * @param bundleId
     * @param deleteQuotes delete contained quotes as well? if <code>false</code>,
     *                     any contained quotes will be moved to the un-assigned bundle for the
     *                     given user
     * @throws EntityNotFoundException When either the user or bundle can't be
     *                                 resolved
     */
    public void deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes) throws EntityNotFoundException {
        System.out.println("deleteBundleForUser " + userId);

        try {
            if (!deleteQuotes) {
                QuoteBundle oqb = getAllQuoteBundleForUser(userId);

                PreparedStatement ps2 = dao.getPreparedStatement("update tw_bundleitem set bundleitem_quotebundle=? where bundleitem_quotebundle=?", Statement.RETURN_GENERATED_KEYS);
                ps2.setString(1, oqb.getId());
                ps2.setString(2, bundleId);
                ps2.executeUpdate();
            }

            PreparedStatement ps1 = dao.getPreparedStatement("delete from tw_quotebundle where quotebundle_id=?", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, bundleId);
            ps1.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }

    /**
     * Gets the quote given the quote id.
     *
     * @param quoteId
     * @return the loaded quote
     * @throws EntityNotFoundException
     */
    public Quote getQuote(String quoteId) throws EntityNotFoundException {
        System.out.println("getQuote " + quoteId);
        throw new UnsupportedOperationException();
    }

    public Quote addOrphanQuote(String userId, String title, Reference reference, String quoteText, String quoteBundleId) throws ConstraintViolationException, EntityNotFoundException {
        System.out.println("addOrphanQuote " + userId);
        DocRef document = EntityFactory.get().buildDoc(title, new Date(), true);
        saveDoc(document);

        DocContent docContent = EntityFactory.get().buildDocContent(document.getId(), quoteText);
        saveDocContent(docContent);

        Quote quote = EntityFactory.get().buildQuote(quoteText, document, null, 1, 1);
        quote = addQuoteToBundle(userId, quoteBundleId, quote);
        return quote;
    }


    /**
     * Adds the given quote to the quote bundle identified by the given bundle id.
     *
     * @param userId   needed for creating quote/user binding
     * @param bundleId
     * @param quote
     * @return the persisted quote
     * @throws ConstraintViolationException When the quote doesn't validate
     * @throws EntityNotFoundException      When the bundle can't be found from the
     *                                      given id
     */
    public Quote addQuoteToBundle(String userId, String bundleId, Quote quote) throws ConstraintViolationException,
            EntityNotFoundException {
        if (userId == null || bundleId == null || quote == null) throw new NullPointerException();
        System.out.println("addQuoteToBundle " + userId + " bundleId=" + bundleId + " DocumentId=" + quote.getDocument().getId() + " QuoteId=" + quote.getId());

        try {
            // insert quote
            PreparedStatement ps1 = dao.getPreparedStatement("insert into tw_quote(quote_doc, quote_endpage, quote_quote, quote_serializedmark, quote_startpage, quote_id) values (?,?,?,?,?,?)", Statement.NO_GENERATED_KEYS);
            ps1.setString(1, quote.getDocument().getId());
            ps1.setInt(2, quote.getStartPage());
            ps1.setString(3, quote.getQuote());
            ps1.setString(4, quote.getSerializedMark());
            ps1.setInt(5, quote.getStartPage());
            ps1.setString(6, quote.getId());
            ps1.executeUpdate();

            // add quote to bundle
            PreparedStatement ps2 = dao.getPreparedStatement("insert into tw_bundleitem(bundleitem_quote, bundleitem_quotebundle, bundleitem_id) values (?,?,?)", Statement.NO_GENERATED_KEYS);
            ps2.setString(1, quote.getId());
            ps2.setString(2, bundleId);
            ps2.setString(3, UUID.uuid());
            ps2.executeUpdate();

            // add quote to all bundle
            QuoteBundle all = getAllQuoteBundleForUser(userId);
            if (!all.getId().equals(bundleId)) {
                PreparedStatement ps3 = dao.getPreparedStatement("insert into tw_bundleitem(bundleitem_quote, bundleitem_quotebundle, bundleitem_id) values (?,?,?)", Statement.NO_GENERATED_KEYS);
                ps3.setString(1, quote.getId());
                ps3.setString(2, all.getId());
                ps3.setString(3, UUID.uuid());
                ps3.executeUpdate();
            }


            addQuoteUserBinding(userId, quote.getId());
            return quote;

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }

    /**
     * permanently deletes an existing quote for a given user from all bundles
     * that contain it.
     *
     * @param userId  needed for removing the quote/user binding
     * @param quoteId
     * @throws EntityNotFoundException when the quote isn't found to exist in the
     *                                 bundle
     */
    public void deleteQuote(String userId, String bundleId, String quoteId) throws EntityNotFoundException {
        System.out.println("deleteQuote " + userId);
        QuoteBundle all = getAllQuoteBundleForUser(userId);

        if (all.getId().equals(bundleId)) {
            this.simpleJdbcTemplate.update("delete from tw_quote where quote_id=?", quoteId);
        } else {
            this.simpleJdbcTemplate.update("delete from tw_bundleitem where bundleitem_quote=? and bundleitem_quotebundle=?", quoteId, bundleId);
        }

    }

    /**
     * adds an association of an existing quote to an existing
     * bundle.
     *
     * @param userId
     * @param quoteId        id of the quote to move
     * @param bundleId id of the bundle to which to add the quote
     * @throws EntityNotFoundException When a participating entity is not found
     */
    public void attachQuote(String userId, String quoteId, String bundleId)
            throws EntityNotFoundException {
        if (userId == null || bundleId == null || quoteId == null) throw new NullPointerException();

        this.simpleJdbcTemplate.update("insert into tw_bundleitem(bundleitem_quote, bundleitem_quotebundle, bundleitem_id) values (?,?,?)", quoteId, bundleId, UUID.uuid());

    }

    /**
     * Moves an existing quote from an existing source bundle to an existing
     * target bundle.
     *
     * @param userId
     * @param quoteId        id of the quote to move
     * @param sourceBundleId id of the bundle currently containing the quote
     * @param targetBundleId id of the bundle to which to move the quote
     * @throws EntityNotFoundException When a participating entity is not found
     */
    public void moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId)
            throws EntityNotFoundException {
        this.simpleJdbcTemplate.update("update tw_bundleitem set bundleitem_quotebundle=? where bundleitem_quotebundle=? and bundleitem_quote=?", targetBundleId, sourceBundleId, quoteId);
    }
    /**
     * Adds an association of an existing quote bundle to an existing user.
     *
     * @param userId
     * @param bundleId
     * @throws EntityExistsException if the association already exists
     */
    public void addBundleUserBinding(String userId, String bundleId) throws EntityExistsException {
        System.out.println("addBundleUserBinding " + userId);
        this.simpleJdbcTemplate.update("insert into tw_permission(permission_quotebundle, permission_user, permission_id) values (?,?,?)", bundleId, userId, UUID.uuid());
    }

    /**
     * Removes a user bundle association.
     *
     * @param userId
     * @param bundleId
     * @throws EntityNotFoundException when the association doesn't exist
     */
    public void removeBundleUserBinding(String userId, String bundleId) throws EntityNotFoundException {
        System.out.println("removeBundleUserBinding " + userId);
        this.simpleJdbcTemplate.update("insert into tw_permission(permission_doc, permission_user, permission_id) values (?,?,?)", bundleId, userId);
    }

    /**
     * Adds an association of an existing doc to an existing user.
     *
     * @param userId
     * @param docId
     * @throws EntityExistsException if the association already exists
     */
    public void addDocUserBinding(String userId, String docId) throws EntityExistsException {
        System.out.println("addDocUserBinding " + userId);
        this.simpleJdbcTemplate.update("insert into tw_permission(permission_doc, permission_user, permission_id) values (?,?,?)", docId, userId, UUID.uuid());
    }


    /**
     * Removes a user doc association.
     *
     * @param userId
     * @param docId
     * @throws EntityNotFoundException when the association doesn't exist
     */
    public void removeDocUserBinding(String userId, String docId) throws EntityNotFoundException {
        System.out.println("removeDocUserBinding " + userId + " docId = " + docId);
        this.simpleJdbcTemplate.update("delete from tw_permission where permission_doc=? and permission_user=?", docId, userId);
    }


    /**
     * Returns all user/doc bindings that exist for a given doc
     *
     * @param docId id of the doc
     * @return list of doc user bindings
     */
    public List<DocUserBinding> getDocUserBindingsForDoc(String docId) {
        System.out.println("getDocUserBindingsForDoc " + docId);
        throw new UnsupportedOperationException();
    }


    /**
     * Adds an association of an existing quote to an existing user.
     *
     * @param userId
     * @param quoteId
     * @throws EntityExistsException if the association already exists
     */
    public void addQuoteUserBinding(String userId, String quoteId) throws EntityExistsException {
        System.out.println("addQuoteUserBinding " + userId + " quoteId=" + quoteId);
        this.simpleJdbcTemplate.update("insert into tw_permission(permission_quote, permission_user, permission_id) values (?,?,?)", quoteId, userId, UUID.uuid());

    }

    /**
     * Checks does the specified document available for the user
     *
     * @param userId
     * @param docId
     * @return
     */
    public boolean isDocAvailableForUser(String userId, String docId) {
        System.out.println("isDocAvailableForUser " + userId);
        throw new UnsupportedOperationException();

    }

    /**
     * Checks does the bundle available for the user
     *
     * @param userId
     * @param bundleId
     * @return
     */
    public boolean isBundleAvailableForUser(String userId, String bundleId) {
        System.out.println("isBundleAvailableForUser " + userId);
        throw new UnsupportedOperationException();

    }

    /**
     * Checks does the bundle available for the user
     *
     * @param userId
     * @param quoteId
     * @return true/false
     */
    public boolean isQuoteAvailableForUser(String userId, String quoteId) {
        System.out.println("isQuoteAvailableForUser " + userId);
        throw new UnsupportedOperationException();

    }

    /**
     * Gets all quotes that point to the doc and available for current user having
     * the given doc id.
     *
     * @param docId
     * @param userId
     * @return non-<code>null</code> list of quotes
     */
    public List<Quote> findQuotesByDocForUser(String docId, String userId) {
        System.out.println("findQuotesByDocForUser " + docId);
        throw new UnsupportedOperationException();

    }

    public List<Quote> findQuotesForUser(String userId) {
        System.out.println("findQuotesForUser " + userId);
        throw new UnsupportedOperationException();

    }
    
    /**
     * Creates copy of  quote bundle and quote to bundle links and then  and grants permission for this bundle to specified user
     *
     * @param bundleId
     * @param userId
     * @return the persisted bundle
     */
    
    public QuoteBundle shareBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
        System.out.println("shareBundleForUser " + userId + " | bundleId " + bundle.getId());
        if (userId == null || bundle == null) throw new NullPointerException();
        String newBundleId = UUID.uuid(); 
        try {
            PreparedStatement ps1 = dao.getPreparedStatement("insert into tw_quotebundle(quotebundle_id, quotebundle_name, quotebundle_description, parent_quotebundle) values (?,?,?,?)", Statement.NO_GENERATED_KEYS);
            ps1.setString(1, newBundleId);
            ps1.setString(2, bundle.getName());
            ps1.setString(3, bundle.getDescription());
            ps1.setString(4, bundle.getId());
            ps1.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
        //copy links
        for (Quote quote : getQuotesWithDocRefWithCaseRef(bundle.getId())) {
        	attachQuote(userId, quote.getId(), newBundleId);
        	//add access right
        	addQuoteUserBinding(userId, quote.getId());
        }

        addBundleUserBinding(userId, newBundleId);
        return getQuoteBundle(newBundleId);


    }
    /**
     * Returns list of users whose have access to quotes of selected bundle
     *
     * @param currentUserId currently logged user
     * @param bundleId
     * @return list of users
     */
    
    public List<User> getBundleUsers(String currentUserId, String bundleId) throws ConstraintViolationException {
        System.out.println("getBundleUsers  bundleId " + bundleId);
        List<User> result = new ArrayList<User>();

        try {
/*        	Quote based version
        	String query = "select distinct u.* from tw_bundleitem  fi \n" +
        			"inner join tw_bundleitem oi on fi.bundleitem_quote=oi.bundleitem_quote \n" +
        			"inner join tw_permission p on p.permission_quotebundle = fi.bundleitem_quotebundle \n" +
        			"inner join tw_user u on p.permission_user = u.user_id \n" +
        			"where oi.bundleitem_quotebundle=? \n" +
        			"and fi.bundleitem_quotebundle!=oi.bundleitem_quotebundle \n" +
        			"and p.permission_user != ?\n";
*/        	
        	String query = "select u.* from tw_quotebundle gb\n" +
        			"inner join tw_permission p on p.permission_quotebundle = gb.quotebundle_id \n" +
        			"inner join tw_user u on p.permission_user = u.user_id \n" +
        			"where gb.parent_quotebundle=? \n" + 
        			"and p.permission_user!= ?\n";
            PreparedStatement ps = dao.getPreparedStatement(query, Statement.NO_GENERATED_KEYS);
            ps.setString(1, bundleId);
            ps.setString(2, currentUserId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	result.add(dao.loadUser(rs));
            }
            return result;
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }


    }

	private MapSqlParameterSource createQuoteBundleParameterSource(QuoteBundle bundle) {
		return new MapSqlParameterSource()
			.addValue("quotebundle_id", bundle.getId())
			.addValue("quotebundle_name", bundle.getName())
			.addValue("quotebundle_description", bundle.getDescription());
	}
	
	

}
