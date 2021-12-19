package de.likewhat.customheads.economy;

public class EconomyCallback {

    private final double amount;
    private final boolean success;
    private final String responseMessage;

    public EconomyCallback(double amount, boolean success, String responseMessage) {
        this.amount = amount;
        this.success = success;
        this.responseMessage = responseMessage;
    }

    /**
     * The original Amount to be deducted by the Caller
     * @return Amount from Caller
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Whether the Transaction was a success or not
     * @return Boolean
     */
    public boolean wasSuccess() {
        return success;
    }

    /**
     * Response from the Handler
     * May be a failed or successful Response
     * @return Response
     */
    public String getResponseMessage() {
        return responseMessage;
    }
}
