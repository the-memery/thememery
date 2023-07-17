package com.quest.etna.service;

import com.quest.etna.model.Artwork;
import com.quest.etna.repositories.ArtworkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtworkService implements iModelService<Artwork>{

    private ArtworkRepository artworkRepository;

    public ArtworkService(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }

    @Override
    public List<Artwork> getList() {
        return artworkRepository.getAll();
    }

    @Override
    public Artwork getOneById(Integer id) {
        Optional<Artwork> artwork = artworkRepository.findById(id);
        if (artwork.isEmpty()) {
            return null;
        }
        return artwork.get();
    }

    public List<Artwork> getByUserId(Integer id) {
        Optional<List<Artwork>> artworks = Optional.ofNullable(artworkRepository.getByUserId(id));
        if (artworks.isEmpty()) {
            return null;
        }
        return artworks.get();
    }

    @Override
    public Artwork create(Artwork entity) {
        artworkRepository.save(entity);
        return entity;
    }

    @Override
    public Artwork update(Integer id, Artwork entity) {
        Optional<Artwork> artwork = artworkRepository.findById(id);
        if (artwork.isEmpty()) {
            return null;
        }

        Artwork artworkFound = artwork.get();
        if (entity.getTitle() != null) {
            artworkFound.setTitle(entity.getTitle());
        }
        if (entity.getPrice() != null) {
            artworkFound.setPrice(entity.getPrice());
        }
        if (entity.getTechnique() != null) {
            artworkFound.setTechnique(entity.getTechnique());
        }
        if (entity.getImage() != null) {
            artworkFound.setImage(entity.getImage());
        }
        artworkRepository.save(artworkFound);

        return artworkFound;
    }

    @Override
    public Boolean delete(Integer id) {
        try {
            Optional<Artwork> artwork = artworkRepository.findById(id);
            if (artwork.isEmpty()) {
                return false;
            }

            Artwork artworkFound = artwork.get();
            artworkRepository.delete(artworkFound);

        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
