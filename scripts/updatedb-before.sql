USE "arrowhead";

-- Delete all first
DELETE FROM system_ WHERE system_name = "cardemoconsumer";
DELETE FROM system_ WHERE system_name = "helloconsumer";
DELETE FROM system_ WHERE system_name = "missionschedulertester";
DELETE FROM system_ WHERE system_name = "missionexecutortester";
DELETE FROM system_ WHERE system_name = "robot-controller-test";
DELETE FROM authorization_intra_cloud;
DELETE FROM authorization_intra_cloud_interface_connection;

SELECT @car_prov := id FROM system_ WHERE system_name = "cardemoprovider";
SELECT @sr_id := id FROM system_ WHERE system_name = "serviceregistry";
SELECT @sr_reg_id := id FROM service_definition WHERE service_definition = "service-register";
SELECT @sr_ureg_id := id FROM service_definition WHERE service_definition = "service-unregister";

INSERT INTO system_
    (system_name, address, address_type, port, authentication_info)
    VALUES
    ("cardemoconsumer", "127.0.0.1", "IPV4", 8080, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0NQrjqUHV8X/UPY96HFStNFRCyyB92RX64PqZZyQRDytyMWVItsaDXL2WuTs1YrCSJORgJr/sIKoP3xENn+uwwTUmqJVEznDw61CS4uVZdaKJsHHZF515ACq/KK89PokrjBcvfPqDvI0DSzofKkZlELffHme6XzPShP7USFS4eIkUeDbg1j25lqo8J18T61wZI+OodXwv4i4ANmlXdwHGNkHGxsWrXOUevMZXNhwcJhWwZmqIT4mr0SoYvZ4txvsYlAp/X+FV2Dc3TRZtHQjVSoidj64A64OdTkvynXXGm2Qj0GSBT/6M760iupzXUupS0GYia4TMTkBwEfmgu/s+QIDAQAB");

INSERT INTO system_
    (system_name, address, address_type, port, authentication_info)
    VALUES
    ("helloconsumer", "127.0.0.1", "IPV4", 8080, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0NQrjqUHV8X/UPY96HFStNFRCyyB92RX64PqZZyQRDytyMWVItsaDXL2WuTs1YrCSJORgJr/sIKoP3xENn+uwwTUmqJVEznDw61CS4uVZdaKJsHHZF515ACq/KK89PokrjBcvfPqDvI0DSzofKkZlELffHme6XzPShP7USFS4eIkUeDbg1j25lqo8J18T61wZI+OodXwv4i4ANmlXdwHGNkHGxsWrXOUevMZXNhwcJhWwZmqIT4mr0SoYvZ4txvsYlAp/X+FV2Dc3TRZtHQjVSoidj64A64OdTkvynXXGm2Qj0GSBT/6M760iupzXUupS0GYia4TMTkBwEfmgu/s+QIDAQAB");

INSERT INTO system_
    (system_name, address, address_type, port, authentication_info)
    VALUES
    ("robot-controller-test", "127.0.0.1", "IPV4", 8080, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0NQrjqUHV8X/UPY96HFStNFRCyyB92RX64PqZZyQRDytyMWVItsaDXL2WuTs1YrCSJORgJr/sIKoP3xENn+uwwTUmqJVEznDw61CS4uVZdaKJsHHZF515ACq/KK89PokrjBcvfPqDvI0DSzofKkZlELffHme6XzPShP7USFS4eIkUeDbg1j25lqo8J18T61wZI+OodXwv4i4ANmlXdwHGNkHGxsWrXOUevMZXNhwcJhWwZmqIT4mr0SoYvZ4txvsYlAp/X+FV2Dc3TRZtHQjVSoidj64A64OdTkvynXXGm2Qj0GSBT/6M760iupzXUupS0GYia4TMTkBwEfmgu/s+QIDAQAB");

INSERT INTO system_
    (system_name, address, address_type, port, authentication_info)
    VALUES
    ("missionexecutortester", "127.0.0.1", "IPV4", 8080, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0NQrjqUHV8X/UPY96HFStNFRCyyB92RX64PqZZyQRDytyMWVItsaDXL2WuTs1YrCSJORgJr/sIKoP3xENn+uwwTUmqJVEznDw61CS4uVZdaKJsHHZF515ACq/KK89PokrjBcvfPqDvI0DSzofKkZlELffHme6XzPShP7USFS4eIkUeDbg1j25lqo8J18T61wZI+OodXwv4i4ANmlXdwHGNkHGxsWrXOUevMZXNhwcJhWwZmqIT4mr0SoYvZ4txvsYlAp/X+FV2Dc3TRZtHQjVSoidj64A64OdTkvynXXGm2Qj0GSBT/6M760iupzXUupS0GYia4TMTkBwEfmgu/s+QIDAQAB");

INSERT INTO system_
    (system_name, address, address_type, port, authentication_info)
    VALUES
    ("missionschedulertester", "127.0.0.1", "IPV4", 8080, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0NQrjqUHV8X/UPY96HFStNFRCyyB92RX64PqZZyQRDytyMWVItsaDXL2WuTs1YrCSJORgJr/sIKoP3xENn+uwwTUmqJVEznDw61CS4uVZdaKJsHHZF515ACq/KK89PokrjBcvfPqDvI0DSzofKkZlELffHme6XzPShP7USFS4eIkUeDbg1j25lqo8J18T61wZI+OodXwv4i4ANmlXdwHGNkHGxsWrXOUevMZXNhwcJhWwZmqIT4mr0SoYvZ4txvsYlAp/X+FV2Dc3TRZtHQjVSoidj64A64OdTkvynXXGm2Qj0GSBT/6M760iupzXUupS0GYia4TMTkBwEfmgu/s+QIDAQAB");

INSERT INTO authorization_intra_cloud
    (consumer_system_id, provider_system_id, service_id)
    VALUES
    (@car_prov, @sr_id, @sr_reg_id);

