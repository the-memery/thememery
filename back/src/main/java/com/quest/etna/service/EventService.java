package com.quest.etna.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quest.etna.model.Event;
import com.quest.etna.repositories.EventRepository;

@Service
public class EventService implements iModelService<Event> {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Event> getList() {
        // Iterable<Event> event = eventRepository.findAll();
        return eventRepository.getAll();
    }

    public List<Event> getAllOrderByDate() {
        return eventRepository.getAllOrderByDate();
    }

    @Override
    public Event getOneById(Integer id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            return null;
        }

        return event.get();
    }

    @Override
    public Event create(Event entity) {
        eventRepository.save(entity);
        return entity;
    }

    @Override
    public Event update(Integer id, Event entity) {

        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            return null;
        }

        Event eventFound = event.get();

        if (entity.getName() != null) {
            eventFound.setName(entity.getName());
        }
        if (entity.getType() != null) {
            eventFound.setType(entity.getType());
        }
        if (entity.getDate() != null) {
            eventFound.setDate(entity.getDate());
        }
        if (entity.getImage() != null) {
            eventFound.setImage(entity.getImage());
        }
        if (entity.getAddress() != null) {
            eventFound.setAddress(entity.getAddress());
        }

        eventRepository.save(eventFound);
        return eventFound;
    }

    @Override
    public Boolean delete(Integer id) {
        try {
            Optional<Event> event = eventRepository.findById(id);
            if (event.isEmpty()) {
                return false;
            }

            Event eventFound = event.get();

            eventRepository.delete(eventFound);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
