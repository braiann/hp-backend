package hp.server.app.models.repository;

import hp.server.app.models.entity.Person;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

    public Person findByDocumentNumber(Long documentNumber);

    public Person findByEmail(String email);

    public Optional<Person> findByUsername(String username);
}
