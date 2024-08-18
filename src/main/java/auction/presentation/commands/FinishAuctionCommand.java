package auction.presentation.commands;

import auction.business.services.AuctionServices;
import auction.presentation.interfaces.ICommand;
import java.util.Scanner;

public class FinishAuctionCommand implements ICommand {
    private final AuctionServices auctionServices;

    public FinishAuctionCommand(AuctionServices auctionServices) {
        this.auctionServices = auctionServices;
    }
    @Override
    public void execute() {
        finishAuction();
    }

    private void finishAuction() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o código do leilão:");
        String code = scanner.nextLine();

        this.auctionServices.finish(code);

        System.out.println("Leilão finalizado com sucesso!");
    }

}
