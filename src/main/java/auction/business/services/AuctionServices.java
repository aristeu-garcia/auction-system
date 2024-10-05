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

public class AuctionServices {

    private AuctionPersistence auctionPersistence;
    private BidPersistence bidPersistence;
    private UserPersistence userPersistence;


    public AuctionServices(
            AuctionPersistence auctionPersistence,
            BidPersistence bidPersistence,
            UserPersistence userPersistence

    ) {
        this.auctionPersistence = auctionPersistence;
        this.bidPersistence = bidPersistence;
        this.userPersistence = userPersistence;
    }

    public Auction create(String name, double initValue) {
        Auction auction = new Auction(generateCode(), name, AuctionState.INACTIVE, initValue, LocalDateTime.now().plusHours(4));
        this.auctionPersistence.create(auction);
        return auction;
    }
    public void open(String code) {
        this.auctionPersistence.updateStatus(code, AuctionState.OPEN);
    }

    public boolean finish(String code) {
        Auction auction = findByCode(code)
                .orElseThrow(() -> new RuntimeException("Auction not found to finish"));

        Bid winningBid = auction.getBids().stream()
                .max((bid1, bid2) -> Double.compare(bid1.getValue(), bid2.getValue()))
                .orElse(null);

        if (winningBid != null) {
            Optional<User> winner = this.userPersistence.findById(winningBid.getUserId());
            if(winner.isEmpty()){
                throw new RuntimeException("Winner not found");
            }
            auction.setWinner(winner.get());
            System.out.println("Sending congratulations message to: " + winner.get().getEmail());
        } else {
            System.out.println("No bids were placed on this auction.");
        }

        auction.setState(AuctionState.ENDED);
        return true;
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

    public List<Auction> getAll() {
        List<Auction> auctions = auctionPersistence.getAuctionsList();
        if(auctions.isEmpty()){
            System.out.println("No auctions found");
        }
        return auctions;
    }


    public Optional<Auction> findByCode(String code) {
        Optional<Auction> auctionOpt = auctionPersistence.findByCode(code);
        if (auctionOpt.isEmpty()) {
            System.out.println("Auction with code " + code + " not found.");
        }
        return auctionOpt;
    }
}
