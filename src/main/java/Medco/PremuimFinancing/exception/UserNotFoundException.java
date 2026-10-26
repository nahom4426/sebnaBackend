package Medco.PremuimFinancing.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotFoundException extends RuntimeException{
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("User with ID " + userId + " not found");
        this.userId = userId;
    }
}

