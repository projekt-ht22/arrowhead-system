package ai.aitia.snowblower.snowblower_provider.blowerInterface;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

@Component
public class BlowerInterface {

    private static final int PWM_PIN12 = 12; // MUST BE A HARDWARE PWM SUPPORTED PIN
    private static final int PWM_PIN13 = 13; // MUST BE A HARDWARE PWM SUPPORTED PIN

    private static final byte COMM_SET_RPM = 8; // uart rpm identification

    private Context pi4j;
    private Pwm pwm_pin12;
    private Pwm pwm_pin13;
    private Serial uart;

    private boolean stop = false;


    public BlowerInterface() {
        pi4j = Pi4J.newAutoContext();

        // setup pwm

        PwmConfig config_pin12 = Pwm.newConfigBuilder(pi4j)
                .id("pwm-pin-12")
                .name("PWM Pin 12")
                .address(PWM_PIN12)
                .pwmType(PwmType.HARDWARE) // USE HARDWARE PWM
                .frequency(1000)    // optionally pre-configure the desired frequency to 1KHz
                //.shutdown(0)        // optionally pre-configure a shutdown duty-cycle value (on terminate)
                //.initial(125)     // optionally pre-configure an initial duty-cycle value (on startup)
                .build();

        // use try-with-resources to auto-close I2C when complete
        pwm_pin12 = pi4j.providers().get(PiGpioPwmProvider.class).create(config_pin12);

        PwmConfig config_pin13 = Pwm.newConfigBuilder(pi4j)
            .id("pwm-pin-13")
            .name("PWM Pin 13")
            .address(PWM_PIN13)
            .pwmType(PwmType.HARDWARE) // USE HARDWARE PWM
            .frequency(1000)    // optionally pre-configure the desired frequency to 1KHz
            //.shutdown(0)        // optionally pre-configure a shutdown duty-cycle value (on terminate)
            //.initial(125)     // optionally pre-configure an initial duty-cycle value (on startup)
            .build();

        // use try-with-resources to auto-close I2C when complete
        pwm_pin13 = pi4j.providers().get(PiGpioPwmProvider.class).create(config_pin13);


        // setup uart

        // https://www.embeddedpi.com/documentation/com-ports/mypi-industrial-raspberry-pi-uart1-ttys0-configuration
        SerialConfig config_uart  = Serial.newConfigBuilder(pi4j)
                .id("uart-port")
                .name("My Uart Port")
                .device("/dev/ttyS0") // pin14: TX pin15:RX
                .use_115200_N81()
                .build();
        
        // get a serial I/O provider from the Pi4J context
        SerialProvider serialProvider = pi4j.provider("pigpio-serial");


        // use try-with-resources to auto-close SERIAL when complete
        uart = serialProvider.create(config_uart);

        // open serial port communications
        uart.open();
    }

    public void setPwmPin12(int dutyCycle) {
        if (!stop){
            pwm_pin12.dutyCycle(dutyCycle);
            pwm_pin12.on();
		    System.out.println("Pin12 dutyCycle: " + pwm_pin12.dutyCycle());
        }
        else {
            System.out.println("System is stopped");
        }
    }

    public void setPwmPin13(int dutyCycle) {
        if (!stop){
            pwm_pin13.dutyCycle(dutyCycle);
            pwm_pin13.on();
		    System.out.println("Pin13 dutyCycle: " + pwm_pin13.dutyCycle());
        }
        else {
            System.out.println("System is stopped");
        }
    }

    public void SetRpm(int rpm){
        if (!stop){

            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            byte[] rpm_bytes = ByteBuffer.allocate(4).putInt(rpm).array();
            payload.write(COMM_SET_RPM);
            try {
                payload.write(rpm_bytes);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            byte[] payload_bytes = payload.toByteArray();  


            int crc = 0x0000;          // initial value
            int polynomial = 0x1021;
            for (int index = 0; index < payload_bytes.length; index++) {
                byte b = payload_bytes[index];
                for (int i = 0; i < 8; i++) {
                    boolean bit = ((b >> (7 - i) & 1) == 1);
                    boolean c15 = ((crc >> 15 & 1) == 1);
                    crc <<= 1;
                    if (c15 ^ bit) crc ^= polynomial;
                }
            }
            crc &= 0xffff;

            ByteArrayOutputStream temp_arr = new ByteArrayOutputStream();

            temp_arr.write(0x02);               // One Start byte (value 2 for short packets and 3 for long packets)
            temp_arr.write(0x05);               // One or two bytes specifying the packet length
            try {
                temp_arr.write(payload_bytes);  // The payload of the packet
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  
            byte[] crc_bytes = ByteBuffer.allocate(2).putShort(((short)crc)).array();

            byte[] crc_bytes2 = ByteBuffer.allocate(4).putInt(crc).array();
            StringBuilder result = new StringBuilder();
            for (byte aByte : crc_bytes2) {
                result.append(String.format("%02x", aByte));
                result.append(" ");
                // upper case
                // result.append(String.format("%02X", aByte));
            }
            System.out.println("crc array: " + result.toString());

            try {
                temp_arr.write(crc_bytes);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }               // Two bytes with a CRC checksum on the payload
            temp_arr.write(3);                  // One stop byte (value 3)

            byte[] bytes_array = temp_arr.toByteArray();   
            System.out.println("test");
            System.out.println("byte array: " + bytes_array);
            System.out.println("crc: " + crc);

            StringBuilder result2 = new StringBuilder();
            for (byte aByte : bytes_array) {
                result2.append(String.format("%02x", aByte));
                result2.append(" ");
                // upper case
                // result.append(String.format("%02X", aByte));
            }
            System.out.println("byte array: " + result2.toString());
            uart.write(bytes_array);

            
        }
    }
    
    public void stopSystem() {
        System.out.println("Stop system");
        stop = true;
        pwm_pin12.off();
        pwm_pin13.off();
        SetRpm(0);
    }

    public void startSystem() {
        System.out.println("Start system");
        stop = false;
        pwm_pin12.on();
        pwm_pin13.on();
    }


    public void shutdown() {
        // turn of pwm
        pi4j.shutdown();
    }
}