package auction.data.models;

import java.time.LocalDateTime;
import java.util.Date;

public class Bid implements Comparable<Bid> {
    private int id;
    private int auctionId;
    private int userId;
    private LocalDateTime date;
    private double value;

    public Bid() {
    }

    public Bid(int id, int auctionId, int userId, LocalDateTime date, double value) {
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
        this.date = date;
        this.value = value;
    }
    public Bid(LocalDateTime date, double value) {
        this.date = date;
        this.value = value;
    }

    public Bid(int id, LocalDateTime date, double value) {
        this.id = id;
        this.date = date;
        this.value = value;

        this.auctionId = 0;
        this.userId = 0;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Bid other) {
        return Double.compare(this.value, other.value);
    }


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id=" + id +
                ", auctionId=" + auctionId +
                ", userId=" + userId +
                ", date=" + date +
                ", value=" + value +
                '}';
    }
}
