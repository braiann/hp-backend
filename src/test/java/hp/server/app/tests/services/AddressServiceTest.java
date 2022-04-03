package hp.server.app.tests.services;

import hp.server.app.HpServerApplication;
import hp.server.app.models.entity.Address;
import hp.server.app.models.entity.City;
import hp.server.app.models.repository.AddressRepository;
import hp.server.app.models.repository.CityRepository;
import hp.server.app.services.AddressService;
import hp.server.app.tests.Datos;
import hp.server.app.utils.exceptionsmessages.ApiRestErrorMessage;
import nrt.common.microservice.exceptions.CommonBusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

@SpringBootTest(properties = {"spring.profiles.active=test"}, classes = HpServerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddressServiceTest {

    @MockBean
    private AddressRepository addressRepository;
    @MockBean
    private CityRepository cityRepository;
    @Autowired
    private AddressService addressService;

    @Test
    public void findAllTest() {
        System.out.println("----- findAllTest -----");
        Mockito.when(addressRepository.findAll()).thenReturn(Datos.ADDRESS_LIST);

        List<Address> addresses = (List<Address>) addressService.findAll();

        Assertions.assertNotSame(Collections.emptyList(), addresses);
        Assertions.assertEquals(Datos.ADDRESS_LIST.size(), addresses.size());

        Mockito.verify(addressRepository).findAll();
    }

    @Test
    public void saveAddressTest() {
        System.out.println("----- saveAddressTest -----");
        Address newAddress = Datos.NEW_ADDRESS;
        newAddress.setCity(Datos.CITY);

        Mockito.when(cityRepository.findById(1L)).thenReturn(java.util.Optional.of(Datos.CITY));
        Mockito.when(addressRepository.save(Mockito.any(Address.class))).then(new Answer<Address>() {
            Long sequencyId = 1L;

            @Override
            public Address answer(InvocationOnMock invocationOnMock) throws Throwable {
                Address address = invocationOnMock.getArgument(0);
                address.setId(++sequencyId);
                return address;
            }
        });

        try {
            Address address = addressService.save(newAddress);

            Assertions.assertNotNull(address);
            Assertions.assertEquals(2L, address.getId());
            Assertions.assertEquals(Datos.NEW_ADDRESS.getAddress(), address.getAddress());
            Assertions.assertEquals(Datos.NEW_ADDRESS.getCity(), address.getCity());

            Mockito.verify(addressRepository).save(Mockito.any(Address.class));
            Mockito.verify(cityRepository, Mockito.times(1)).findById(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void saveAddressWithNullCityReturnExceptionTest() {
        System.out.println("----- saveAddressWithNullCityReturnExceptionTest -----");
        Address newAddress = Datos.NEW_ADDRESS;
        City city = Datos.CITY;
        city.setId(2L);
        newAddress.setCity(Datos.CITY);

        Mockito.when(cityRepository.findById(1L)).thenReturn(java.util.Optional.of(Datos.CITY));
        Mockito.when(cityRepository.findById(Mockito.anyLong())).thenThrow(new CommonBusinessException(ApiRestErrorMessage.CITY_INVALID));
        Mockito.when(addressRepository.save(Mockito.any(Address.class))).then(new Answer<Address>() {
            Long sequencyId = 1L;

            @Override
            public Address answer(InvocationOnMock invocationOnMock) throws Throwable {
                Address address = invocationOnMock.getArgument(0);
                address.setId(++sequencyId);
                return address;
            }
        });

        try {
            Exception exception = Assertions.assertThrows(CommonBusinessException.class, () -> {
                Address address = addressService.save(newAddress);
            });

            Assertions.assertNotNull(exception);
            Assertions.assertEquals(ApiRestErrorMessage.CITY_INVALID, exception.getMessage());

            Mockito.verify(cityRepository, Mockito.times(1)).findById(2L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
