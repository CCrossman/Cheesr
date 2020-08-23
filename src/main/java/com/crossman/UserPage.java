package com.crossman;

import com.crossman.v2.StoredUser;
import com.crossman.v2.UserRepository;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import java.util.List;

public final class UserPage extends CheesrPage implements IRequireAdminAuthorization {

	public UserPage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		add(new PageNavigatorPanel("pageNavigator"));

		final UserRepository userRepository = WicketApplication.getInjector().getInstance(UserRepository.class);
		final List<StoredUser> storedUsers = userRepository.findAll();

		PageableListView listView = new PageableListView("users", storedUsers, 5) {
			@Override
			protected void populateItem(ListItem item) {
				StoredUser su = (StoredUser) item.getModelObject();
				item.add(new Label("id", String.valueOf(su.getId())));
				item.add(new Label("name", su.getName()));
				item.add(new Label("active", String.valueOf(su.isActive())));
				item.add(new Label("admin", String.valueOf(su.isAdmin())));
				item.add(new Label("created", String.valueOf(su.getCreated())));
			}
		};
		add(listView);
		add(new PagingNavigator("navigator", listView));
	}
}
