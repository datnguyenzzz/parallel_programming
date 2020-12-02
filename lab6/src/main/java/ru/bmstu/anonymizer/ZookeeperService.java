package ru.bmstu.anonymizer;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;

import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.ArrayList;
import java.util.List;

import ru.bmstu.anonymizer.Store.StoreActor;
import ru.bmstu.anonymizer.Messages.SetServerList;

import akka.stream.javadsl.Flow;

import org.apache.zookeeper.*;


public class ZookeeperService {

    private ZooKeeper zooKeeper;
    private ActorRef storeActor;

    private static final String ZK_BASE_URL = "127.0.0.1:2181";
    private static final String ROOT = "/servers";
    private static final String NODE = "/servers/s";
    private static final int SESSION_TIMEOUT = 60000;

    public ZookeeperService(ActorRef storeActor) throws IOException {
        this.storeActor = storeActor;
        this.zooKeeper = createZooKeeper();
        watchServers();
    }

    public ZooKeeper createZooKeeper() throws IOException {
        return new ZooKeeper(ZK_BASE_URL, SESSION_TIMEOUT, null);
    }

    public void createServer(String serverUrl) throws KeeperException, InterruptedException {
        zooKeeper.create(
            NODE,
            serverUrl.getBytes(),
            ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }

    private void watchServers() {
        try {
            List<String> serverNode = zooKeeper.getChildren(ROOT, watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchServers();
                }
            });

            List<String> servers = new ArrayList<>();

            for (String nodeName : serverNode) {
                System.out.println("nodeName = " + nodeName);
                byte[] serverUrl = zooKeeper.getData(ROOT + "/" + nodeName, null, null);
                servers.add(new String(serverUrl));
            }

            storeActor.tell(new SetServerList(servers.toArray(new String[0])), ActorRef.noSender());
          } catch (KeeperException | InterruptedException e) {
              e.printStackTrace();
          }
    }

}
