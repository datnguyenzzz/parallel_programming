# Command and results

1) After installation done. Launch Zookeeper's server by command

```sh
> zkserver
```

2) Launch Zookeeper Client and create root node for project

```sh
> zkCli
> create /servers
```

3) Run project ( Create webserver, create Zookeeper services, fetch several time to specific url)

```sh
> mvn package
> mvn exec:java
```

Result:

Server will anonymously fetch several times to directed URL

![alt text](https://github.com/datnguyen79198/parallel_programming/blob/main/lab6/assert/1.PNG)
