# nohup java -Xms256m -Xmx256m  -Xmn96m -Xss256k -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=96m  -jar eureka-server-0.0.1-SNAPSHOT.jar  >eureka.log 2>&1 &
# nohup java -Xms128m -Xmx128m  -Xmn48m -Xss256k -XX:MetaspaceSize=32m -XX:MaxMetaspaceSize=512m  -jar eureka-server-0.0.1-SNAPSHOT.jar  >eureka.log 2>&1 &
nohup java -Xms128m -Xmx128m  -Xmn80m -Xss256k -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=64m   -jar basic-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev  >log.log 2>&1 &
# nohup java -Xms128m -Xmx128m  -Xmn48m -Xss256k   -jar eureka-server-0.0.1-SNAPSHOT.jar  >eureka.log 2>&1 &
