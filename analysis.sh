range="3 4 5 8 16 32 64 128 192"
mvn clean install package -o
for num in ${range}; do
  mvn exec:java -Dexec.mainClass=cat.udl.cig.sms.main.NeighborhoodSimulation -o -Dexec.args="$num"
done