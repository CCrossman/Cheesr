package com.crossman;

import com.crossman.v2.CheesrProduct;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Homepage
 */
public class HomePage extends CheesrPage implements IRequireAuthorization {

	public HomePage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		PageableListView cheeses = new PageableListView("products", getProducts(), 5) {
			@Override
			protected void populateItem(ListItem item) {
				CheesrProduct product = (CheesrProduct)item.getModelObject();
				item.add(new Label("name", product.getName()));
				item.add(new Label("type", product.getType().name()));
				item.add(new Label("price", "$" + product.getPrice()));
				item.add(new Link("add", item.getModel()) {
					@Override
					public void onClick() {
						CheesrProduct selected = (CheesrProduct)getModelObject();
						getCart().getProducts().add(selected);
					}
				});
			}
		};
		add(cheeses);
		add(new PagingNavigator("navigator", cheeses));
		add(new ShoppingCartPanel("shoppingcart", getCart()));
		add(new Link("checkout") {
			@Override
			public void onClick() {
				HomePage.this.setResponsePage(CheckoutPage.class);
			}

			@Override
			public boolean isVisible() {
				return !getCart().getProducts().isEmpty();
			}
		});
		add(new PageNavigatorPanel("pageNavigator"));
	}
}
