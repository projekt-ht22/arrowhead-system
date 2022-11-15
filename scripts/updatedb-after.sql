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

-- rule ids
-- SELECT @car_create_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_create;
-- SELECT @car_get_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_get;
SELECT @hello_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_hello;
SELECT @add_message_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_add_message;
SELECT @get_message_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_message;
SELECT @set_speed_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_set_speed;
SELECT @set_tilt_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_set_tilt;

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

-- INSERT INTO authorization_intra_cloud_interface_connection
--    (authorization_intra_cloud_id, interface_id)
--    VALUES
--    (@add_message_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_message_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@set_speed_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@set_tilt_aid, @sr_interface);