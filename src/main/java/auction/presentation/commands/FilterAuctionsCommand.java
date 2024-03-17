package auction.presentation.commands;

import auction.business.services.AuctionServices;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.presentation.interfaces.ICommand;
import java.util.List;

public class FilterAuctionsCommand implements ICommand {
    private final AuctionServices auctionServices;
    private final AuctionState state;

    public FilterAuctionsCommand(AuctionServices auctionServices, AuctionState state) {
        this.auctionServices = auctionServices;
        this.state = state;
    }

    @Override
    public void execute() {
        List<Auction> auctions = auctionServices.filterByState(state);
        System.out.println(auctions);
    }
}
