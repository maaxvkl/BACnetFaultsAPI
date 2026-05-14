package faults.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Fault {

    private String deviceIp;
    private String objectType;
    private String objectName;
    private String objectDescription;
    private String eventState;
    private Map<String,String> properties;

}
