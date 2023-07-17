package com.quest.etna.repositories;

import com.quest.etna.model.Artwork;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkRepository extends PagingAndSortingRepository<Artwork, Integer> {

    @Query(value = "SELECT a FROM Artwork a")
    // public List<Address> getAll(Pageable pageable);
    public List<Artwork> getAll();

    @Query("SELECT a FROM Artwork a WHERE a.user.id = :user_id")
    public List<Artwork> getByUserId(Integer user_id);

}
