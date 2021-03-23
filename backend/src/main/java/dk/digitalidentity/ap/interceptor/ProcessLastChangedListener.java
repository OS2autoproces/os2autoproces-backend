package dk.digitalidentity.ap.interceptor;

import java.io.Serializable;
import java.util.List;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;

import dk.digitalidentity.ap.dao.model.Process;

// purpose of this class
// *********************
// Hibernate does NOT update lastChanged timestamp on Processes, if only collections
// have been modified. Several bugreports have been opened against Hibernate on this,
// but to little effect - so various workarounds/hacks are proposed on the internet.
//
// This hack seems to have the smallest impact, and it solves the issue without having
// to modify the Hibernate source code.

public class ProcessLastChangedListener implements PreCollectionUpdateEventListener {
	private static final long serialVersionUID = 1280688366478668968L;

	@SuppressWarnings("rawtypes")
	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
		if (event.getAffectedOwnerOrNull() instanceof Process) {
			PersistentCollection collection = event.getCollection();
			Serializable storedSnapshot = collection.getStoredSnapshot();
			boolean equals = false;

			// compare the modified collection (PersistantBag) with the unmodified collection (List)
			if (collection instanceof PersistentBag && storedSnapshot instanceof List) {
				PersistentBag bag = (PersistentBag) collection;
				List list = (List) storedSnapshot;
				
				if (bag.containsAll(list) && bag.size() == list.size()) {
					equals = true;
				}
			}

			if (!equals) {
				Process owner = (Process) event.getAffectedOwnerOrNull();

				if (owner != null) {
					if (owner.getFreetext() != null && owner.getFreetext().length() > 0) {
						// mod(100) works, as there are less than 100 collections on the Process class,
						// and this method is called once for each modified collection. So no matter
						// what, as long as one or more collections have been modified, the freeText
						// field will have a value different from before.
						char newChar = (char) ((((int) owner.getFreetext().charAt(0)) + 1) % 100);

						owner.setFreetext(Character.toString(newChar));
					}
					else {
						owner.setFreetext(Character.toString((char) 1));
					}
				}
			}
		}
	}
}
