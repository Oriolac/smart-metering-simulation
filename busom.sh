numMeters=16
numMsgs="270 300 330 350 380"
mvn clean install package -o
for i in $(seq 1 30); do
	for num in ${numMsgs}; do
		echo $num
 		mvn exec:java -Dexec.mainClass=cat.udl.cig.sms.main.busom.BusomNeighborhood -o -Dexec.args="32 $num" 
	done
done
