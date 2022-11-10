USE arrowhead;

-- secure interface id
SELECT @sr_interface := id FROM service_interface WHERE interface_name = "HTTP-SECURE-JSON";

-- car test systems
SELECT @car_prov := id FROM system_ WHERE system_name = "cardemoprovider";
SELECT @car_cons := id FROM system_ WHERE system_name = "cardemoconsumer";
-- car service definitions
SELECT @sr_create := id FROM service_definition WHERE service_definition = "create-car";
SELECT @sr_get := id FROM service_definition WHERE service_definition = "get-car";

-- car rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@car_cons, @car_prov, @sr_create);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@car_cons, @car_prov, @sr_get);

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
SELECT @mission_scheduler := id FROM system_ WHERE system_name = "missionscheduler";
SELECT @mission_scheduler_tester := id FROM system_ WHERE system_name = "missionschedulertester";
-- mission scheduler service definitions
SELECT @sr_add_mission := id FROM service_definition WHERE service_definition = "add-mission";
SELECT @sr_get_next_mission := id FROM service_definition WHERE service_definition = "get-next-mission";

-- mission scheduler rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@mission_scheduler_tester, @mission_scheduler, @sr_add_mission);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@mission_scheduler_tester, @mission_scheduler, @sr_get_next_mission);

-- mission executor systems
SELECT @mission_executor := id FROM system_ WHERE system_name = "missionexecutor";
SELECT @mission_executor_tester := id FROM system_ WHERE system_name = "missionexecutortester";
-- mission executor service definitions
SELECT @sr_do_mission := id FROM service_definition WHERE service_definition = "do-mission";

-- mission scheduler rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@mission_executor_tester, @mission_executor, @sr_do_mission);

-- rule ids
SELECT @car_create_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_create;
SELECT @car_get_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_get;
SELECT @hello_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_hello;
SELECT @add_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_add_mission;
SELECT @get_next_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_next_mission;
SELECT @do_mission_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_do_mission;

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@car_create_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@car_get_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@hello_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@add_mission_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_next_mission_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@do_mission_aid, @sr_interface);