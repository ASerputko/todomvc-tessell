package org.gwtmpv.todomvc.client.app;

import static org.gwtmpv.model.properties.NewProperty.booleanProperty;
import static org.gwtmpv.todomvc.client.views.AppViews.newListTodoItemView;

import org.gwtmpv.model.dsl.Binder;
import org.gwtmpv.model.properties.BooleanProperty;
import org.gwtmpv.presenter.BasicPresenter;
import org.gwtmpv.todomvc.client.model.AppState;
import org.gwtmpv.todomvc.client.model.Todo;
import org.gwtmpv.todomvc.client.views.IsListTodoItemView;
import org.gwtmpv.todomvc.client.views.ListTodoItemStyle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

public class ListTodoItemPresenter extends BasicPresenter<IsListTodoItemView> {

  private final BooleanProperty editing = booleanProperty("editing", false);
  private final Binder binder = new Binder(this);
  private final AppState state;
  private final Todo todo;
  private final ListTodoItemStyle s;

  public ListTodoItemPresenter(AppState state, Todo todo) {
    super(newListTodoItemView());
    this.state = state;
    this.todo = todo;
    s = view.style();
  }

  @Override
  public void onBind() {
    super.onBind();

    binder.when(editing).is(true).show(view.editPanel());
    binder.when(editing).is(true).hide(view.displayPanel());
    binder.when(editing).is(true).set(s.editing()).on(view.li());
    view.content().setText(todo.name.get());

    binder.bind(todo.name).to(view.editBox());
    binder.bind(todo.name).toTextOf(view.content());

    binder.bind(todo.done).to(view.checkBox());
    binder.when(todo.done).is(true).set(s.done()).on(view.li());
    binder.when(todo.done).is(true).add(todo).to(state.doneTodos);

    view.content().addDoubleClickHandler(new DoubleClickHandler() {
      public void onDoubleClick(DoubleClickEvent event) {
        editing.set(true);
        view.editBox().setFocus(true);
      }
    });

    view.editBox().addKeyDownHandler(new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          editing.set(false);
        }
      }
    });
    
    view.destroyAnchor().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        state.destroy(todo);
      }
    });
  }

}
