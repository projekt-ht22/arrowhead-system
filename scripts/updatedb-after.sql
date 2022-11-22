USE arrowhead;

-- secure interface id
SELECT @sr_interface := id FROM service_interface WHERE interface_name = "HTTP-SECURE-JSON";

-- -- car test systems
-- SELECT @car_prov := id FROM system_ WHERE system_name = "cardemoprovider";
-- SELECT @car_cons := id FROM system_ WHERE system_name = "cardemoconsumer";
-- -- car service definitions
-- SELECT @sr_create := id FROM service_definition WHERE service_definition = "create-car";
-- SELECT @sr_get := id FROM service_definition WHERE service_definition = "get-car";

-- -- car rules
-- INSERT INTO authorization_intra_cloud
--     (consumer_system_id, provider_system_id, service_id)
--     VALUES
--     (@car_cons, @car_prov, @sr_create);

-- INSERT INTO authorization_intra_cloud
--     (consumer_system_id, provider_system_id, service_id)
--     VALUES
--     (@car_cons, @car_prov, @sr_get);

-- hello systems
SELECT @hello_prov := id FROM system_ WHERE system_name = "helloprovider";
SELECT @hello_cons := id FROM system_ WHERE system_name = "helloconsumer";
-- hello service definitions
SELECT @sr_hello := id FROM service_definition WHERE service_definition = "hello";

-- hello rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@hello_cons, @hello_prov, @sr_hello);

-- mission scheduler systems
SELECT @robot_controller := id FROM system_ WHERE system_name = "robot-controller";
SELECT @robot_controller_test := id FROM system_ WHERE system_name = "robot-controller-test";
-- mission scheduler service definitions
-- SELECT @sr_add_message := id FROM service_definition WHERE service_definition = "add-message";
SELECT @sr_set_speed := id FROM service_definition WHERE service_definition = "set-track-speed";
SELECT @sr_get_message := id FROM service_definition WHERE service_definition = "get-message";
SELECT @sr_set_tilt := id FROM service_definition WHERE service_definition = "set-tilt-amount";

-- mission scheduler rules
-- INSERT INTO authorization_intra_cloud
--     (consumer_system_id, provider_system_id, service_id)
--     VALUES
--     (@robot_controller_test, @robot_controller, @sr_add_message);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@robot_controller_test, @robot_controller, @sr_get_message);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@robot_controller_test, @robot_controller, @sr_set_speed);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@robot_controller_test, @robot_controller, @sr_set_tilt);

-- mission executor systems
SELECT @mission_executor := id FROM system_ WHERE system_name = "missionexecutor";
SELECT @mission_executor_tester := id FROM system_ WHERE system_name = "missionexecutortester";
-- mission executor service definitions
SELECT @sr_do_mission := id FROM service_definition WHERE service_definition = "do-mission";

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@mission_executor_tester, @mission_executor, @sr_do_mission);

-- mission scheduler systems
SELECT @mission_scheduler := id FROM system_ WHERE system_name = "missionscheduler";
SELECT @mission_scheduler_tester := id FROM system_ WHERE system_name = "missionschedulertester";
SELECT @sr_add_mission := id FROM service_definition WHERE service_definition = "add-mission";
-- mission scheduler rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@mission_scheduler_tester, @mission_scheduler, @sr_add_mission);

-- navigator systems
SELECT @navigator := id FROM system_ WHERE system_name = "navigator";
SELECT @navigator_tester := id FROM system_ WHERE system_name = "navigatortester";
SELECT @sr_go_to_point := id FROM service_definition WHERE service_definition = "go-to-point";
-- navigator rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@navigator_tester, @navigator, @sr_go_to_point);


-- inter system rules
SELECT @sr_executor_ready := id FROM service_definition WHERE service_definition = "executor-ready";
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@mission_executor, @mission_scheduler, @sr_executor_ready);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@mission_scheduler, @mission_executor, @sr_do_mission);

-- rule ids
-- SELECT @car_create_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_create;
-- SELECT @car_get_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_get;
SELECT @hello_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_hello;
SELECT @add_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_add_mission;
--SELECT @get_next_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_next_mission;
SELECT @do_mission_aid_test:= id FROM authorization_intra_cloud WHERE service_id = @sr_do_mission AND consumer_system_id = @mission_executor_tester;
SELECT @do_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_do_mission  AND consumer_system_id = @mission_scheduler;
SELECT @executor_ready_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_executor_ready;
SELECT @add_message_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_add_message;
SELECT @get_message_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_message;
SELECT @set_speed_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_set_speed;
SELECT @set_tilt_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_set_tilt;
SELECT @go_to_point_test_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_go_to_point AND consumer_system_id = @navigator_tester;

-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@car_create_aid, @sr_interface);

-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@car_get_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@hello_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@add_mission_aid, @sr_interface);

-- INSERT INTO authorization_intra_cloud_interface_connection
--    (authorization_intra_cloud_id, interface_id)
--    VALUES
--    (@add_message_aid, @sr_interface);

-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@get_next_mission_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_message_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@do_mission_aid_test, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@set_speed_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@do_mission_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@executor_ready_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@set_tilt_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@go_to_point_test_aid, @sr_interface);
