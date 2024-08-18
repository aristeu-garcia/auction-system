package auction.presentation.commands;
import auction.business.services.AuctionServices;
import auction.business.services.UserService;
import auction.data.models.User;
import auction.presentation.interfaces.ICommand;

import java.util.Scanner;

public class BidAnAuctionCommand implements ICommand {

    private final AuctionServices auctionServices;
    private User user;

    public BidAnAuctionCommand(AuctionServices auctionServices, User user) {
        this.auctionServices = auctionServices;
    }
    @Override
    public void execute() {
        bidAnAuction();
    }

    private void bidAnAuction() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o codigo do leilão:");
        String code = scanner.nextLine();

        double value = 0.0;
        boolean validPrice = false;
        while (!validPrice) {
            System.out.println("Digite o valor do lance:");
            try {
                value = Double.parseDouble(scanner.nextLine());
                if (value <= 0.0) {
                    System.out.println("O valor do lance deve ser maior do que zero.");
                } else {
                    validPrice = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, insira um valor numérico válido para o valor do lance.");
            }
        }

        this.auctionServices.placeBid(code, value, this.user);
        System.out.println("Lance aplicado com sucesso!");

    }
}
