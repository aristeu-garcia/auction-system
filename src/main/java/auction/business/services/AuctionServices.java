package auction.business.services;

import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.models.Bid;
import auction.data.models.User;
import auction.data.persistence.AuctionPersistence;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

public class AuctionServices {

    private AuctionPersistence auctionPersistence;


    public AuctionServices(AuctionPersistence auctionPersistence) {
        this.auctionPersistence = auctionPersistence;
    }

    public Auction create(String name, double initValue) {
        Auction auction = new Auction(generateCode(), name, AuctionState.INACTIVE, initValue, LocalDateTime.now().plusHours(4));
        this.auctionPersistence.create(auction);
        return auction;
    }
    public boolean open(String code) {
        Auction auction = findByCode(code)
                .orElseThrow(() -> new RuntimeException("Auction not found to open"));
        auction.setState(AuctionState.OPEN);
        return true;
    }

    public boolean finish(String code) {
        Auction auction = findByCode(code)
                .orElseThrow(() -> new RuntimeException("Auction not found to finish"));

        Bid winningBid = auction.getBids().stream()
                .max((bid1, bid2) -> Double.compare(bid1.getValue(), bid2.getValue()))
                .orElse(null);

        if (winningBid != null) {
            User winner = winningBid.getUser();
            auction.setWinner(winner);
            System.out.println("Sending congratulations message to: " + winner.getEmail());
        } else {
            System.out.println("No bids were placed on this auction.");
        }

        auction.setState(AuctionState.ENDED);
        return true;
    }
    public List<Bid> getOrderedBids(Auction auction){
        List<Bid> bids = auction.getBids();
        bids.sort(Comparator.reverseOrder());
        return bids;
    }
    public List<Bid> getHighestAndLowestBid(Auction auction) {
        List<Bid> bids = getOrderedBids(auction);

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


    private static String generateCode() {
        int size = 10;
        String token = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            char charCreated = token.charAt(random.nextInt(token.length()));
            code.append(charCreated);
        }
        return code.toString();
    }

    public List<Auction> filterByState(AuctionState state) {
        return auctionPersistence.getAuctionsList().stream()
                .filter(auction -> auction.getState().equals(state))
                .toList();
    }

    private Bid getLastBid(Auction auction){
        List<Bid> bids = auction.getBids();

        if (bids == null || bids.isEmpty()) {
            return null;
        }

        return bids.get(bids.size() - 1);
    }

    public Optional<Auction> findByCode(String code) {
        Optional<Auction> auctionOpt = auctionPersistence.findByCode(code);
        if (auctionOpt.isEmpty()) {
            System.out.println("Auction with code " + code + " not found.");
        }
        return auctionOpt;
    }

    public void placeBid(String code, Double value, User user) {
        Optional<Auction> auctionOptional = findByCode(code);

        Auction auction = auctionOptional.orElseThrow(() -> new RuntimeException("Auction not found"));

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
            User lastBidUser = lastBid.getUser();
            if (lastBidUser != null && lastBidUser.equals(user)) {
                throw new RuntimeException("User must not be the same as the last user");
            }
        }

        Bid bid = new Bid(user,LocalDateTime.now(), value);
        auction.getBids().add(bid);

    }
}
