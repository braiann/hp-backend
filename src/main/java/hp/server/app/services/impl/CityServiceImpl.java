package hp.server.app.services.impl;

import hp.server.app.models.entity.City;
import hp.server.app.models.repository.CityRepository;
import hp.server.app.services.CityService;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl extends CommonServiceImpl<City, CityRepository> implements CityService {

    private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);
    @Autowired
    private CityRepository cityRepository;

}
