package auction.data.models;
import java.time.LocalDateTime;
import java.util.List;

public class Auction {
    private String code;
    private final String name;
    private AuctionState state;
    private final double initValue;
    private double finalValue;
    private LocalDateTime endDate;
    private List<Bid> bids;

    public Auction(String code,
                   String name,
                   AuctionState state,
                   double initValue
                  ) {
        this.code = code;
        this.name = name;
        this.state = state;
        this.initValue = initValue;
    }

    public AuctionState getState() {
        return state;
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

}
