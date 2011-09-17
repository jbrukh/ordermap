package org.brukhman.ordermap;

import com.hazelcast.core.Hazelcast;

public final class BackupNode {
	
	public final static void main(String... args) {
		// start up hazelcast and act as a backup
		Hazelcast.getDefaultInstance();
	}

}
