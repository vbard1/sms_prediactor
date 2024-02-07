package src.model_builder;

import java.util.HashMap;

public class SmsXmlFile {
    public String filePath;
    public HashMap<String, Integer> adresses;

    public SmsXmlFile(){
        filePath="";
        adresses =  new HashMap<>(5);;
    }

}
