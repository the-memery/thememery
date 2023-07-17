package com.quest.etna.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.quest.etna.model.User;

import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    @Query(value = "SELECT * FROM user", nativeQuery = true)
    // public List<Address> getAll(Pageable pageable);
    public List<User> getAll();

    @Query("SELECT u FROM User u WHERE u.id = :id")
    public User getById(int id);

    @Query("SELECT u FROM User u WHERE u.username = :username")
	public User getByUsername(String username);
}
