package com.tabulaw.client.view;

import java.util.ArrayList;
import java.util.Iterator;

import com.allen_sauer.gwt.log.client.Log;

/**
 * ViewCache - A cache container for views that are managed by the mvc
 * framework.
 * <p>
 * This cache does two things:
 * <ol>
 * <li>Manage a bounded queue of views that are dom attach ready whose capacity
 * is provided upon instantiation of this cache.
 * <li>Manage a stack of distinct view refs where each ref represents a
 * "pointer" to a view that was or is viewed. Therefore, the max size of this
 * stack is the number of defined views in the app.
 * </ol>
 * <p>
 * Both lists are orderd according to the order in which views are visited where
 * the list head contains the most recently visited.
 * @author jpk
 */
final class ViewCache {

	/**
	 * The maximum number of views to cache at any given time.
	 */
	private final int capacity;

	/**
	 * queue of views in order of newest cached (head) to oldest cached (tail)
	 * bounded by a set capacity.
	 */
	private final ArrayList<CView> queue;

	/**
	 * Unbounded distinct list of refs to visited views whose order is dictated by
	 * the view visitation order.
	 */
	private final ArrayList<ViewRef> stack;

	/**
	 * Constructor
	 * @param capacity The max number of queue cache elements to store at any
	 *        given time
	 */
	ViewCache(int capacity) {
		this.capacity = capacity;
		this.queue = new ArrayList<CView>(capacity);
		this.stack = new ArrayList<ViewRef>();
	}

	/**
	 * @return The max number of cachable views at a single point in time.
	 */
	int getCapacity() {
		return capacity;
	}

	/**
	 * Caches the given view.
	 * <p>
	 * Sets the given view at the head of the view cache queue returning the
	 * "expired" view if the view cache is at capacity prior to calling this
	 * method or <code>null</code> if the view cache was not at capacity.
	 * @param e the view and its initializer to cache
	 * @return The removed cache element or <code>null</code> if the queue cache
	 *         is not yet at capacity.
	 */
	CView cache(CView e) {
		assert e != null;
		final ViewKey key = e.getViewKey();

		// add to queue removing it first if present to maintain desired ordering
		Log.debug("ViewCache.cache() - caching queue entry: " + key);
		final int qindex = searchQueue(key);
		if(qindex >= 0) {
			//Log.debug("ViewCache.set() - removing OLD queue cache entry: " + key);
			queue.remove(qindex);
		}
		// insert at head
		queue.add(0, e);

		// post to stack
		final int sindex = searchStack(key);
		if(sindex >= 0) {
			// in stack already - move it to head if elsewhere
			if(sindex > 0) {
				Log.debug("ViewCache.cache() - moving existing stack entry to head: " + key);
				stack.add(0, stack.remove(sindex));
			}
		}
		else {
			// not in stack - add it at head
			Log.debug("ViewCache.cache() - adding view to stack: " + key);
			stack.add(0, new ViewRef(e.init, e.vc.getView().getShortViewName(), e.vc.getView().getLongViewName()));
		}

		CView expired = null;

		// queue capacity check
		if(queue.size() > capacity) {
			expired = queue.remove(queue.size() - 1);
			assert queue.size() == capacity;
		}

		return expired;
	}

	/**
	 * Remove a view from cache. <br>
	 * @param key the key of the element to remove
	 */
	void remove(ViewKey key) {
		removeAt(searchQueue(key));
	}

	/**
	 * Removes a view from the view stack.
	 * @param key the key of the element to remove
	 */
	void removeFromStack(ViewKey key) {
		stack.remove(searchStack(key));
	}

	/**
	 * Removes a view from cache at the given index. <br>
	 * NOTE: the visited stack is un-affected.
	 * @param index the index at which to remove the element
	 * @return The removed element.
	 */
	CView removeAt(int index) {
		return queue.remove(index);
	}

	/**
	 * @return The number currently cached views in the queue.
	 */
	int size() {
		return queue.size();
	}

	/**
	 * @return The number of distinct visited views - i.e.: the size of the
	 *         visisted stack.
	 */
	int numVisited() {
		return stack.size();
	}

	/**
	 * Clears out <em>all</em> cached elements in the queue and stack lists
	 * effectively setting its state to that of instantiation.
	 */
	void clear() {
		queue.clear();
		stack.clear();
	}

	/**
	 * Searches the view queue.
	 * @param key The view key
	 * @return The matching cache list index or <code>-1</code> if not present.
	 */
	int searchQueue(ViewKey key) {
		for(int i = 0; i < queue.size(); i++) {
			if(key.equals(queue.get(i).getViewKey())) return i;
		}
		return -1;
	}

	/**
	 * Same as {@link #searchQueue(ViewKey)} but returns the element rather than
	 * its index.
	 * @param key the view key
	 * @return the matching element or <code>null</code> if not present.
	 */
	CView peekQueue(ViewKey key) {
		for(int i = 0; i < queue.size(); i++) {
			final CView e = queue.get(i);
			if(key.equals(e.getViewKey())) return e;
		}
		return null;
	}

	/**
	 * Searches the visited view ref stack.
	 * @param key The view key
	 * @return The matching cache list index or <code>-1</code> if not present.
	 */
	private int searchStack(ViewKey key) {
		for(int i = 0; i < stack.size(); i++) {
			if(stack.get(i).getViewInitializer().getViewKey().equals(key)) return i;
		}
		return -1;
	}

	/**
	 * @return A newly created queue cache element iterator from newest to oldest.
	 */
	Iterator<CView> queueIterator() {
		return queue.size() == 0 ? null : queue.listIterator(0);
	}

	/**
	 * @return A newly created stack cache element iterator from newest to oldest.
	 */
	Iterator<ViewRef> visitedRefIterator() {
		return stack.size() == 0 ? null : stack.listIterator(0);
	}
}