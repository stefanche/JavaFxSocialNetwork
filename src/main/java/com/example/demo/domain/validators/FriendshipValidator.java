package com.example.demo.domain.validators;

import com.example.demo.Repository.database.UserDbRepo;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        Tuple<Integer> friendship = entity.getID();
        if (friendship.getFrom()<0) {
            throw new ValidationException("From_user should be greater than 0");
        }
        if (friendship.getTo()<0) {
            throw new ValidationException("To_user should be greater than 0");
        }
    }
}
