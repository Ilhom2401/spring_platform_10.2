package uz.pdp.appjparelationships.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjparelationships.entity.Address;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
