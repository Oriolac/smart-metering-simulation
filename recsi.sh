numMeters=16
numMsgs="2 4 6 8 10 12 14 16 18 20 25 30 35 40 45 50 60 70 80 90 100 110 120 130 140 150 160 170 180 190 200 210 220 230 240 250"
mvn clean install package -o
for i in $(seq 1 30); do
	for num in ${numMsgs}; do
		echo $num
 		mvn exec:java -Dexec.mainClass=cat.udl.cig.sms.main.NeighborhoodSimulation -o -Dexec.args="16 $num" 
	done
done
