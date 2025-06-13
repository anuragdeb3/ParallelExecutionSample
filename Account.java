public class Account {
    public String accountId;
    public String accountName;
    public String mandateRef; // Add more fields as per sheet structure
    public int rowIndex; // For tracking Excel row update

    public Account(String accountId, String accountName, String mandateRef, int rowIndex) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.mandateRef = mandateRef;
        this.rowIndex = rowIndex;
    }

    @Override
    public String toString() {
        return accountId + " - " + accountName + " - " + mandateRef;
    }
}
