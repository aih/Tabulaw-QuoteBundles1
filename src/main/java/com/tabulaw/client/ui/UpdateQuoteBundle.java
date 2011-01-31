package com.tabulaw.client.ui;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.QuoteBundle;

public class UpdateQuoteBundle {

	public static void updateQuoteBundleTitle() {
		updateQuoteBundleTitle(ClientModelCache.get().getCurrentQuoteBundle());
	}

	public static void updateQuoteBundleTitle(QuoteBundle bundle) {
		if (bundle == null) {
			return;
		}
		String oldValue = bundle.getName();
		String newValue = getNewQuoteBundleTitle(bundle);
		if (!oldValue.equals(newValue)) {
			bundle.setName(newValue);
			bundle.setDescription("Quote Bundle for " + newValue);
		}
		ClientModelCache.get().persist(bundle, null);
		ServerPersistApi.get().updateBundleProps(bundle);
	}

	@SuppressWarnings("unchecked")
	private static String getNewQuoteBundleTitle(QuoteBundle bundle) {
		return getNewQuoteBundleTitle((List<QuoteBundle>) ClientModelCache
				.get().getAll(EntityType.QUOTE_BUNDLE), bundle.getName());
	}

	private static String getNewQuoteBundleTitle(List<QuoteBundle> qbs,
			String title) {
		while (true) {
			if (title.toLowerCase().startsWith("untitled")) {
				title = prompt(
						"Please give this Quote Bundle a title",
						title);
			} else if (title.isEmpty()) {
				title = prompt(
						"Title cannot be empty. Please give this Quote Bundle a title",
						title);
			} else if (alreadyQuoteBundleTitleExist(qbs, title)) {
				title = prompt(
						"Title already exists. Please give this Quote Bundle a title",
						title);
			} else {
				return title;
			}
		}
	}

	private static boolean alreadyQuoteBundleTitleExist(List<QuoteBundle> qbs,
			String title) {
		for (QuoteBundle qb : qbs) {
			if (qb.getDescription().equals(title)) {
				return true;
			}
		}
		return false;
	}

	private static String prompt(String description, String value) {
		return onPrompt(Window.prompt(description, value), value);
	}

	private static String onPrompt(String newValue, String oldValue) {
		if (newValue == null) {
			return oldValue;
		} else {
			return newValue;
		}
	}
}
