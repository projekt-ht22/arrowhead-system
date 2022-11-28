package ai.aitia.gps_controller.gps_controller_provider;

public class GPSProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";


	public static final String GET_GPS_CORDINATES_SERVICE_DEFINITION = "get-gps-cordinates";
	public static final String GET_GPS_CORDINATES_URI = "/get_gps_cordinates";

	public static final String GET_GPS_HEADING_SERVICE_DEFINITION = "get-gps-heading";
	public static final String GET_GPS_HEADING_URI = "/get_gps_heading";

	public static final String GET_GPS_ACCURACY_SERVICE_DEFINITION = "get-gps-accuracy";
	public static final String GET_GPS_ACCURACY_URI = "/get_gps_accuracy";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private GPSProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
