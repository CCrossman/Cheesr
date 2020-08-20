package com.crossman;

import com.crossman.v1.Cheese;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

import java.util.List;

public abstract class CheesrPage extends WebPage {
	protected CheesrPage(PageParameters pageParameters) {
		if (pageParameters.containsKey("message")) {
			info(pageParameters.getString("message"));
		}
		if (pageParameters.containsKey("error")) {
			error(pageParameters.getString("error"));
		}
	}

	public CheesrSession getCheesrSession() {
		return (CheesrSession) getSession();
	}

	public Cart getCart() {
		return getCheesrSession().getCart();
	}

	public List<Cheese> getCheeses() {
		return WicketApplication.get().getCheeses();
	}
}
