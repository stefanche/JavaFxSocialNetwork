package com.example.demo.domain.validators;

import com.example.demo.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getName().isEmpty())
            throw new ValidationException("Numele nu e valid");
        if (entity.getID() <= 0)
            throw new ValidationException("Id canot be 0 or negative");
    }
}
