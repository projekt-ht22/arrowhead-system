USE arrowhead;

-- secure interface id
SELECT @sr_interface := id FROM service_interface WHERE interface_name = "HTTP-SECURE-JSON";

-- gps interface systems
SELECT @gps_controller := id FROM system_ WHERE system_name = "gps-controller";
SELECT @gps_controller_test := id FROM system_ WHERE system_name = "gps-controller-test";
-- gps interface service definitions
SELECT @sr_get_gps_cordinates := id FROM service_definition WHERE service_definition = "get-gps-cordinates";
SELECT @sr_get_gps_heading := id FROM service_definition WHERE service_definition = "get-gps-heading";
SELECT @sr_get_gps_accuracy := id FROM service_definition WHERE service_definition = "get-gps-accuracy";


-- gps interface rules
INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@gps_controller_test, @gps_controller, @sr_get_gps_cordinates);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES    
    (@gps_controller_test, @gps_controller, @sr_get_gps_heading);

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES    
    (@gps_controller_test, @gps_controller, @sr_get_gps_accuracy);

-- rule ids
SELECT @get_gps_cordinates_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_cordinates;
SELECT @get_gps_heading_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_heading;
SELECT @get_gps_accuracy_aid:= id FROM authorization_intra_cloud WHERE service_id = @sr_get_gps_accuracy;

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_gps_cordinates_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_gps_heading_aid, @sr_interface);

INSERT INTO authorization_intra_cloud_interface_connection
    (authorization_intra_cloud_id, interface_id)
    VALUES
    (@get_gps_accuracy_aid, @sr_interface);