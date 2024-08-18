package auction.presentation.commands;

import auction.business.services.AuctionServices;
import auction.business.services.UserService;
import auction.data.models.AuctionState;
import auction.data.models.User;
import auction.data.persistence.AuctionPersistence;
import auction.data.persistence.UserPersistence;
import auction.presentation.commands.invoker.CommandInvoker;
import auction.presentation.interfaces.ICommand;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        init();

    }

    private static void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Criar leilão");
        System.out.println("2. Filtrar leilões inativos");
        System.out.println("3. Filtrar leilões abertos");
        System.out.println("4. Filtrar leilões finalizados");
        System.out.println("5. Filtrar leilões expirados");
        System.out.println("6. Dar lance");
        System.out.println("7. Abrir leilão");
        System.out.println("8. Finalizar leilão");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void init() {
        UserPersistence userPersistence = new UserPersistence();
        UserService userService = new UserService(userPersistence);
        AuctionPersistence auctionPersistence = new AuctionPersistence();
        AuctionServices auctionServices = new AuctionServices(auctionPersistence);
        CommandInvoker invoker = new CommandInvoker();
        Scanner scanner = new Scanner(System.in);

        User currentUser = null;

        System.out.println("Deseja usar o usuário padrão (1) ou criar um novo usuário (2)?");
        int authChoice = scanner.nextInt();
        switch (authChoice) {
            case 1:
                currentUser = userService.createUserDefaultIfNotExists();
                break;
            case 2:
                CreateUserCommand createUserCommand = new CreateUserCommand(userService);
                executeCommand(invoker, createUserCommand);
                currentUser = createUserCommand.getUserCreated();
                break;
            default:
                System.out.println("Opção inválida.");
                return;
        }

        boolean mustContinue = true;
        while (mustContinue && currentUser != null) {

            displayMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    executeCommand(invoker, new CreateAuctionCommand(auctionServices));
                    break;
                case 2:
                    executeCommand(invoker, new FilterAuctionsCommand(auctionServices, AuctionState.INACTIVE));
                    break;
                case 3:
                    executeCommand(invoker, new FilterAuctionsCommand(auctionServices, AuctionState.OPEN));
                    break;
                case 4:
                    executeCommand(invoker, new FilterAuctionsCommand(auctionServices, AuctionState.ENDED));
                    break;
                case 5:
                    executeCommand(invoker, new FilterAuctionsCommand(auctionServices, AuctionState.EXPIRED));
                    break;
                case 6:
                    executeCommand(invoker, (ICommand) new BidAnAuctionCommand(auctionServices, currentUser));
                    break;
                case 7:
                    executeCommand(invoker, new OpenAuctionCommand(auctionServices));
                    break;
                case 8:
                    executeCommand(invoker, new FinishAuctionCommand(auctionServices));
                    break;
                case 0:
                    mustContinue = false;
                    System.out.println("Encerrando o programa.");
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, tente novamente.");
            }
        }

        scanner.close();
    }

    private static void executeCommand(CommandInvoker invoker, ICommand command) {
        try {
            invoker.setCommand(command);
            invoker.invoke();
        } catch (RuntimeException e) {
            System.out.println(e);
        }

    }
}
