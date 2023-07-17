package com.quest.etna.service;

import com.quest.etna.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.quest.etna.model.Address;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AddressService implements iModelService<Address> {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    // public List<Address> getList(Integer page, Integer limit) {
    public List<Address> getList() {
        // attention avec lignes 26 - 29
        Iterable<Address> address = addressRepository.findAll();
        // if (((List<Address>) address).isEmpty()) {
        //     return null;
        // }

        // PageRequest pageable = PageRequest.of(page, limit);
        // PageRequest pageable = PageRequest.of();
        // return addressRepository.getAll(pageable);
        return addressRepository.getAll();
        // getAllByUserID
    }

    @Override
    public Address getOneById(Integer id) {
        Optional<Address> address = addressRepository.findById(id);
        if (address.isEmpty()) {
            return null;
        }

        return address.get();
    }

    // @Override
    public Address getByUserId(Integer user_id) {
        Optional<Address> address = addressRepository.getByUserId(user_id);
        if (address.isEmpty()) {
            return null;
        }

        return address.get();
    }

    @Override
    public Address create(Address entity) {
        addressRepository.save(entity);
        return entity;
    }

    @Override
    public Address update(Integer id, Address entity) {

        Optional<Address> address = addressRepository.findById(id);
        if (address.isEmpty()) {
            return null;
        }

        Address addressFound = address.get();

        if (entity.getStreet() != null) {
            addressFound.setStreet(entity.getStreet());
        }
        if (entity.getPostalCode() != null) {
            addressFound.setPostalCode(entity.getPostalCode());
        }
        if (entity.getCity() != null) {
            addressFound.setCity(entity.getCity());
        }
        if (entity.getCountry() != null) {
            addressFound.setCountry(entity.getCountry());
        }

        addressRepository.save(addressFound);
        return addressFound;
    }

    @Override
    public Boolean delete(Integer id) {
        try {
            Optional<Address> address = addressRepository.findById(id);
            if (address.isEmpty()) {
                return false;
            }

            Address addressFound = address.get();

            addressRepository.delete(addressFound);

        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
