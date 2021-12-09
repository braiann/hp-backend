package hp.server.app.services.impl;

import hp.server.app.models.entity.Address;
import hp.server.app.models.entity.City;
import hp.server.app.models.repository.AddressRepository;
import hp.server.app.services.AddressService;
import hp.server.app.services.CityService;
import hp.server.app.utils.exceptionsmessages.ApiRestErrorMessage;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressServiceImpl extends CommonServiceImpl<Address, AddressRepository> implements AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CityService cityService;

    @Override
    public Address save(Address entity) throws Exception {
        logger.info("Enter to save()");
        Optional<City> city = cityService.findById(entity.getCity().getId());
        if (city.isEmpty()) {
            throw new CommonBusinessException(ApiRestErrorMessage.CITY_INVALID);
        }
        entity.setCity(city.get());
        return super.save(entity);
    }


}
