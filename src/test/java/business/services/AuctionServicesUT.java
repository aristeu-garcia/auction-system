package business.services;
import auction.business.services.AuctionServices;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.persistence.AuctionPersistence;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
public class AuctionServicesUT {
//    @Test
//    public void shouldFilterAuctionsByOpenState() {
//        AuctionServices auctionPersistence = new AuctionPersistence();
//        AuctionServices auctionServices = new AuctionServices(auctionPersistence);
//        Auction newAuction = auctionServices.init("PS5", 1000);
//        ArrayList<Auction> listAuction = new ArrayList<>();
//        listAuction.add(newAuction);
//
//        ArrayList<Auction> filteredList = auctionServices.filterByState(listAuction, AuctionState.OPEN);
//        assertEquals(1, filteredList.size());
//        assertEquals(newAuction, filteredList.get(0));
//    }
}
