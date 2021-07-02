range="3" #4 5 8 16 32 64 128 192"
mvn clean install package -o 
for num in ${range}; do
  for numtimes in `seq 1 15`; do
  	mvn exec:java -Dexec.mainClass=cat.udl.cig.sms.main.NeighborhoodSimulation -o -Dexec.args="$num 15"
  done
done
