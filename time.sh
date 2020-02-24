


if test $# -eq 1; then
    commanda="/home/oriol/.idea-Ij/jbr/bin/java -Dfile.encoding=UTF-8 -classpath /home/oriol/1-Criptografia/1-SmartMetering/2-Projectes/SmartMetering/out/production/SmartMetering:/home/oriol/.m2/repository/com/moandjiezana/toml/toml4j/0.7.2/toml4j-0.7.2.jar:/home/oriol/.m2/repository/com/google/code/gson/gson/2.8.1/gson-2.8.1.jar:/home/oriol/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.0.0-M4/junit-jupiter-api-5.0.0-M4.jar:/home/oriol/.m2/repository/org/opentest4j/opentest4j/1.0.0-M2/opentest4j-1.0.0-M2.jar:/home/oriol/.m2/repository/org/junit/platform/junit-platform-commons/1.0.0-M4/junit-platform-commons-1.0.0-M4.jar:/home/oriol/1-Criptografia/1-SmartMetering/2-Projectes/CigLib/classes/artifacts/CigLib_jar/CigLib.jar:/home/oriol/.m2/repository/org/mockito/mockito-all/2.0.0-beta/mockito-all-2.0.0-beta.jar udl.cig.sms.runnable.NeighborhoodSimulation $1"
    mkdir stats/$1
    for fit in `ls stats/$1`; do
      echo -n "" > "stats/$1/$fit"
    done
    for i in `seq 101`; do
        echo "============= NEW ITERATION ================="
        $commanda > tmp.txt
        awk '
          /SM-BS/ {print $2}
        ' tmp.txt >> stats/$1/SM-BS.dat
        awk '
          /SM-KE/ {print $2}
        ' tmp.txt >> stats/$1/SM-KE.dat
        awk '
          /SSt-BS/ {print $2}
        ' tmp.txt >> stats/$1/SSt-BS.dat
        awk '
          /SSt-KE/ {print $2}
        ' tmp.txt >> stats/$1/SSt-KE.dat
        awk '
          /SM-CT/ {print $2}
        ' tmp.txt >> stats/$1/SM-CT.dat
        awk '
          /SSt-CT/ {print $2}
        ' tmp.txt >> stats/$1/SSt-CT.dat
    done
fi