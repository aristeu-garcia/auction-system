package auction.data.models;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Auction {
    private int id;
    private String code;
    private final String name;
    private AuctionState state;
    private final double initValue;
    private double finalValue;
    private LocalDateTime endDate;
    private List<Bid> bids;

    private User winner;

    public Auction(String code,
                   String name,
                   AuctionState state,
                   double initValue,
                   LocalDateTime endDate
                  ) {
        this.code = code;
        this.name = name;
        this.state = state;
        this.initValue = initValue;
        this.endDate = endDate;
        this.bids = new ArrayList<>();
    }

    public double getInitValue() {
        return initValue;
    }

    public AuctionState getState() {

        this.checkExpiration();

        return state;
    }

    public User getWinner(){
        return this.winner;
    }
    public void setWinner(User user){
        this.winner = user;
    }
    public void setState(AuctionState state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public double getFinalValue() {
        return finalValue;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public List<Bid> getBids() {
        return this.bids;
    }

    public void checkExpiration() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(this.endDate) && this.state != AuctionState.ENDED) {
            this.setState(AuctionState.EXPIRED);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", initValue=" + initValue +
                ", finalValue=" + finalValue +
                ", endDate=" + (endDate != null ? endDate.toString() : "N/A") +
                ", bids=" + (bids != null ? bids.size() : "N/A") +
                '}';
    }

    public void setFinalValue(double finalValue) {
        this.finalValue = finalValue;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }
}
