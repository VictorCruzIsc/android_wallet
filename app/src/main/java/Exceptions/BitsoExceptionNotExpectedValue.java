package Exceptions;

@SuppressWarnings("serial")
public class BitsoExceptionNotExpectedValue extends RuntimeException {
    public BitsoExceptionNotExpectedValue(String message){
        super(message);
    }
}
