package auction.business.services;

import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.persistence.AuctionPersistence;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AuctionServices {

    private AuctionPersistence auctionPersistence;


    public AuctionServices(AuctionPersistence auctionPersistence) {
        this.auctionPersistence = auctionPersistence;
    }

    public Auction create(String name, double initValue) {
        Auction auction = new Auction(generateCode(), name, AuctionState.INACTIVE, initValue);
        auctionPersistence.getAuctionsList().add(auction);
        return auction;
    }
    public boolean open(String code) {
        Auction auction = findByCode(code)
                .orElseThrow(() -> new RuntimeException("Auction not found to open"));
        auction.setState(AuctionState.OPEN);
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
    public Optional<Auction> findByCode(String code) {
        return auctionPersistence.getAuctionsList().stream()
                .filter(auction -> auction.getCode().equals(code))
                .findFirst();
    }


}
