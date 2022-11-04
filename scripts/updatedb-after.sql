USE arrowhead;

SELECT @car_prov := id FROM system_ WHERE system_name = "cardemoprovider";
SELECT @car_cons := id FROM system_ WHERE system_name = "cardemoconsumer";
SELECT @hello_prov := id FROM system_ WHERE system_name = "helloprovider";
SELECT @hello_cons := id FROM system_ WHERE system_name = "helloconsumer";
SELECT @sr_create := id FROM service_definition WHERE service_definition = "create-car";
SELECT @sr_get := id FROM service_definition WHERE service_definition = "get-car";
SELECT @sr_hello := id FROM service_definition WHERE service_definition = "hello";
SELECT @sr_interface := id FROM service_interface WHERE interface_name = "HTTP-SECURE-JSON";

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@car_cons, @car_prov, @sr_create);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@car_cons, @car_prov, @sr_get);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@hello_cons, @hello_prov, @sr_hello);

SELECT @car_create_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_create;
SELECT @car_get_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_get;
SELECT @hello_aid := id FROM authorization_intra_cloud WHERE service_id = @sr_hello;

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