package ai.aitia.hello.hello_consumer;

public class HelloConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	// The service definitions for the service that is consumed
	public static final String HELLO_SERVICE_DEFINITION = "hello";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private HelloConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
