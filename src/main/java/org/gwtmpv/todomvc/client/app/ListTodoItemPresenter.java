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
  // could be in the model, but for now Todo is a simple todo
  private final BooleanProperty done = booleanProperty("done", false);
  private final Binder binder = new Binder(this);
  private final AppState state;
  private final Todo todo;
  private final ListTodoItemStyle s;

  public ListTodoItemPresenter(AppState state, Todo todo) {
    super(newListTodoItemView());
    this.state = state;
    this.todo = todo;
    done.set(todo.isDone());
    s = view.style();
  }

  @Override
  public void onBind() {
    super.onBind();
    view.content().setText(todo.getName());

    binder.when(editing).is(true).hide(view.displayPanel());
    binder.when(editing).is(true).show(view.editPanel());
    binder.when(editing).is(true).set(s.editing()).on(view.li());

    binder.bind(done).to(view.checkBox());
    binder.when(done).is(true).set(s.done()).on(view.li());
    binder.when(done).is(true).add(todo).to(state.doneTodos);

    view.content().addDoubleClickHandler(new DoubleClickHandler() {
      public void onDoubleClick(DoubleClickEvent event) {
        editing.set(true);
        view.editBox().setText(todo.getName());
        view.editBox().setFocus(true);
      }
    });

    view.editBox().addKeyDownHandler(new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          editing.set(false);
          todo.setName(view.editBox().getText());
          view.content().setText(view.editBox().getText());
        }
      }
    });
    
    view.destroyAnchor().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        state.destroy(todo);
      }
    });
  }

  public boolean isFor(Todo other) {
    return todo == other;
  }

}
