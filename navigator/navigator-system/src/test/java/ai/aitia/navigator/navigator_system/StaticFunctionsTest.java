package ai.aitia.navigator.navigator_system;

import org.junit.jupiter.api.Test;

import ai.aitia.mission_scheduler.common.GPSPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ai.aitia.navigator.navigator_system.StaticFunctions.calculateBearing;

public class StaticFunctionsTest {

    @Test
    void testBearingCalculation() {
        assertEquals(2.017273193419058,
            calculateBearing(
                new GPSPoint(65.61719301392534, 22.138877684612723),
                new GPSPoint(65.61706745373932, 22.139513006460135)
            )
        );
        assertEquals(-0.1652011059161434,
            calculateBearing(
                //65.61686316645734, 22.139252438508013
                new GPSPoint(65.61686316645734, 22.139252438508013),
                //65.61705587083694, 22.13917461585554
                new GPSPoint(65.61705587083694, 22.13917461585554)
            )
        );
        assertEquals(-2.8526423281242153,
            calculateBearing(
                //65.61686316645734, 22.139252438508013
                new GPSPoint(65.61686316645734, 22.139252438508013),
                //65.6167103893084, 22.139142429348244
                new GPSPoint(65.6167103893084, 22.139142429348244)
            )
        );
        assertEquals(1.5705359654353315,
            calculateBearing(
                //65.61686316645734, 22.139252438508013
                new GPSPoint(65.61686316645734, 22.139252438508013),
                //65.61686319901285, 22.13955817173417
                new GPSPoint(65.61686319901285, 22.13955817173417)
            )
        );
    }
}