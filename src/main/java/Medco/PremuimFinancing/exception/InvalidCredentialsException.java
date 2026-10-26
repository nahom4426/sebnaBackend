package Medco.PremuimFinancing.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(){
        super("Invalid UserName or Password");
    }
}
