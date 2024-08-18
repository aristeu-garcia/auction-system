package auction.data.models;

import java.time.LocalDateTime;
import java.util.Date;

public class Bid implements Comparable<Bid> {
    private User user;
    private LocalDateTime date;
    private double value;

    public Bid(User user, LocalDateTime date, double value) {
        this.user = user;
        this.date = date;
        this.value = value;
    }

    @Override
    public int compareTo(Bid other) {
        return Double.compare(this.value, other.value);
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "user=" + user +
                ", date=" + date +
                ", value=" + value +
                '}';
    }
}
