package com.example.demo.domain.validators;

import com.example.demo.domain.Message;

public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message entity) throws ValidationException {
        if (entity.getMessage().isEmpty()){
            throw new ValidationException("Message is empty");
        } else if (entity.getMessage().length() > 255){
            throw new ValidationException("Message is too long");
        } else if (entity.getMessage().length() < 3){
            throw new ValidationException("Message is too short");
        } else if (entity.getTo().isEmpty()) {
            throw new ValidationException("To is empty");
        }


    }
}
