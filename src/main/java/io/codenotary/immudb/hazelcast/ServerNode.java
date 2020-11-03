package io.codenotary.immudb.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import java.util.Map;
import java.util.Scanner;

public class ServerNode {

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        Map<Long, String> map = hazelcastInstance.getMap("data");
        FlakeIdGenerator idGenerator = hazelcastInstance.getFlakeIdGenerator("newid");

        char c = 0;
        Scanner reader = new Scanner(System.in);
        System.out.println("Press Enter to insert a new key, q to exit");

        for (int i = 0; c != 'q'; i++) {
            var key = idGenerator.newId();
            var value = "message" + i;
            map.put(key, value);
            System.out.printf("New key inserted: %d: %s%n", key, value);
            String input = reader.nextLine();
            c = input.length() > 0 ? input.charAt(0) : 0;
        }

        System.exit(0);
    }
}

