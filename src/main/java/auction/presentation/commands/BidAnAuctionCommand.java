package auction.presentation.commands;
import auction.business.services.AuctionServices;
import auction.business.services.BidServices;
import auction.data.models.User;
import auction.presentation.interfaces.ICommand;

import java.util.Scanner;

public class BidAnAuctionCommand implements ICommand {

    private final BidServices bidServices;
    private User user;

    public BidAnAuctionCommand(BidServices bidServices, User user) {
        this.bidServices = bidServices;
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

        this.bidServices.placeBid(code, value, this.user);
        System.out.println("Lance aplicado com sucesso!");

    }
}
