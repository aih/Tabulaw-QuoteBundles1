/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.service.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
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
import com.tabulaw.util.XStreamUtils;

/**
 * Manages the persistence of user related data that is not part of the user
 * entity.
 *
 * @author jpk
 */
public class UserDataService {
	private class QuoteBundleRowMapper extends ModelRowMapper implements ParameterizedRowMapper<QuoteBundle> {

		public QuoteBundle mapRow(ResultSet rs, int rownum) throws SQLException {
            return loadQuoteBundle(rs);
		}
	}
	
	private class DocRefWithCaseRefRowMapper extends ModelRowMapper implements ParameterizedRowMapper<DocRef> {

		public DocRef mapRow(ResultSet rs, int rownum) throws SQLException {
			return loadDocRefWithCaseRef(rs);
		}
	}
	
	private class DocContentRowMapper extends ModelRowMapper implements ParameterizedRowMapper<DocContent> {

		public DocContent mapRow(ResultSet rs, int rownum) throws SQLException {
            return loadDocContent(rs);
        }
    }
	
	private class UserStateRowMapper extends ModelRowMapper implements ParameterizedRowMapper<UserState> {

		public UserState mapRow(ResultSet rs, int rownum) throws SQLException {
            return loadUserState(rs);
        }
    }
	
	private class QuoteRowMapper extends ModelRowMapper implements ParameterizedRowMapper<Quote> {

		public Quote mapRow(ResultSet rs, int rownum) throws SQLException {
            return loadQuote(rs);
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
    

    /**
     * Constructor
     *
     * @param validationFactory
     */
    @Inject
    public UserDataService(ValidatorFactory validationFactory, ISanitizer sanitizer, DataSource ds) {
        this.sanitizer = sanitizer;
        
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(ds);
		
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
		try {
			DocRef doc = this.simpleJdbcTemplate.queryForObject(
					"select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id where doc_id=?",
					new DocRefWithCaseRefRowMapper(), docId);
			return doc;
		} catch (EmptyResultDataAccessException erd) {
			throw new EntityNotFoundException("No document found with id: '" + docId, erd);
		}
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
		try {
			System.out.println("getDocContent " + docId);
			DocContent content = this.simpleJdbcTemplate.queryForObject("select * from tw_doc where doc_id=?",
					new DocContentRowMapper(), docId);
			return content;
		} catch (EmptyResultDataAccessException erd) {
			throw new EntityNotFoundException("No document found with id: '" + docId, erd);
		}

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
		try {
			UserState userState = this.simpleJdbcTemplate.queryForObject(
					"select * from tw_userstate where userstate_user=?", new UserStateRowMapper(), userId);
			return userState;
		} catch (EmptyResultDataAccessException erd) {
			throw new EntityNotFoundException("No user state with user id: '" + userId + "' was found.", erd);
		}
	}

    /**
     * Saves user state.
     *
     * @param userState
     * @throws EntityExistsException
     */
	@Transactional
	public void saveUserState(UserState userState) throws EntityExistsException {
		System.out.println("saveUserState id:" + userState.getId() + " | bundleId"
				+ userState.getCurrentQuoteBundleId() + " | user id: " + userState.getUserId());
		if (userState == null)
			throw new NullPointerException();

		try {
			getUserState(userState.getUserId());
			this.simpleJdbcTemplate
					.update(
							"update tw_userstate set userstate_quotebundle=?, userstate_allquotebundle=?  where userstate_id=?",
							userState.getCurrentQuoteBundleId(), userState.getAllQuoteBundleId(), userState.getId());
		} catch (EntityNotFoundException enfe) {
			//TODO Check insert operation
			this.simpleJdbcTemplate
					.update(
							"insert into tw_userstate(" +
							"userstate_quotebundle, " +
							"userstate_allquotebundle, " +
							"userstate_user, " +
							"userstate_id) values (?,?,?,?)",
							userState.getCurrentQuoteBundleId()
							, userState.getAllQuoteBundleId()
							, userState.getUserId(),
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

		try {
			QuoteBundle qb = this.simpleJdbcTemplate
					.queryForObject(
							"select * from tw_quotebundle, tw_userstate where userstate_allquotebundle=quotebundle_id AND userstate_user=?",
							new QuoteBundleRowMapper(), userId);
			qb.setQuotes(getQuotesWithDocRefWithCaseRef(qb.getId()));
			System.out.println("ALL BUNDLE ID:" + qb.getId());
			return qb;
		} catch (EmptyResultDataAccessException erd) {
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

        List<QuoteBundle> list = this.simpleJdbcTemplate.query(
        		"select * from tw_quotebundle, tw_permission " +
        		"where " +
        		"	permission_quotebundle=quotebundle_id " +
        		"	AND permission_user=? " +
        		"order by quotebundle_name",
				new QuoteBundleRowMapper(), userId);        

        //add quotes to each bundle
        for (QuoteBundle bundle : list) {
        	bundle.setQuotes(getQuotesWithDocRefWithCaseRef(bundle.getId()));
        	
        }

        return new BundleContainer(list);
    }

    /**
     * Gets the quote bundle given the bundle id.
     *
     * @param bundleId
     * @return
     * @throws EntityNotFoundException
     */
	@Transactional
	public QuoteBundle getQuoteBundle(String bundleId) throws EntityNotFoundException {
		System.out.println("getQuoteBundle " + bundleId);

		if (bundleId == null)
			throw new NullPointerException();

		try {
			QuoteBundle qb = this.simpleJdbcTemplate.queryForObject(
					"select * from tw_quotebundle where quotebundle_id=?", new QuoteBundleRowMapper(), bundleId);
			qb.setQuotes(getQuotesWithDocRefWithCaseRef(qb.getId()));
			return qb;
		} catch (EmptyResultDataAccessException erd) {
			throw new EntityNotFoundException("getQuoteBundle " + bundleId, erd);
		}
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
	@Transactional
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
	@Transactional
    public DocRef saveDoc(DocRef doc) throws ConstraintViolationException {
        System.out.println("saveDoc id=" + doc.getId());
        if (doc == null) throw new NullPointerException();

        Reference ref = doc.getReference();
        String referenceId=null;

        if (ref!=null && ref instanceof CaseRef) {
            CaseRef caseRef = (CaseRef)ref;
            caseRef.setId(UUID.uuid());
            referenceId = caseRef.getId();

            this.simpleJdbcTemplate.update("insert into tw_caseref(caseref_id, caseref_court, caseref_docloc, caseref_firstpagenumber, caseref_lastpagenumber, caseref_parties, caseref_reftoken, caseref_url, caseref_year) values (?,?,?,?,?,?,?,?,?)"
            		, caseRef.getId()
            		, caseRef.getCourt()
            		, caseRef.getDocLoc()
            		, caseRef.getFirstPageNumber()
            		, caseRef.getLastPageNumber()
            		, caseRef.getParties()
            		, caseRef.getReftoken()
            		, caseRef.getUrl()
            		, caseRef.getYear()
            		);
        }

        doc.setId(UUID.uuid()) ;
        this.simpleJdbcTemplate.update("insert into tw_doc(doc_id, doc_caseref, doc_title, doc_date, doc_referencedoc) values (?,?,?,?,?)"
        		, doc.getId()
        		, doc.getReference()!=null ? referenceId : null
        		, doc.getTitle()
        		, new java.sql.Date(doc.getDate().getTime())
        		, doc.isReferenceDoc()
        		);

        return doc;

    }

	@Transactional
    public void saveDocContent(DocContent docContent) throws ConstraintViolationException {
        System.out.println("saveDocContent id:" + docContent.getId());

        this.simpleJdbcTemplate.update("update tw_doc set doc_htmlcontent=?, doc_firstpagenumber=?, doc_pagesxpath=? where doc_id=?"
        		, docContent.getHtmlContent()
        		, docContent.getFirstPageNumber()
        		, XStreamUtils.toXML(docContent.getPagesXPath())
        		, docContent.getId()
        		);
        

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
	@Transactional
    public void deleteDoc(String docId) throws EntityNotFoundException {
        System.out.println("deleteDoc " + docId);
        this.simpleJdbcTemplate.update("delete from tw_doc where doc_id=?", docId);
    }

    /**
     * Finds a case type doc by its remote url property.
     *
     * @param remoteUrl the unique remote url
     * @return the found doc
     * @throws EntityNotFoundException
     */
	@Transactional
    public DocRef findCaseDocByRemoteUrl(String remoteUrl) throws EntityNotFoundException {
        System.out.println("findCaseDocByRemoteUrl " + remoteUrl);
        try {
	        DocRef result = this.simpleJdbcTemplate
			.queryForObject(
					"select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id where caseref_url=?",
					new DocRefWithCaseRefRowMapper(), remoteUrl);
	        return result;
        } catch (EmptyResultDataAccessException erd){
            throw new EntityNotFoundException("remoteUrl "+remoteUrl, erd);
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
	@Transactional
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
	@Transactional
    public QuoteBundle addBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
        System.out.println("addBundleForUser " + userId + " | bundleId " + bundle.getId());
        if (userId == null || bundle == null) throw new NullPointerException();

        this.simpleJdbcTemplate.update("insert into tw_quotebundle(quotebundle_id, quotebundle_name, quotebundle_description) values (?,?,?)"
        		, bundle.getId()
        		, bundle.getName()
        		, bundle.getDescription()
        		);

        addBundleUserBinding(userId, bundle.getId());
        return bundle;
        
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
	@Transactional
    public void deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes) throws EntityNotFoundException {
        System.out.println("deleteBundleForUser " + userId);

        if (!deleteQuotes) {
            QuoteBundle oqb = getAllQuoteBundleForUser(userId);

            this.simpleJdbcTemplate.update("update tw_bundleitem set bundleitem_quotebundle=? where bundleitem_quotebundle=?"
            		, oqb.getId(), bundleId);

            
        }
        this.simpleJdbcTemplate.update("delete from tw_quotebundle where quotebundle_id=?", bundleId);


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

	@Transactional
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
	@Transactional
    public Quote addQuoteToBundle(String userId, String bundleId, Quote quote) throws ConstraintViolationException,
            EntityNotFoundException {
        if (userId == null || bundleId == null || quote == null) throw new NullPointerException();
        System.out.println("addQuoteToBundle " + userId + " bundleId=" + bundleId + " DocumentId=" + quote.getDocument().getId() + " QuoteId=" + quote.getId());

	    // insert quote
	    this.simpleJdbcTemplate.update("insert into tw_quote(quote_doc, quote_endpage, quote_quote, quote_serializedmark, quote_startpage, quote_id) values (?,?,?,?,?,?)"
	    		, quote.getDocument().getId()
	    		, quote.getStartPage()
	    		, quote.getQuote()
	    		, quote.getSerializedMark()
	    		, quote.getStartPage()
	    		, quote.getId()
	    		);
		
	
	    // add quote to bundle
	    attachQuote(userId, quote.getId(), bundleId);
	    
	
	    // add quote to all bundle
	    QuoteBundle all = getAllQuoteBundleForUser(userId);
	    if (!all.getId().equals(bundleId)) {
	        attachQuote(userId, quote.getId(), all.getId());
	    }
	
	
	    addQuoteUserBinding(userId, quote.getId());
	    return quote;
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
	@Transactional
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
	@Transactional
    public void attachQuote(String userId, String quoteId, String bundleId)
            throws EntityNotFoundException {
        if (userId == null || bundleId == null || quoteId == null) throw new NullPointerException();

        this.simpleJdbcTemplate.update("insert into tw_bundleitem(bundleitem_quote, bundleitem_quotebundle, bundleitem_id) values (?,?,?)"
        								, quoteId
        								, bundleId
        								, UUID.uuid());

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
	@Transactional
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
	@Transactional
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
	@Transactional
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
	@Transactional
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
	@Transactional
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
	@Transactional
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
	@Transactional
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
	@Transactional
    public List<Quote> findQuotesByDocForUser(String docId, String userId) {
        System.out.println("findQuotesByDocForUser " + docId);
        throw new UnsupportedOperationException();

    }

	@Transactional
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
    
	@Transactional
    public QuoteBundle shareBundleForUser(String userId, QuoteBundle bundle) throws ConstraintViolationException {
        System.out.println("shareBundleForUser " + userId + " | bundleId " + bundle.getId());
        if (userId == null || bundle == null) throw new NullPointerException();
        String newBundleId = UUID.uuid();
        System.out.println("bundleid= " + newBundleId);
        
        userId = "some wrong id";

        //create copy of bundle
	    this.simpleJdbcTemplate.update("insert into tw_quotebundle(quotebundle_id, quotebundle_name, quotebundle_description, parent_quotebundle) values (?,?,?,?)"
	    		, newBundleId
	    		, bundle.getName()
	    		, bundle.getDescription()
	    		, bundle.getId()
	    		);
        
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
    
	@Transactional
    public List<User> getBundleUsers(String currentUserId, String bundleId) throws ConstraintViolationException {
        System.out.println("getBundleUsers  bundleId " + bundleId);
        List<User> result = this.simpleJdbcTemplate.query(
    			"select u.* from tw_quotebundle gb\n" +
    			"inner join tw_permission p on p.permission_quotebundle = gb.quotebundle_id \n" +
    			"inner join tw_user u on p.permission_user = u.user_id \n" +
    			"where gb.parent_quotebundle=? \n" + 
    			"and p.permission_user!= ?\n",
				new UserRowMapper(), bundleId, currentUserId);        

        return result;
    }

	private MapSqlParameterSource createQuoteBundleParameterSource(QuoteBundle bundle) {
		return new MapSqlParameterSource()
			.addValue("quotebundle_id", bundle.getId())
			.addValue("quotebundle_name", bundle.getName())
			.addValue("quotebundle_description", bundle.getDescription());
	}
	
	

}
