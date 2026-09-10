package manager;

import model.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractManager<E extends Identifiable> {

    private final List<E> store = new ArrayList<>();
    private boolean unsavedChanges = false;

    // Subclasses get an unmodifiable view — they must call addToStore/removeFromStore
    protected List<E> getStore() {
        return Collections.unmodifiableList(store);
    }

    public List<E> getAll() {
        return new ArrayList<>(store);
    }

    public E findById(String id) {
        if (id == null) return null;
        String needle = id.trim();
        for (E item : store)
            if (needle.equalsIgnoreCase(item.getId())) return item;
        return null;
    }

    public boolean existsById(String id) {
        return findById(id) != null;
    }

    protected void addToStore(E item) {
        store.add(item);
        markChanged();
    }

    protected boolean removeFromStore(E item) {
        boolean removed = store.remove(item);
        if (removed) markChanged();
        return removed;
    }

    public void replaceAll(List<E> freshItems) {
        store.clear();
        store.addAll(freshItems);
        unsavedChanges = false;
    }

    protected void markChanged()       { unsavedChanges = true; }
    public boolean hasUnsavedChanges() { return unsavedChanges; }
    public void clearChangedFlag()     { unsavedChanges = false; }
}
