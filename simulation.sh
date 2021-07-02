numMeters=192
mvn clean install package -o -DskipTests
for i in $(seq 1 100); do
  echo $i
  mvn exec:java -Dexec.mainClass=cat.udl.cig.sms.main.NeighborhoodSimulation -o -Dexec.args="$numMeters"
done
