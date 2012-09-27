/*
 * Copyright 2012, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.webtrans.client.presenter;

import org.zanata.webtrans.client.events.NotificationEvent.Severity;
import org.zanata.webtrans.client.events.PublishWorkspaceChatEvent;
import org.zanata.webtrans.client.events.PublishWorkspaceChatEventHandler;
import org.zanata.webtrans.client.events.ShowSideMenuEvent;
import org.zanata.webtrans.client.events.WorkspaceContextUpdateEvent;
import org.zanata.webtrans.client.events.WorkspaceContextUpdateEventHandler;
import org.zanata.webtrans.client.rpc.CachingDispatchAsync;
import org.zanata.webtrans.client.view.SideMenuDisplay;
import org.zanata.webtrans.shared.model.UserWorkspaceContext;
import org.zanata.webtrans.shared.rpc.GetTranslatorList;
import org.zanata.webtrans.shared.rpc.GetTranslatorListResult;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

/**
 * @author aeng
 * 
 */
public class SideMenuPresenter extends WidgetPresenter<SideMenuDisplay> implements NotificationLabelListener,
      SideMenuDisplay.Listener, PublishWorkspaceChatEventHandler
{
   private final EditorOptionsPresenter editorOptionsPresenter;
   private final ValidationOptionsPresenter validationOptionsPresenter;
   private final WorkspaceUsersPresenter workspaceUsersPresenter;
   private final NotificationPresenter notificationPresenter;

   private final UserWorkspaceContext userWorkspaceContext;

   private final DispatchAsync dispatcher;

   private boolean isExpended = false;

   @Inject
   // @formatter:off
   public SideMenuPresenter(SideMenuDisplay display, EventBus eventBus, CachingDispatchAsync dispatcher,
                            EditorOptionsPresenter editorOptionsPresenter,
                            ValidationOptionsPresenter validationOptionsPresenter,
                            WorkspaceUsersPresenter workspaceUsersPresenter,
                            NotificationPresenter notificationPresenter,
                            UserWorkspaceContext userWorkspaceContext)
   // @formatter:on
   {
      super(display, eventBus);
      this.editorOptionsPresenter = editorOptionsPresenter;
      this.validationOptionsPresenter = validationOptionsPresenter;
      this.workspaceUsersPresenter = workspaceUsersPresenter;
      this.notificationPresenter = notificationPresenter;

      this.userWorkspaceContext = userWorkspaceContext;
      this.dispatcher = dispatcher;
      display.setListener(this);
   }

   @Override
   protected void onBind()
   {
      editorOptionsPresenter.bind();
      validationOptionsPresenter.bind();
      workspaceUsersPresenter.bind();
      notificationPresenter.bind();

      registerHandler(eventBus.addHandler(WorkspaceContextUpdateEvent.getType(), new WorkspaceContextUpdateEventHandler()
      {
         @Override
         public void onWorkspaceContextUpdated(WorkspaceContextUpdateEvent event)
         {
            userWorkspaceContext.setProjectActive(event.isProjectActive());
            setReadOnly(userWorkspaceContext.hasReadOnlyAccess());
         }
      }));

      setReadOnly(userWorkspaceContext.hasReadOnlyAccess());

      notificationPresenter.setNotificationListener(this);

      registerHandler(eventBus.addHandler(PublishWorkspaceChatEvent.getType(), this));

      // We won't receive the EnterWorkspaceEvent generated by our own login,
      // because this presenter is not bound until we get the callback from
      // EventProcessor.
      // Thus we load the translator list here.
      loadTranslatorList();
   }

   private void loadTranslatorList()
   {
      dispatcher.execute(GetTranslatorList.ACTION, new AsyncCallback<GetTranslatorListResult>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Log.error("error fetching translators list: " + caught.getMessage());
         }

         @Override
         public void onSuccess(GetTranslatorListResult result)
         {
            workspaceUsersPresenter.initUserList(result.getTranslatorList());
         }
      });
   }

   @Override
   public void onPublishWorkspaceChat(PublishWorkspaceChatEvent event)
   {
      if (display.getCurrentTab() != SideMenuDisplay.WORKSPACEUSER_VIEW)
      {
         display.setChatTabAlert(true);
      }
   }

   // Disable Chat, Editor options, and validation options if readonly
   private void setReadOnly(boolean isReadOnly)
   {
      display.setChatTabVisible(!isReadOnly);
      display.setEditorOptionsTabVisible(!isReadOnly);
      display.setValidationOptionsTabVisible(!isReadOnly);
   }

   @Override
   protected void onUnbind()
   {
      editorOptionsPresenter.unbind();
      validationOptionsPresenter.unbind();
      workspaceUsersPresenter.unbind();
      notificationPresenter.unbind();
   }

   @Override
   protected void onRevealDisplay()
   {
   }

   public void showEditorMenu(boolean showEditorMenu)
   {
      if (!userWorkspaceContext.hasReadOnlyAccess())
      {
         display.setEditorOptionsTabVisible(showEditorMenu);
         display.setValidationOptionsTabVisible(showEditorMenu);

         if (showEditorMenu && isExpended)
         {
            display.setSelectedTab(SideMenuDisplay.NOTIFICATION_VIEW);
         }
         else
         {
            display.setSelectedTab(SideMenuDisplay.NOTIFICATION_VIEW);
         }
      }
   }

   @Override
   public void onEditorOptionsClick()
   {
      showAndExpandOrCollapseTab(SideMenuDisplay.EDITOR_OPTION_VIEW);
   }

   @Override
   public void onNotificationClick()
   {
      showAndExpandOrCollapseTab(SideMenuDisplay.NOTIFICATION_VIEW);
   }

   @Override
   public void onValidationOptionsClick()
   {
      showAndExpandOrCollapseTab(SideMenuDisplay.VALIDATION_OPTION_VIEW);
   }

   @Override
   public void onChatClick()
   {
      showAndExpandOrCollapseTab(SideMenuDisplay.WORKSPACEUSER_VIEW);
   }

   protected void showAndExpandOrCollapseTab(int tabView)
   {
      if (!userWorkspaceContext.hasReadOnlyAccess())
      {
         if (!isExpended)
         {
            expendSideMenu(true);
            display.setSelectedTab(tabView);
         }
         else if (display.getCurrentTab() != tabView)
         {
            display.setSelectedTab(tabView);
         }
         else
         {
            expendSideMenu(false);
         }
      }
   }

   protected void expendSideMenu(boolean isExpend)
   {
      isExpended = isExpend;
      eventBus.fireEvent(new ShowSideMenuEvent(isExpended));
      if (!isExpended)
      {
         display.setSelectedTab(SideMenuDisplay.NOTIFICATION_VIEW);
      }
   }

   @Override
   public void setNotificationLabel(int count, Severity severity)
   {
      display.setNotificationText(count, severity);
   }

   @Override
   public void showNotification()
   {
      if (!isExpended)
      {
         expendSideMenu(true);
         display.setSelectedTab(SideMenuDisplay.NOTIFICATION_VIEW);
      }
      else if (display.getCurrentTab() != SideMenuDisplay.NOTIFICATION_VIEW)
      {
         display.setSelectedTab(SideMenuDisplay.NOTIFICATION_VIEW);
      }
   }
}
