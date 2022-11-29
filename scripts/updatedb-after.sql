USE arrowhead;

-- secure interface id
SELECT @sr_interface := id FROM service_interface WHERE interface_name = "HTTP-SECURE-JSON";


-- Systems and service definitions ----------------------------------------------------------------------------------------

-- hello systems
-- SELECT @hello_prov := id FROM system_ WHERE system_name = "helloprovider";
-- SELECT @hello_cons := id FROM system_ WHERE system_name = "helloconsumer";
-- hello service definitions
-- SELECT @sr_hello := id FROM service_definition WHERE service_definition = "hello";

-- gps interface systems
SELECT @gps_controller := id FROM system_ WHERE system_name = "gps-controller";
SELECT @gps_controller_test := id FROM system_ WHERE system_name = "gps-controller-test";
-- gps interface service definitions
SELECT @sr_get_gps_cordinates := id FROM service_definition WHERE service_definition = "get-gps-cordinates";
SELECT @sr_get_gps_heading := id FROM service_definition WHERE service_definition = "get-gps-heading";
SELECT @sr_get_gps_accuracy := id FROM service_definition WHERE service_definition = "get-gps-accuracy";

-- mission scheduler systems
SELECT @robot_controller := id FROM system_ WHERE system_name = "robot-controller";
SELECT @robot_controller_test := id FROM system_ WHERE system_name = "robot-controller-test";

-- mission scheduler service definitions
-- SELECT @sr_add_message := id FROM service_definition WHERE service_definition = "add-message";
SELECT @sr_set_speed := id FROM service_definition WHERE service_definition = "set-track-speed";
SELECT @sr_get_message := id FROM service_definition WHERE service_definition = "get-message";
SELECT @sr_set_tilt := id FROM service_definition WHERE service_definition = "set-tilt-amount";

-- mission executor systems
-- SELECT @mission_executor := id FROM system_ WHERE system_name = "missionexecutor";
-- SELECT @mission_executor_tester := id FROM system_ WHERE system_name = "missionexecutortester";
-- mission executor service definitions
-- SELECT @sr_do_mission := id FROM service_definition WHERE service_definition = "do-mission";

-- mission scheduler systems
-- SELECT @mission_scheduler := id FROM system_ WHERE system_name = "missionscheduler";
-- SELECT @mission_scheduler_tester := id FROM system_ WHERE system_name = "missionschedulertester";
-- mission scheduler service definitions
-- SELECT @sr_add_mission := id FROM service_definition WHERE service_definition = "add-mission";

-- navigator systems
SELECT @navigator := id FROM system_ WHERE system_name = "navigator";
SELECT @navigator_tester := id FROM system_ WHERE system_name = "navigatortester";
-- navigator service definitions
SELECT @sr_go_to_point := id FROM service_definition WHERE service_definition = "go-to-point";

-- Rules for the test systems ---------------------------------------------------------------------------------------------


-- rule ids
-- gps interface rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@gps_controller_test, @gps_controller, @sr_get_gps_cordinates);
SELECT @get_gps_cordinates_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_cordinates;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_gps_cordinates_aid, @sr_interface);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES    
    (@gps_controller_test, @gps_controller, @sr_get_gps_heading);
SELECT @get_gps_heading_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_heading;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_gps_heading_aid, @sr_interface);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@gps_controller_test, @gps_controller, @sr_get_gps_accuracy);
SELECT @get_gps_accuracy_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_accuracy;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_gps_accuracy_aid, @sr_interface);

-- hello rules
-- INSERT INTO authorization_intra_cloud
--     (consumer_system_id, provider_system_id, service_id)
--     VALUES
--     (@hello_cons, @hello_prov, @sr_hello);
-- SELECT @hello_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_hello;
-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@hello_aid, @sr_interface);


-- Robot controller
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@robot_controller_test, @robot_controller, @sr_get_message);
SELECT @get_message_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_message;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_message_aid, @sr_interface);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@robot_controller_test, @robot_controller, @sr_set_speed);
SELECT @set_speed_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_set_speed;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@set_speed_aid, @sr_interface);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@robot_controller_test, @robot_controller, @sr_set_tilt);
SELECT @set_tilt_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_set_tilt;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@set_tilt_aid, @sr_interface);

-- mission executor
-- INSERT INTO authorization_intra_cloud
--     (consumer_system_id, provider_system_id, service_id)
--     VALUES
--     (@mission_executor_tester, @mission_executor, @sr_do_mission);
-- SELECT @do_mission_aid_test:= id FROM authorization_intra_cloud WHERE service_id = @sr_do_mission AND consumer_system_id = @mission_executor_tester;
-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@do_mission_aid_test, @sr_interface);

-- mission scheduler rules
-- INSERT INTO authorization_intra_cloud
--     (consumer_system_id, provider_system_id, service_id)
--     VALUES
--     (@mission_scheduler_tester, @mission_scheduler, @sr_add_mission);
-- SELECT @add_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_add_mission;
-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@add_mission_aid, @sr_interface);

-- navigator rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@navigator_tester, @navigator, @sr_go_to_point);
SELECT @go_to_point_test_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_go_to_point AND consumer_system_id = @navigator_tester;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@go_to_point_test_aid, @sr_interface);

---------------------------------------------------------------------------------------------------------------------------------------------------------------

-- inter system rules

-- mission executor -> mission scheduler : executor ready
-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@mission_executor, @mission_scheduler, @sr_executor_ready);
-- SELECT @executor_ready_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_executor_ready;
-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@executor_ready_aid, @sr_interface);

-- mission scheduler -> mission executor : do mission
-- INSERT INTO authorization_intra_cloud
--     (consumer_system_id, provider_system_id, service_id)
--     VALUES
--     (@mission_scheduler, @mission_executor, @sr_do_mission);
-- SELECT @do_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_do_mission  AND consumer_system_id = @mission_scheduler;
-- INSERT INTO authorization_intra_cloud_interface_connection
--     (authorization_intra_cloud_id, interface_id)
--     VALUES
--     (@do_mission_aid, @sr_interface);

-- navigator -> gps controller : get accuracy
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@navigator, @gps_controller, @sr_get_gps_accuracy);
SELECT @navigator_get_accuracy_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_accuracy  AND consumer_system_id = @navigator;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@navigator_get_accuracy_aid, @sr_interface);

-- navigator -> gps controller : get coordinates
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@navigator, @gps_controller, @sr_get_gps_cordinates);
SELECT @navigator_get_coordinates_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_cordinates  AND consumer_system_id = @navigator;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@navigator_get_coordinates_aid, @sr_interface);

-- navigator -> gps controller : get heading
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@navigator, @gps_controller, @sr_get_gps_heading);
SELECT @navigator_get_heading_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_heading  AND consumer_system_id = @navigator;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@navigator_get_heading_aid, @sr_interface);

-- navigator -> robot controller : set speed
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@navigator, @robot_controller, @sr_set_speed);
SELECT @navigator_set_speed_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_set_speed  AND consumer_system_id = @navigator;
INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@navigator_set_speed_aid, @sr_interface);