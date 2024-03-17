package auction.presentation.commands;

import auction.business.services.AuctionServices;
import auction.presentation.interfaces.ICommand;

import java.util.Scanner;

public class OpenAuctionCommand implements ICommand {
    private final AuctionServices auctionServices;

    public OpenAuctionCommand(AuctionServices auctionServices) {
        this.auctionServices = auctionServices;
    }
    @Override
    public void execute() {
        openAuction();
    }

    private void openAuction() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o codigo do leilão:");
        String code = scanner.nextLine();

        try {
            this.auctionServices.open(code);
            System.out.println("Leilão aberto com sucesso!");
        } catch (RuntimeException e) {
            System.out.println("Erro ao abrir o leilão: " + e.getMessage());
        }
    }


}
