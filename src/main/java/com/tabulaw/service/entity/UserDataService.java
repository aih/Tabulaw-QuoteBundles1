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

import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import com.google.inject.Inject;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.*;
import com.tabulaw.service.sanitizer.ISanitizer;
import com.tabulaw.util.UUID;

/**
 * Manages the persistence of user related data that is not part of the user
 * entity.
 *
 * @author jpk
 */
public class UserDataService {

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

    /**
     * Constructor
     *
     * @param validationFactory
     */
    @Inject
    public UserDataService(ValidatorFactory validationFactory, ISanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }


    /**
     * Gets a list of all docs for a given user.
     *
     * @param userId user id
     * @return list of docs
     */
    public List<DocRef> getDocsForUser(String userId) {
        System.out.println("getDocsForUser " + userId);
        if (userId == null) throw new NullPointerException();
        List<DocRef> ret = new ArrayList<DocRef>();

        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id, tw_permission where permission_doc=doc_id AND permission_user=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ret.add(dao.loadDocRefWithCaseRef(rs));
            }
            return ret;
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }

    /**
     * Provides a list of all doc refs in the system.
     *
     * @return doc list
     */
    public List<DocRef> getAllDocs() {
        System.out.println("getAllDocs");
        List<DocRef> ret = new ArrayList<DocRef>();

        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id", Statement.NO_GENERATED_KEYS);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ret.add(dao.loadDocRefWithCaseRef(rs));
            }
            return ret;
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    /**
     * Gets the doc ref given the doc id.
     *
     * @param docId
     * @return to loaded doc ref
     * @throws EntityNotFoundException
     */

    public DocRef getDoc(String docId) throws EntityNotFoundException {
        System.out.println("getDoc " + docId);
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_doc left outer join tw_caseref on doc_caseref=caseref_id where doc_id=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, docId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return dao.loadDocRefWithCaseRef(rs);
            }
            throw new EntityNotFoundException("No document found with id: '" + docId);

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

   /**
     * Gets the doc <em>content</em> given the doc id.
     *
     * @param docId {@link DocRef} id
     * @return to loaded doc content
     * @throws EntityNotFoundException
     */
    public DocContent getDocContent(String docId) throws EntityNotFoundException {
        System.out.println("getDocContent " + docId);
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_doc where doc_id=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, docId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return dao.loadDocContent(rs);
            }
            throw new EntityNotFoundException("No document found with id: '" + docId);

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }


    /**
     * Gets the user state for the given user id
     *
     * @param userId
     * @return the user's state entity
     * @throws EntityNotFoundException
     */
    public UserState getUserState(String userId) throws EntityNotFoundException {
        System.out.println("getUserState " + userId);
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_userstate where userstate_user=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return dao.loadUserState(rs);
            }
            throw new EntityNotFoundException("No user state with user id: '" + userId + "' was found.");

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }


    /**
     * Saves user state.
     *
     * @param userState
     * @throws EntityExistsException
     */
    public void saveUserState(UserState userState) throws EntityExistsException {
        System.out.println("saveUserState id:" + userState.getId() + " | bundleId" + userState.getCurrentQuoteBundleId() + " | user id: " + userState.getUserId());
        if (userState == null) throw new NullPointerException();

        Dao dao = new Dao();
        
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_userstate where userstate_id=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, userState.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
	            PreparedStatement ps1 = dao.getPreparedStatement("update tw_userstate set userstate_quotebundle=?, userstate_allquotebundle=?  where userstate_id=?", Statement.NO_GENERATED_KEYS);
	            ps1.setString(1, userState.getCurrentQuoteBundleId());
	            ps1.setString(2, userState.getAllQuoteBundleId());
	            ps1.setString(3, userState.getId());
	            ps1.executeUpdate();
            } else {
	            PreparedStatement ps1 = dao.getPreparedStatement("insert into tw_userstate(userstate_quotebundle, userstate_allquotebundle, userstate_user, userstate_id) values (?,?,?,?)", Statement.NO_GENERATED_KEYS);
	            ps1.setString(1, userState.getCurrentQuoteBundleId());
	            ps1.setString(2, userState.getAllQuoteBundleId());
	            ps1.setString(3, userState.getUserId());
	            ps1.setString(4, UUID.uuid());
	            ps1.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
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
    public QuoteBundle getAllQuoteBundleForUser(String userId) {
        System.out.println("getAllQuoteBundleForUser " + userId);
        if (userId == null) throw new NullPointerException();

        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_quotebundle, tw_userstate where userstate_allquotebundle=quotebundle_id AND userstate_user=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                QuoteBundle qb = dao.loadQuoteBundle(rs);
                qb.setQuotes(getQuotesWithDocRefWithCaseRef(qb.getId()));
                System.out.println("ALL BUNDLE ID:"+qb.getId());
                return qb;
            } else {
                // create all quote bundle container

                QuoteBundle oqc = new QuoteBundle();
                oqc.setId(UUID.uuid());
                oqc.setName("All Quotes");
                oqc.setDescription("All quotes stored there");
                addBundleForUser(userId, oqc);

                UserState us = null;
                
                try {
                	us = getUserState(userId);
                } catch(EntityNotFoundException enf){
                    us = new UserState();
                    us.setId(UUID.uuid());
                    us.setUserId(userId);
                }
                us.setAllQuoteBundleId(oqc.getId());
                saveUserState(us);
                System.out.println("ALL BUNDLE ID:"+oqc.getId());
                return oqc;

            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    private List<Quote> getQuotesWithDocRefWithCaseRef(String bundleId)
    {
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_quote, tw_doc left outer join tw_caseref on doc_caseref=caseref_id, tw_bundleitem where quote_doc=doc_id and bundleitem_quote=quote_id and bundleitem_quotebundle=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, bundleId);
            ResultSet rs = ps.executeQuery();

            List<Quote> list = new ArrayList<Quote>();
            while (rs.next()) {
                Quote quote = dao.loadQuoteWithDocRefWithCaseRef(rs);
                list.add(quote);
            }

            return list;
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    /**
     * Gets all bundles for a given user.
     * <p/>
     * Auto-creates an all quote bundle if one doesn't exist for the user.
     *
     * @param userId
     * @return list of quote bundles
     */
    public BundleContainer getBundlesForUser(String userId) {
        System.out.println("getBundlesForUser " + userId);

        // first ensure an all quotes container exists for user
        getAllQuoteBundleForUser(userId);

        Dao dao = new Dao();
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

        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_quotebundle where quotebundle_id=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, bundleId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                QuoteBundle qb = dao.loadQuoteBundle(rs);
                qb.setQuotes(getQuotesWithDocRefWithCaseRef(qb.getId()));
                return qb;
            } else {
                throw new EntityNotFoundException("getQuoteBundle " + bundleId);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
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
    public void updateBundlePropsForUser(String userId, QuoteBundle bundle) throws IllegalArgumentException,
            ConstraintViolationException, EntityNotFoundException {
        if (userId == null || bundle == null) throw new NullPointerException();
        System.out.println("updateBundlePropsForUser " + userId + " bundleId=" + bundle.getId());

        Dao dao = new Dao();
        try {
            PreparedStatement ps1 = dao.getPreparedStatement("update tw_quotebundle set quotebundle_name=?, quotebundle_description=? where quotebundle_id=?", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, bundle.getName());
            ps1.setString(2, bundle.getDescription());
            ps1.setString(3, bundle.getId());

            ps1.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

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

        Dao dao = new Dao();
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

        Dao dao = new Dao();
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

        Dao dao = new Dao();

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

        Dao dao = new Dao();
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
        Dao dao = new Dao();
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

        Dao dao = new Dao();

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

        Dao dao = new Dao();
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

        Dao dao = new Dao();
        try {
            if (all.getId().equals(bundleId)) {
                // Delete from all means delete quote
                PreparedStatement ps2 = dao.getPreparedStatement("delete from tw_quote where quote_id=?", Statement.RETURN_GENERATED_KEYS);
                ps2.setString(1, quoteId);
                ps2.executeUpdate();
            } else {
                PreparedStatement ps2 = dao.getPreparedStatement("delete from tw_bundleitem where bundleitem_quote=? and bundleitem_quotebundle=?", Statement.RETURN_GENERATED_KEYS);
                ps2.setString(1, quoteId);
                ps2.setString(2, bundleId);
                ps2.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

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

        Dao dao = new Dao();
        try {

            PreparedStatement ps2 = dao.getPreparedStatement("update tw_bundleitem set bundleitem_quotebundle=? where bundleitem_quotebundle=? and bundleitem_quote=?", Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, targetBundleId);
            ps2.setString(2, sourceBundleId);
            ps2.setString(3, quoteId);
            ps2.executeUpdate();

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

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
        Dao dao = new Dao();
        try {
            PreparedStatement ps2 = dao.getPreparedStatement("insert into tw_permission(permission_quotebundle, permission_user, permission_id) values (?,?,?)", Statement.NO_GENERATED_KEYS);
            ps2.setString(1, bundleId);
            ps2.setString(2, userId);
            ps2.setString(3, UUID.uuid());
            ps2.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
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
        Dao dao = new Dao();

        try {
            PreparedStatement ps1 = dao.getPreparedStatement("delete from tw_permission where permission_quotebundle=? and permission_user=?", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, bundleId);
            ps1.setString(2, userId);
            ps1.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

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
        Dao dao = new Dao();
        try {
            PreparedStatement ps2 = dao.getPreparedStatement("insert into tw_permission(permission_doc, permission_user, permission_id) values (?,?,?)", Statement.NO_GENERATED_KEYS);
            ps2.setString(1, docId);
            ps2.setString(2, userId);
            ps2.setString(3, UUID.uuid());
            ps2.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
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
        Dao dao = new Dao();
        try {
            PreparedStatement ps2 = dao.getPreparedStatement("delete from tw_permission where permission_doc=? and permission_user=?", Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, docId);
            ps2.setString(2, userId);
            ps2.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
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
        Dao dao = new Dao();
        try {
            PreparedStatement ps2 = dao.getPreparedStatement("insert into tw_permission(permission_quote, permission_user, permission_id) values (?,?,?)", Statement.NO_GENERATED_KEYS);
            ps2.setString(1, quoteId);
            ps2.setString(2, userId);
            ps2.setString(3, UUID.uuid());
            ps2.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
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

}
