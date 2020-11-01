package io.codenotary.ledgercompliance.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import io.codenotary.immudb4j.FileRootHolder;
import io.codenotary.immudb4j.crypto.VerificationException;
import io.codenotary.ledgercompliance.client.LedgerComplianceClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LcHazelcastPlugin {

    public static void main(String[] args) {
        FileRootHolder rootHolder = null;
        try {
            rootHolder = FileRootHolder.newBuilder().setRootsFolder("./helloworld_lc_roots").build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final LedgerComplianceClient lcClient = LedgerComplianceClient.newBuilder()
                .setRootHolder(rootHolder)
                .setServerUrl("ppp.immudb.io")
                .setServerPort(33080)
                .setApiKey("mhyavifptrfrkrojvruqgqgpuzmghzwalksm")
                .build();

        ClientConfig config = new ClientConfig();
        config.setClusterName("dev");
        HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(config);
        IMap<Long, String> map = hazelcastInstanceClient.getMap("data");

        map.addEntryListener(new EntryAddedListener<Long, String>() {

            public void entryAdded(EntryEvent<Long, String> entryEvent) {
                try {
                    String key = Long.toString(entryEvent.getKey());
                    lcClient.safeSet(key, entryEvent.getValue().getBytes());
                    String value = new String(lcClient.safeGet(key), StandardCharsets.UTF_8);

                    System.out.println("Inserted key in HC and LC. Retrieving from LC: ");
                    System.out.printf("%s: %s%n%n", key , value);
                } catch (VerificationException e) {
                    e.printStackTrace();
                }
            }
        }, true);

    }
}