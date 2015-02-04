package gov.raleighnc.switchyard.integration.service.cwmeter.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.switchyard.Context;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.composer.RESTEasyContextMapper;

/**
 * Context Mapper for RESTEasy to allow for CORS-related issues
 * 
 * @author mikev
 */
public class CorsRESTEasyContextMapper extends RESTEasyContextMapper {
    @Override
    public void mapTo(Context context, RESTEasyBindingData target) throws Exception {
        super.mapTo(context, target);
        Map<String, List<String>> httpHeaders = target.getHeaders();
        httpHeaders.put("Access-Control-Allow-Origin", Arrays.asList("*"));
    }
}