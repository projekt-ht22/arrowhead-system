package ai.aitia.robot_controller.robot_controller_provider.serial;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.aitia.robot_controller.robot_controller_provider.serial.Serial;

public class SerialParseTest {


    @Test
    void testParseMessage() throws IOException {

        byte[] duty_cycle = ByteBuffer.allocate(2).putShort((short) 50).array();
        byte[] total_current = ByteBuffer.allocate(4).putFloat((float)10.0).array();
        byte[] track_rpm = ByteBuffer.allocate(4).putInt(500).array();

        ByteArrayOutputStream temp_arr = new ByteArrayOutputStream();
        temp_arr.write(0xFF);
        temp_arr.write(0x04);
        temp_arr.write(0x00);
        temp_arr.write(0x05);
        temp_arr.write(duty_cycle);
        temp_arr.write(0x06);
        temp_arr.write(total_current);
        temp_arr.write(0x07);
        temp_arr.write(track_rpm);
        temp_arr.write(0x80);
        byte[] bytes_array = temp_arr.toByteArray();  
        
    }

    // Scheduler scheduler;

    // @BeforeEach
    // void newScheduler() {
    //     scheduler = new Scheduler();
    // }

    // @Test
    // void testAddingMessagesSamePriority() {
    //     scheduler.addMessage(new Message(new ArrayList<String>(), "first", 0));
    //     scheduler.addMessage(new Message(new ArrayList<String>(), "second", 0));
    //     scheduler.addMessage(new Message(new ArrayList<String>(), "third", 0));

    //     assertEquals(scheduler.getNextAndRemoveMessage().getName(), "first");
    //     assertEquals(scheduler.getNextAndRemoveMessage().getName(), "second");
    //     assertEquals(scheduler.getNextAndRemoveMessage().getName(), "third");
    // }

    // @Test
    // void testAddingMessageDifferentPriority() {
    //     scheduler.addMessage(new Message(new ArrayList<String>(), "low", 1));
    //     scheduler.addMessage(new Message(new ArrayList<String>(), "high", 20));
    //     scheduler.addMessage(new Message(new ArrayList<String>(), "middle", 3));

    //     assertEquals(scheduler.getNextAndRemoveMessage().getName(), "high");
    //     assertEquals(scheduler.getNextAndRemoveMessage().getName(), "middle");
    //     assertEquals(scheduler.getNextAndRemoveMessage().getName(), "low");
    // }
    
}
