package ai.aitia.navigator.navigator_system;

public class PIDController {

    private final double k;
    private final double ki;
    private final double kd;

    private double last_e = 0;
    private double integral_e = 0;

    public PIDController(double k, double ki, double kd) {
        this.k = k;
        this.ki = ki;
        this.kd = kd;
    }

    public double getNextU(double e) {
        integral_e += e;
        return (k * e + kd * (e - last_e) + kd * integral_e);
    }

}
