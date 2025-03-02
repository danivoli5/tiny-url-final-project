# MongoDB Replica Set Configuration

```
Even with preserved PVCs, sometimes the replica set configuration is not reloaded automatically.

To fix this, you can run the following command:

Exec into the first pod (typically mongo-0):
kubectl exec -it mongo-0 -- mongo -u mongouser -p mongopass --authenticationDatabase admin

In the shell, initialize the replica set:
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "mongo-0.mongo:27017" },
    { _id: 1, host: "mongo-1.mongo:27017" }
  ]
});

Confirm with:
rs.status();
```

# Redis Replica Set Configuration

```
Exec into the replica pod (e.g., redis-1):
kubectl exec -it redis-1 -- redis-cli

Then, in the Redis CLI, run:
REPLICAOF redis-0.redis 6379

Verify Replication:
On redis-1, run:
INFO replication

You should see:
role:slave (or replica)
master_host: redis-0.redis
master_link_status: up

Optionally, check redis-0 with:
kubectl exec -it redis-0 -- redis-cli INFO replication
```