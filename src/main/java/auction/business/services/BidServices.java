package auction.business.services;

import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.models.Bid;
import auction.data.models.User;
import auction.data.persistence.AuctionPersistence;
import auction.data.persistence.BidPersistence;
import auction.data.persistence.UserPersistence;

import java.time.LocalDateTime;
import java.util.*;

public class BidServices {
    private AuctionPersistence auctionPersistence;
    private BidPersistence bidPersistence;
    private UserPersistence userPersistence;


    public BidServices(
            BidPersistence bidPersistence,
            UserPersistence userPersistence,
            AuctionPersistence auctionPersistence

    ) {
        this.bidPersistence = bidPersistence;
        this.userPersistence = userPersistence;
        this.auctionPersistence = auctionPersistence;
    }



    public List<Bid> getOrderedDescBids(int auctionId){
        List<Bid> bids = this.bidPersistence.getBidsByAuctionId(auctionId);
        return bids;
    }

    public List<Bid> getOrderedAscBids(int auctionId){
        List<Bid> bids = this.bidPersistence.getBidsByAuctionId(auctionId, "ASC");
        return bids;
    }
    public List<Bid> getHighestAndLowestBid(Auction auction) {
        List<Bid> bids = getOrderedDescBids(auction.getId());

        if (bids == null || bids.isEmpty()) {
            return new ArrayList<>();
        } else {
            Bid highestBid = bids.get(0);
            Bid lowestBid = bids.get(bids.size() - 1);

            List<Bid> newBids = new ArrayList<>(2);
            newBids.add(highestBid);
            newBids.add(lowestBid);

            return newBids;
        }
    }

    private Bid getLastBid(Auction auction){
        List<Bid> bids = auction.getBids();

        if (bids == null || bids.isEmpty()) {
            return null;
        }

        return bids.get(bids.size() - 1);
    }

    public void placeBid(String code, Double value, User user) {
        Optional<Auction> auctionOptional = this.auctionPersistence.findByCode(code);

        Auction auction = auctionOptional.orElseThrow(() -> new RuntimeException("Auction not found"));

        Optional<User> userOptional = this.userPersistence.findById(user.getId());

        User userFound = userOptional.orElseThrow(()-> new RuntimeException("User not found"));

        if(auction.getState() != AuctionState.OPEN){
            throw new RuntimeException("Auction must be OPEN");
        }

        if (value <= auction.getInitValue()) {
            throw new RuntimeException("Value must be greater than the initial bid");
        }

        Bid lastBid = this.getLastBid(auction);


        if (lastBid != null && lastBid.getValue() >= value) {
            throw new RuntimeException("Value must be greater than the last bid");
        }

        if (lastBid != null) {
            int lastBidUserId = lastBid.getUserId();
            if (lastBidUserId == user.getId()) {
                throw new RuntimeException("User must not be the same as the last user");
            }
        }

        Bid bid = new Bid(LocalDateTime.now(), value);
        bid.setUserId(userFound.getId());
        bid.setAuctionId(auction.getId());
        this.bidPersistence.create(bid);
    }
}
