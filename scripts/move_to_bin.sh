#!/bin/bash
cd ..
cp -rf mission-executor/mission-executor-system/target bin/mission-executor
cp -rf mission-scheduler/mission-scheduler-provider/target bin/mission-scheduler
cp -rf mission-scheduler/mission-scheduler-consumer/target bin/mission-scheduler-tester
cp -rf robot-controller/robot-controller-provider/target bin/robot-controller
cp -rf robot-controller/robot-controller-consumer/target bin/robot-controller-tester