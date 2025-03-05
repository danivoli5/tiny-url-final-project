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
