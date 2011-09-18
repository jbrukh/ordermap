package org.brukhman.ordermap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

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
		Map<UUID, T> result = Maps.newHashMap();
		for (UUID id: ids) {
			result.put(id, mapping.get(id));
		}
		return result;
	}
}
