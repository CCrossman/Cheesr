package com.crossman;

import com.crossman.v1.Address;
import com.crossman.v1.AddressResultSetHandler;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;
import java.util.List;

public final class ProfilePage extends CheesrPage implements IRequireAuthorization {
	// used only by PropertyModel for DropDownChoice
	private String addressType = "HOME";

	public ProfilePage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		try (Connection c = WicketApplication.getInjector().getInstance(Sql2o.class).open()) {
			final List<Address> addresses = c.createQuery("SELECT kind, name, street, city, state, zip from addresses where username = :usr")
					.addParameter("usr", getCheesrSession().getUser().getName())
					.executeAndFetch(AddressResultSetHandler.instance);

			ListView listView = new ListView("addresses", addresses) {
				@Override
				protected void populateItem(ListItem item) {
					Address address = (Address) item.getModelObject();
					item.add(new Label("kind", address.getKind().name()));
					item.add(new Label("name", address.getName()));
					item.add(new Label("street", address.getStreet()));
					item.add(new Label("city", address.getCity()));
					item.add(new Label("state", address.getState()));
					item.add(new Label("zip", address.getZip()));
				}
			};
			add(listView);
		}

		final TextField tfName = new TextField("name", new Model());
		final TextField tfStreet = new TextField("street", new Model());
		final TextField tfCity = new TextField("city", new Model());
		final TextField tfState = new TextField("state", new Model());
		final TextField tfZip = new TextField("zip", new Model());

		final DropDownChoice ddc = new DropDownChoice("choices", new PropertyModel(this, "addressType"), new LoadableDetachableModel() {
			@Override
			protected Object load() {
				return Arrays.asList(Address.Type.values());
			}
		});

		final Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				// save address in database
				try (Connection c = WicketApplication.getInjector().getInstance(Sql2o.class).open()) {
					final String name = tfName.getModelObjectAsString();
					final String street = tfStreet.getModelObjectAsString();
					final String city = tfCity.getModelObjectAsString();
					final String state = tfState.getModelObjectAsString();
					final String zip = tfZip.getModelObjectAsString();

					c.createQuery("INSERT INTO addresses (username, kind, name, street, city, state, zip) VALUES (:usr, :kind, :name, :street, :city, :state, :zip) ON CONFLICT (username, kind) DO UPDATE SET name = :name, street = :street, city = :city, state = :state, zip = :zip")
								.addParameter("usr", getCheesrSession().getUser().getName())
								.addParameter("name", name)
								.addParameter("kind", addressType)
								.addParameter("street", street)
								.addParameter("city", city)
								.addParameter("state", state)
								.addParameter("zip", zip)
								.executeUpdate();
				}

				// return to front page
				PageParameters pp = new PageParameters();
				pp.add("message", "Updated billing address");
				setResponsePage(HomePage.class, pp);
			}
		};
		add(form);

		form.add(tfName.setRequired(true));
		form.add(tfStreet.setRequired(true));
		form.add(tfCity.setRequired(true));
		form.add(tfState.setRequired(true));
		form.add(tfZip.setRequired(true));
		form.add(ddc.setRequired(true));

		form.add(new Link("cancel") {
			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		});

		add(new PageNavigatorPanel("pageNavigator"));
	}
}
