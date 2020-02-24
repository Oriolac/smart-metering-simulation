
if test $# -eq 1; then
    for i in `seq 101`; do
        echo "============= NEW ITERATION ================="
        java --module-path $PATH_TO_FX --add-modules=javafx.controls -cp out/artifacts/smart_metering_simulation_jar/smart-metering-simulation.jar udl.cig.sms.runnable.NeighborhoodSimulation $1
    done
fi