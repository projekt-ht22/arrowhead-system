package ai.aitia.navigator.navigator_system;

public class PIDController {

    private final float k;
    private final float ki;
    private final float kd;

    private float last_e = 0;
    private float integral_e = 0;

    public PIDController(float k, float ki, float kd) {
        this.k = k;
        this.ki = ki;
        this.kd = kd;
    }

    public float getNextU(float e) {
        integral_e += e;
        return (k * e + kd * (e - last_e) + kd * integral_e);
    }

}
