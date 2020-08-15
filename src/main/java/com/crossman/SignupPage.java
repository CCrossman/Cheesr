package com.crossman;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class SignupPage extends WebPage {

	public SignupPage() {
		final TextField usr = new TextField("usr", new Model());
		final TextField pwd = new PasswordTextField("pwd", new Model());

		final Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				super.onSubmit();

				final Encryptor encryptor = WicketApplication.getInjector().getInstance(Encryptor.class);
				final Sql2o sql2o = WicketApplication.getInjector().getInstance(Sql2o.class);

				final String username = usr.getModelObjectAsString();
				try (Connection c = sql2o.open()) {
					long num = c.createQuery("SELECT count(*) FROM users WHERE username = :usr")
							.addParameter("usr", username)
							.executeScalar(Long.class);

					if (num == 0) {
						final String password = encryptor.encrypt(pwd.getModelObjectAsString());
						c.createQuery("INSERT INTO users (username, password, created) VALUES (:usr, :pwd, now())")
								.addParameter("usr", username)
								.addParameter("pwd", password)
								.executeUpdate();

						setResponsePage(new LoginPage(Message.info("User '" + username + "' was registered")));
					} else {
						setResponsePage(new LoginPage(Message.warn("User '" + username + "' already exists")));
					}
				} catch (Exception ex) {
					setResponsePage(new LoginPage(Message.error(ex)));
				}
			}
		};
		form.add(usr);
		form.add(pwd);
		add(form);
	}
}
