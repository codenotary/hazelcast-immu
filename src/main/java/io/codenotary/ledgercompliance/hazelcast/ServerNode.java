package io.codenotary.ledgercompliance.hazelcast;

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
        System.out.println("Type any letter to insert a new key, q to exit");
        int i = 0;

        while (c != 'q') {
            map.put(idGenerator.newId(), "message" + i);
            i++;
            c = reader.next().charAt(0);
        }

    }
}

