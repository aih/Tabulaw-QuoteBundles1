package com.tabulaw.service.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.tabulaw.model.CaseRef;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;
import com.tabulaw.model.UserState;
import com.tabulaw.util.XStreamUtils;

public class ModelRowMapper {

	protected QuoteBundle loadQuoteBundle(ResultSet rs) throws SQLException {
		QuoteBundle ret = new QuoteBundle();
		ret.setId(rs.getString("quotebundle_id"));
		ret.setName(rs.getString("quotebundle_name"));
		ret.setDescription(rs.getString("quotebundle_description"));
		return ret;
	}

	protected DocRef loadDocRefWithCaseRef(ResultSet rs) throws SQLException {
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

	protected DocContent loadDocContent(ResultSet rs) throws SQLException {
		DocContent ret = new DocContent();
		ret.setId(rs.getString("doc_id"));
		ret.setHtmlContent(rs.getString("doc_htmlcontent"));
		ret.setPagesXPath((List<int[]>) XStreamUtils.fromXML(rs.getString("doc_pagesxpath")));
		ret.setFirstPageNumber(rs.getInt("doc_firstpagenumber"));
		return ret;
	}

	protected UserState loadUserState(ResultSet rs) throws SQLException {
		UserState ret = new UserState();
		ret.setId(rs.getString("userstate_id"));
		String currentQuoteBundleId = rs.getString("userstate_quotebundle");
		if (currentQuoteBundleId != null) {
			ret.setCurrentQuoteBundleId(currentQuoteBundleId);
		}
		ret.setUserId(rs.getString("userstate_user"));
		ret.setAllQuoteBundleId(rs.getString("userstate_allquotebundle"));
		return ret;
	}

	protected Quote loadQuote(ResultSet rs) throws SQLException {
		Quote ret = new Quote();
		ret.setId(rs.getString("quote_id"));
		ret.setEndPage(rs.getInt("quote_endpage"));
		DocRef doc = loadDocRefWithCaseRef(rs);
		ret.setDocument(doc);
		ret.setQuote(rs.getString("quote_quote"));
		ret.setSerializedMark(rs.getString("quote_serializedmark"));
		ret.setStartPage(rs.getInt("quote_startpage"));
		return ret;
	}

	protected User loadUser(ResultSet rs) throws SQLException {
		User ret = new User();
		ret.setId(rs.getString("user_id"));
		// skip AppFeatures
		ret.setEmailAddress(rs.getString("user_emailaddress"));
		ret.setEnabled(rs.getBoolean("user_enabled"));
		ret.setExpires(rs.getDate("user_expires"));
		ret.setLocked(rs.getBoolean("user_locked"));
		ret.setName(rs.getString("user_name"));
		// skip OAuthParameters
		// skip OAuthParametersExtra
		ret.setPassword(rs.getString("user_password"));
		ret.setRoles((Collection) XStreamUtils.fromXML(rs.getString("user_roles")));

		return ret;
	}
}
