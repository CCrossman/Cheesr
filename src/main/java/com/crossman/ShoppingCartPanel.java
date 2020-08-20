package com.crossman;

import com.crossman.v2.CheesrProduct;
import lombok.Getter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.NumberFormat;

public class ShoppingCartPanel extends Panel {
	@Getter
	private final Cart cart;

	public ShoppingCartPanel(String id, Cart cart) {
		super(id);
		this.cart = cart;

		add(new ListView("cart", new PropertyModel(this, "cart.products")) {
			@Override
			protected void populateItem(ListItem item) {
				CheesrProduct cheese = (CheesrProduct) item.getModelObject();
				item.add(new Label("name", cheese.getName()));
				// TODO: type
				item.add(new Label("price", "$" + cheese.getPrice()));
				item.add(removeLink("remove", item));
			}
		});

		add(new Label("total", new Model() {
			@Override
			public Object getObject() {
				NumberFormat nf = NumberFormat.getCurrencyInstance();
				return nf.format(getCart().getTotal());
			}
		}));
	}
}
