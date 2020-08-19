package com.crossman;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class LoginPage extends CheesrPage {

	public LoginPage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		final TextField usr = new TextField("usr", new Model());
		final TextField pwd = new PasswordTextField("pwd", new Model());

		Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				super.onSubmit();

				final Encryptor encryptor = WicketApplication.getInjector().getInstance(Encryptor.class);
				final Sql2o sql2o = WicketApplication.getInjector().getInstance(Sql2o.class);

				final String username = usr.getModelObjectAsString();
				try (Connection c = sql2o.open()) {
					final String password = encryptor.encrypt(pwd.getModelObjectAsString());
					long count = c.createQuery("SELECT count(*) FROM users WHERE username = :usr AND password = :pwd")
							.addParameter("usr", username)
							.addParameter("pwd", password)
							.executeScalar(Long.class);

					if (count == 0) {
						PageParameters pp = new PageParameters();
						pp.add("message", "User '" + username + "' not found or incorrect password");
						setResponsePage(LoginPage.class, pp);
					} else {
						getCheesrSession().setUsername(username);
						setResponsePage(HomePage.class);
					}
				} catch (Exception ex) {
					PageParameters pp = new PageParameters();
					pp.add("error", ex.getMessage());
					setResponsePage(LoginPage.class, pp);
				}
			}
		};
		form.add(usr);
		form.add(pwd);
		add(form);

		add(new PageNavigatorPanel("pageNavigator"));
	}
}
