package ai.aitia.gps_controller.gps_controller_provider.gpsinterface;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Component;

@Component
public class GPSInterface {
	private DatagramSocket socket;
    private byte[] buf = new byte[128];

	private byte navigation_status;

	private int time;
	private byte satellites;
	private byte position;
	private byte velocity;

	private short north_velocity_accuracy;
	private short east_velocity_accuracy;
	private short heading_accuracy;

	private double latitude;
	private double longitude;
	private double heading;

    private final Logger logger = LogManager.getLogger(GPSInterface.class);
	

	
	public GPSInterface() throws SocketException {
		// System.out.println("new socket");
        socket = new DatagramSocket(3000);
		// System.out.println("socket done");
	}



	public void getData() throws IOException {

		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		// blocks until a package is received
		try {
			socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  

		// InetAddress address = packet.getAddress();
		// System.out.println("Address: " + address);
		// int port = packet.getPort();
		// System.out.println("Port: " + port);

		byte[] raw_gps_data = packet.getData();
		

		// should always be 0xE7
		byte sync = raw_gps_data[0];
		// System.out.println("sync (0xE7 (-25)): " + sync);
		if (sync == -25){

			// byte 22 Navigation status

			// The navigation status byte value should be 0–7, 10 or 20–22 to be
			// valid for customer use. See page 10 (OxTS-NCOM-Manual). A value of 11 indicates the
			// packet follows NCOM structure-B and should be ignored
			this.navigation_status = raw_gps_data[21];


			// byte 62 Status channel
			// lots of info

			// value 0: Full time, number of satellites, position mode, velocity mode, dual antenna mode
				// byte 67: Total number of satellites tracked by the main GNSS receiver
			// value 3: Position accuracy
				// byte 63-64: North velocity accuracy
				// byte 65-66: East velocity accuracy
			// value 5: Orientation accuracy
				// byte 63-64: Heading accuracy
			//

			byte status_msg = raw_gps_data[62];
			// System.out.println("status_msg: " + status_msg);

			if (status_msg == 0) {
				this.time = ByteBuffer.wrap(raw_gps_data, 63, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
				this.satellites = raw_gps_data[67];
				this.position = raw_gps_data[68];
				this.velocity = raw_gps_data[69];
			}
			else if (status_msg == 3) {
				this.north_velocity_accuracy = ByteBuffer.wrap(raw_gps_data, 63, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
				this.east_velocity_accuracy = ByteBuffer.wrap(raw_gps_data, 65, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
			}
			else if (status_msg == 5) {
				this.heading_accuracy = ByteBuffer.wrap(raw_gps_data, 63, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
			}

			// Latitude of the INS. It is a double in units of radians
			double latitude_radians = ByteBuffer.wrap(raw_gps_data, 23, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();

			// Longitude of the INS. It is a double in units of radians
			double logitude_radians = ByteBuffer.wrap(raw_gps_data, 31, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();

			// Heading in units of 1 × 10−6 radians. Range +- PI
			double heading_radians = raw_gps_data[54] << 16 | raw_gps_data[53] << 8 | raw_gps_data[52];
			heading_radians = heading_radians/1000000;

			this.latitude = Math.toDegrees(latitude_radians);
			this.longitude = Math.toDegrees(logitude_radians);
			this.heading = Math.toDegrees(heading_radians);

			// System.out.println("latitude: " + latitude);
			// System.out.println("longitude: " + longitude);
			// System.out.println("Heading: " + heading);
			logger.info("this heading: {}  raw heading: {}", this.heading, heading_radians);
		}
	}

	public void closeSocket() {
		socket.close();
	}
	

	public byte getNavigation_status() {
		return navigation_status;
	}



	public void setNavigation_status(byte navigation_status) {
		this.navigation_status = navigation_status;
	}



	public int getTime() {
		return time;
	}



	public void setTime(int time) {
		this.time = time;
	}



	public byte getSatellites() {
		return satellites;
	}



	public void setSatellites(byte satellites) {
		this.satellites = satellites;
	}



	public byte getPosition() {
		return position;
	}



	public void setPosition(byte position) {
		this.position = position;
	}



	public byte getVelocity() {
		return velocity;
	}



	public void setVelocity(byte velocity) {
		this.velocity = velocity;
	}



	public short getNorth_velocity_accuracy() {
		return north_velocity_accuracy;
	}



	public void setNorth_velocity_accuracy(short north_velocity_accuracy) {
		this.north_velocity_accuracy = north_velocity_accuracy;
	}



	public short getEast_velocity_accuracy() {
		return east_velocity_accuracy;
	}



	public void setEast_velocity_accuracy(short east_velocity_accuracy) {
		this.east_velocity_accuracy = east_velocity_accuracy;
	}



	public short getHeading_accuracy() {
		return heading_accuracy;
	}



	public void setHeading_accuracy(short heading_accuracy) {
		this.heading_accuracy = heading_accuracy;
	}



	public double getLatitude() {
		return latitude;
	}



	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}



	public double getLongitude() {
		return longitude;
	}



	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}



	public double getHeading() {
		return heading;
	}



	public void setHeading(double heading) {
		this.heading = heading;
	}
}
