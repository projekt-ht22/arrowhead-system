package ai.aitia.snowblower.common.dto;

import java.io.Serializable;


public class GetAccessoryIDResponseDTO implements Serializable{

    private static final long serialVersionUID = 1911L;

    private String name = "Snowblower";
    private String version = "0.1";


    public GetAccessoryIDResponseDTO() {}
    public GetAccessoryIDResponseDTO(final String name, final String version) {
        this.name = name;
        this.version = version;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }



}
