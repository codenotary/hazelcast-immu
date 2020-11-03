package io.codenotary.immudb.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import io.codenotary.immudb4j.FileRootHolder;
import io.codenotary.immudb4j.ImmuClient;
import io.codenotary.immudb4j.crypto.VerificationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ImmudbHazelcastPlugin {

    public static void main(String[] args) {
        FileRootHolder rootHolder = null;
        try {
            rootHolder = FileRootHolder.newBuilder().setRootsFolder("./helloworld_immudb_roots").build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImmuClient client = ImmuClient.newBuilder()
                .setServerUrl("localhost")
                .setServerPort(3322)
                .setRootHolder(rootHolder)
                .build();

        client.login("immudb", "immudb");


        ClientConfig config = new ClientConfig();
        config.setClusterName("dev");
        HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient(config);
        IMap<Long, String> map = hazelcastInstanceClient.getMap("data");

        map.addEntryListener((EntryAddedListener<Long, String>) entryEvent -> {
            try {
                String key = Long.toString(entryEvent.getKey());
                client.safeSet(key, entryEvent.getValue().getBytes());
                String value = new String(client.safeGet(key), StandardCharsets.UTF_8);

                System.out.println("Inserted key in HC and LC. Retrieving from LC: ");
                System.out.printf("%s: %s%n%n", key , value);
            } catch (VerificationException e) {
                e.printStackTrace();
            }
        }, true);

        Runtime.getRuntime().addShutdownHook(new Thread(client::logout));

    }
}