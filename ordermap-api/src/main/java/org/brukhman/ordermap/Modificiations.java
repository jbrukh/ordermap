package org.brukhman.ordermap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public final class Modificiations {

	/**
	 * Return just the portion of the map that resolves these ids.
	 * 
	 * @param ids
	 * @param mapping
	 * @return
	 */
	public final static <T> Map<UUID, T> resolve(final Set<UUID> ids, Map<UUID, T> mapping) {
		checkNotNull(mapping);
		checkNotNull(ids);
		// TODO: this is inefficient...
		return Maps.filterKeys(mapping, new Predicate<UUID>() {
			public boolean apply(UUID key) {
				return ids.contains(key);
			}
		});
	}
}
