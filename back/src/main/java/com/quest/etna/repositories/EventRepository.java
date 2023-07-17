package com.quest.etna.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.quest.etna.model.Event;

public interface EventRepository extends PagingAndSortingRepository<Event, Integer> {
    @Query("SELECT n FROM Event n")
    public List<Event> getAll();

    @Query("SELECT n FROM Event n ORDER BY n.date")
    public List<Event> getAllOrderByDate();

    @Query("SELECT n FROM Event n WHERE n.id = :id") // ORDER BY n.id ASC")
    public List<Event> getById(int id);

    @Query("SELECT n FROM Event n WHERE n.address.id = :address_id")
    public Optional<Event> getByAddressId(int address_id);

}
