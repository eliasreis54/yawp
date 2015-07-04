package io.yawp.repository.shields;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.ActionKey;
import io.yawp.servlet.HttpException;
import io.yawp.utils.HttpVerb;

import java.util.List;

public class Shield<T> extends Feature {

	private boolean allow = false;

	protected IdRef<?> parentId;

	protected IdRef<T> id;

	protected T object;

	protected List<T> objects;

	private ActionKey actionKey;

	protected void always() {
	}

	protected void index() {
	}

	protected void show() {
	}

	protected void create() {
	}

	protected void update() {
	}

	protected void destroy() {
	}

	protected void custom() {
	}

	public final void protectIndex() {
		always();
		index();
		throwIfNotAllowed();
	}

	public final void protectShow() {
		always();
		show();
		throwIfNotAllowed();
	}

	public final void protectCreate() {
		always();
		create();
		throwIfNotAllowed();
	}

	public final void protectUpdate() {
		always();
		update();
		throwIfNotAllowed();
	}

	public final void protectDestroy() {
		always();
		destroy();
		throwIfNotAllowed();
	}

	public final void protectCustom() {
		always();
		custom();
		throwIfNotAllowed();
	}

	protected final Shield<T> allow(boolean condition) {
		this.allow = this.allow || condition;
		return this;
	}

	private void throwIfNotAllowed() {
		if (!allow) {
			throw new HttpException(404);
		}
	}

	@SuppressWarnings("unchecked")
	public void setId(IdRef<?> id) {
		this.id = (IdRef<T>) id;
	}

	@SuppressWarnings("unchecked")
	public void setObject(Object object) {
		this.object = (T) object;
	}

	@SuppressWarnings("unchecked")
	public void setObjects(List<?> objects) {
		this.objects = (List<T>) objects;
	}

	public void setActionKey(ActionKey actionKey) {
		this.actionKey = actionKey;
	}

	protected boolean requestHasObject() {
		return object != null || (objects != null && objects.size() > 0);
	}

	protected boolean isArray() {
		return objects != null;
	}

	protected boolean isAction(HttpVerb verb, String actionName) {
		return isAction(verb, actionName, false);
	}

	protected boolean isActionOverCollection(HttpVerb verb, String actionName) {
		return isAction(verb, actionName, true);
	}

	protected boolean isAction(HttpVerb verb, String actionName, boolean overCollection) {
		if (actionKey == null) {
			return false;
		}
		return actionKey.equals(new ActionKey(verb, actionName, overCollection));
	}

	// TODO
	protected Shield<T> allow() {
		// TODO Auto-generated method stub
		return this;
	}

	public void where(String string, String string2, int i) {
		// TODO Auto-generated method stub

	}

	public void facade(Class<?> clazz) {
		// TODO Auto-generated method stub

	}

}