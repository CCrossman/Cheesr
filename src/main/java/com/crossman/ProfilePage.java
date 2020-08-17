package com.crossman;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public final class ProfilePage extends CheesrPage implements IRequireAuthorization {

	public ProfilePage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		final TextField tfName = new TextField("name", new Model());
		final TextField tfStreet = new TextField("street", new Model());
		final TextField tfCity = new TextField("city", new Model());
		final TextField tfState = new TextField("state", new Model());
		final TextField tfZip = new TextField("zip", new Model());

		Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				// save address in database
				final Sql2o sql2o = WicketApplication.getInjector().getInstance(Sql2o.class);
				try (Connection c = sql2o.open()) {
					long count = c.createQuery("SELECT count(*) FROM addresses WHERE username = :usr AND kind = 'HOME'")
							.addParameter("usr", getCheesrSession().getUsername())
							.executeScalar(Long.class);

					final String name = tfName.getModelObjectAsString();
					final String street = tfStreet.getModelObjectAsString();
					final String city = tfCity.getModelObjectAsString();
					final String state = tfState.getModelObjectAsString();
					final String zip = tfZip.getModelObjectAsString();

					if (count == 0) {
						c.createQuery("INSERT INTO addresses (username, kind, name, street, city, state, zip) VALUES (:usr, 'HOME', :name, :street, :city, :state, :zip)")
								.addParameter("usr", getCheesrSession().getUsername())
								.addParameter("name", name)
								.addParameter("street", street)
								.addParameter("city", city)
								.addParameter("state", state)
								.addParameter("zip", zip)
								.executeUpdate();
					} else {
						c.createQuery("UPDATE addresses SET name = :name, street = :street, city = :city, state = :state, zip = :zip WHERE username = :usr AND kind = 'HOME'")
								.addParameter("usr", getCheesrSession().getUsername())
								.addParameter("name", name)
								.addParameter("street", street)
								.addParameter("city", city)
								.addParameter("state", state)
								.addParameter("zip", zip)
								.executeUpdate();
					}
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

		form.add(new Link("cancel") {
			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		});
	}
}
