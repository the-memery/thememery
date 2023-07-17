package com.quest.etna.service;

import com.quest.etna.model.Address;
import com.quest.etna.model.User;
import com.quest.etna.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements iModelService<User> {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List getList() {
        return userRepository.getAll();
    }

    @Override
    public User getOneById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    public User getOneByUsername(String username) {
        Optional<User> user = Optional.ofNullable(userRepository.getByUsername(username));
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    @Override
    public User create(User entity) {
        userRepository.save(entity);
        return entity;
    }

    @Override
    public User update(Integer id, User entity) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return null;
        }

        User userFound = user.get();
        if (entity.getUsername() != null) {
            userFound.setUsername(entity.getUsername());
        }
        if (entity.getRole() != null) {
            userFound.setRole(entity.getRole());
        }
        userRepository.save(userFound);

        return userFound;
    }

    @Override
    public Boolean delete(Integer id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                return false;
            }

            User userFound = user.get();

            userRepository.delete(userFound);

        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
