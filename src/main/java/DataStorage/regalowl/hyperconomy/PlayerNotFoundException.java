package regalowl.hyperconomy;

public class PlayerNotFoundException extends Exception {
	

	private static final long serialVersionUID = 8640269022006459944L;

	public PlayerNotFoundException() {}

    //Constructor that accepts a message
    public PlayerNotFoundException(String message)
    {
       super(message);
    }
}
