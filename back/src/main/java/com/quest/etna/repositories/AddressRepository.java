package com.quest.etna.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.quest.etna.model.Address;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, Integer> {

    // @Query("SELECT u FROM Address u WHERE u.street = :street")
    // public Address findByUsername(String street);

    // @Query("SELECT n FROM Address n WHERE n.street LIKE %:street%")
    // public List<Address> getAll(Pageable pageable);

    @Query(value = "SELECT * FROM address", nativeQuery = true)
    // public List<Address> getAll(Pageable pageable);
    public List<Address> getAll();

    // @Query("SELECT n FROM Address WHERE n.id = :id") // ORDER BY n.id ASC")
    @Query("SELECT n FROM Address n WHERE n.id = :id") // ORDER BY n.id ASC")
    public List<Address> getById(Integer id);

    @Query("SELECT n FROM Address n WHERE n.user.id = :user_id")
    public Optional<Address> getByUserId(Integer user_id);

}
