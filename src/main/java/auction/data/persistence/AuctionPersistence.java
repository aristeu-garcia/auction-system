package auction.data.persistence;

import auction.data.models.Auction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuctionPersistence {
    private List<Auction> auctionsList;

    public AuctionPersistence() {
        this.auctionsList = new ArrayList<>();
    }

    public List<Auction> getAuctionsList() {
        return auctionsList;
    }



    public void setAuctionsList(List<Auction> auctionsList) {
        this.auctionsList = auctionsList;
    }

    @Override
    public String toString() {
        return "AuctionPersistence{" +
                "auctionsList=" + auctionsList +
                '}';
    }
}
