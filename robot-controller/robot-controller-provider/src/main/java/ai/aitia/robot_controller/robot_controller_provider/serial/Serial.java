package ai.aitia.robot_controller.robot_controller_provider.serial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.fazecast.jSerialComm.*;


import org.springframework.stereotype.Component;

//@Component
public class Serial {
    private SerialPort comPort = SerialPort.getCommPorts()[0];
    private int duty_cycle = 0;
    private float total_current  = 0;
    private int rpm = 0;

    // private final class MessageListener implements SerialPortMessageListener {
    //     @Override
    //     public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

    //     @Override
    //     public byte[] getMessageDelimiter() { return new byte[] { (byte)0x80 }; }

    //     @Override
    //     public boolean delimiterIndicatesEndOfMessage() { return true; }

    //     @Override
    //     public void serialEvent(SerialPortEvent event)
    //     {
    //         byte[] delimitedMessage = event.getReceivedData();
    //         parse(delimitedMessage);
    //     }
    // }

    public Serial() {
        comPort.setBaudRate(115200);
        comPort.openPort();
        // comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        // MessageListener listener = new MessageListener();
        // comPort.addDataListener(listener);
    }

    public void closePort(){
        comPort.removeDataListener();
        comPort.closePort();
    }


    public void set_speed(int left, int right) throws IOException {

        byte[] left_bytes = ByteBuffer.allocate(4).putInt(left).array();
        byte[] right_bytes = ByteBuffer.allocate(4).putInt(right).array();

        ByteArrayOutputStream temp_arr = new ByteArrayOutputStream();
        temp_arr.write(0xFF);
        temp_arr.write(0x01);
        temp_arr.write(left_bytes);
        temp_arr.write(right_bytes);
        temp_arr.write(0x80);
        byte[] bytes_array = temp_arr.toByteArray();        
        comPort.writeBytes(bytes_array, bytes_array.length);

        rpm =  rpm + 10;
    }

    public void read_data() {
        if (comPort.bytesAvailable() != 0){
            byte[] readBuffer = new byte[comPort.bytesAvailable()];
            comPort.readBytes(readBuffer, readBuffer.length);
            parse(readBuffer);
        }               
    }




    private void parse(byte[] bytes) {
        if (bytes[0] == -1 && bytes[1] == 4 && bytes[16] == -128) {
            System.out.println("vesc id " + bytes[2]);
            duty_cycle = ByteBuffer.wrap(bytes, 4, 2).getShort();
            // this.duty_cycle = duty_cycle;
            System.out.println("Duty cycle " + duty_cycle);
            total_current = ByteBuffer.wrap(bytes, 7, 4).getFloat();
            // this.total_current = total_current;
            System.out.println("Total current " + total_current);
            rpm = ByteBuffer.wrap(bytes, 12, 4).getInt();
            // this.rpm = rpm;
            System.out.println("RPM " + rpm);
        }
        else {
            System.out.println("Unknown parse");
            for(int i=0; i< bytes.length ; i++) {
                System.out.print(String.format("0x%02X", bytes[i]) +" ");
            }
        }
    }

    public int get_duty_cycle() {
        return this.duty_cycle;
    }

    public float get_total_current() {
        return this.total_current;
    }

    public int get_rpm() {
        return this.rpm;
    }

    public int get_baud() {
        return comPort.getBaudRate();
    }
}
